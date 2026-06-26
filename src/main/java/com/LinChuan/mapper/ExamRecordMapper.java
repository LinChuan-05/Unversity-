package com.LinChuan.mapper;

import com.LinChuan.entity.ExamRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExamRecordMapper {

    @Select("SELECT COUNT(DISTINCT examTime) FROM exam_record WHERE userId=#{userId} AND examId=#{examId}")
    int countAttempts(@Param("userId") Integer userId, @Param("examId") Integer examId);

    @Insert("INSERT INTO exam_record(userId, questionId, userAnswer, isCorrect, score, examId, review_status, manual_score) VALUES(#{userId}, #{questionId}, #{userAnswer}, #{isCorrect}, #{score}, #{examId}, #{reviewStatus}, #{manualScore})")
    @Options(useGeneratedKeys = true, keyProperty = "recordId")
    int insert(ExamRecord record);

    @Select("SELECT * FROM exam_record WHERE userId = #{userId} ORDER BY examTime DESC")
    List<ExamRecord> findByUserId(@Param("userId") Integer userId);

    @Select("SELECT e.*, q.title, q.answer as correctAnswer " +
            "FROM exam_record e LEFT JOIN question q ON e.questionId = q.questionId " +
            "WHERE e.userId = #{userId} ORDER BY e.examTime DESC")
    List<Map<String, Object>> findDetailByUserId(@Param("userId") Integer userId);

    @Select("SELECT userId, SUM(score) as totalScore, COUNT(*) as questionCount, MAX(examTime) as lastExamTime " +
            "FROM exam_record GROUP BY userId ORDER BY lastExamTime DESC")
    List<Map<String, Object>> findAllUserScores();

    /** 学生个人成绩汇总：按考试场次分组，含批阅状态 */
    @Select("SELECT er.examTime, er.examId, ex.name as examName, " +
            "COALESCE(SUM(er.score), 0) + COALESCE(SUM(er.manual_score), 0) as totalScore, " +
            "SUM(CASE WHEN er.review_status = 1 THEN 1 ELSE 0 END) as pendingCount, " +
            "COUNT(*) as questionCount, MAX(ex.duration) as duration " +
            "FROM exam_record er LEFT JOIN exam ex ON er.examId = ex.examId " +
            "WHERE er.userId = #{userId} " +
            "GROUP BY er.examTime, er.examId, ex.name " +
            "ORDER BY er.examTime DESC")
    List<Map<String, Object>> findSummaryByUserId(@Param("userId") Integer userId);

    /** 管理员：查看所有学生的考试状态汇总 */
    @Select("SELECT u.userId, u.userName, ex.examId, ex.name as examName, ex.maxAttempts, " +
            "COUNT(DISTINCT er.examTime) as taken, MAX(er.examTime) as lastExamTime " +
            "FROM users u CROSS JOIN exam ex " +
            "LEFT JOIN exam_record er ON er.userId = u.userId AND er.examId = ex.examId " +
            "WHERE u.role = 'student' " +
            "GROUP BY u.userId, u.userName, ex.examId, ex.name, ex.maxAttempts " +
            "ORDER BY u.userName, ex.name")
    List<Map<String, Object>> findAllStudentExamStatus();

    /** 管理员：清除某个学生对某科目的所有考试记录（允许重考） */
    @Delete("DELETE FROM exam_record WHERE userId = #{userId} AND examId = #{examId}")
    int deleteByUserIdAndExamId(@Param("userId") Integer userId, @Param("examId") Integer examId);

    /** 学生端：查询某学生的所有错题（isCorrect=0），关联试题详情，按科目分组 */
    @Select("SELECT er.recordId, er.userAnswer, er.examTime, er.examId, ex.name as examName, " +
            "q.questionId, q.title, q.optionA, q.optionB, q.optionC, q.optionD, q.answer as correctAnswer, q.score as questionScore " +
            "FROM exam_record er " +
            "LEFT JOIN question q ON er.questionId = q.questionId " +
            "LEFT JOIN exam ex ON er.examId = ex.examId " +
            "WHERE er.userId = #{userId} AND er.isCorrect = 0 AND er.review_status != 1 " +
            "ORDER BY ex.name, er.examTime DESC")
    List<Map<String, Object>> findWrongAnswers(@Param("userId") Integer userId);

    /** 管理员：获取所有待批阅的简答题 */
    @Select("SELECT er.recordId, er.userId, er.questionId, er.userAnswer, er.examId, er.examTime, " +
            "u.userName, q.title, q.answer as referenceAnswer, q.score as maxScore, ex.name as examName " +
            "FROM exam_record er " +
            "LEFT JOIN users u ON er.userId = u.userId " +
            "LEFT JOIN question q ON er.questionId = q.questionId " +
            "LEFT JOIN exam ex ON er.examId = ex.examId " +
            "WHERE er.review_status = 1 ORDER BY er.examTime DESC")
    List<Map<String, Object>> findPendingReviews();

    /** 管理员：批阅打分 */
    @Update("UPDATE exam_record SET manual_score = #{score}, review_status = 2 WHERE recordId = #{recordId}")
    int updateManualScore(@Param("recordId") Integer recordId, @Param("score") Integer score);
}
