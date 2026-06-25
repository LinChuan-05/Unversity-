package com.lixianda.service;

import com.lixianda.entity.ExamRecord;
import com.lixianda.mapper.ExamRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ExamRecordService {

    @Autowired
    private ExamRecordMapper examRecordMapper;

    public int save(ExamRecord record) {
        return examRecordMapper.insert(record);
    }

    public List<ExamRecord> findByUserId(Integer userId) {
        return examRecordMapper.findByUserId(userId);
    }

    public List<Map<String, Object>> findDetailByUserId(Integer userId) {
        return examRecordMapper.findDetailByUserId(userId);
    }

    public List<Map<String, Object>> findAllUserScores() {
        return examRecordMapper.findAllUserScores();
    }

    /** 查询用户某科目的已参加次数 */
    public int countAttempts(Integer userId, Integer examId) {
        return examRecordMapper.countAttempts(userId, examId);
    }

    /** 成绩汇总：按考试场次分组 */
    public List<Map<String, Object>> findSummaryByUserId(Integer userId) {
        return examRecordMapper.findSummaryByUserId(userId);
    }

    /** 管理员：查看所有学生的考试状态 */
    public List<Map<String, Object>> findAllStudentExamStatus() {
        return examRecordMapper.findAllStudentExamStatus();
    }

    /** 管理员：清除某学生对某科目的记录，允许重考 */
    public int resetAttempts(Integer userId, Integer examId) {
        return examRecordMapper.deleteByUserIdAndExamId(userId, examId);
    }

    /** 学生端：查询错题集 */
    public List<Map<String, Object>> findWrongAnswers(Integer userId) {
        return examRecordMapper.findWrongAnswers(userId);
    }

    /** 管理员：待批阅简答题 */
    public List<Map<String, Object>> findPendingReviews() {
        return examRecordMapper.findPendingReviews();
    }

    /** 管理员：批阅打分 */
    public void updateManualScore(Integer recordId, Integer score) {
        examRecordMapper.updateManualScore(recordId, score);
    }
}
