package com.lixianda.mapper;

import com.lixianda.entity.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionMapper {

    @Insert("INSERT INTO question(title, optionA, optionB, optionC, optionD, answer, examId, score) VALUES(#{title}, #{optionA}, #{optionB}, #{optionC}, #{optionD}, #{answer}, #{examId}, #{score})")
    @Options(useGeneratedKeys = true, keyProperty = "questionId")
    int insert(Question question);

    @Results(id = "questionMap", value = {
        @Result(property = "questionId", column = "questionId"),
        @Result(property = "title", column = "title"),
        @Result(property = "optionA", column = "optionA"),
        @Result(property = "optionB", column = "optionB"),
        @Result(property = "optionC", column = "optionC"),
        @Result(property = "optionD", column = "optionD"),
        @Result(property = "answer", column = "answer"),
        @Result(property = "examId", column = "examId"),
        @Result(property = "score", column = "score")
    })
    @Select("SELECT questionId, title, optionA, optionB, optionC, optionD, answer, examId, score FROM question ORDER BY questionId")
    List<Question> findAll();

    @ResultMap("questionMap")
    @Select("SELECT questionId, title, optionA, optionB, optionC, optionD, answer, examId, score FROM question WHERE examId = #{examId} ORDER BY questionId")
    List<Question> findByExamId(@Param("examId") Integer examId);

    @ResultMap("questionMap")
    @Select("SELECT questionId, title, optionA, optionB, optionC, optionD, answer, examId, score FROM question WHERE questionId = #{questionId} LIMIT 1")
    Question findById(@Param("questionId") Integer questionId);

    @ResultMap("questionMap")
    @Select("SELECT questionId, title, optionA, optionB, optionC, optionD, answer, examId, score FROM question WHERE examId = #{examId} ORDER BY RAND() LIMIT #{limit}")
    List<Question> findRandByExamId(@Param("examId") Integer examId, @Param("limit") int limit);

    @Delete("DELETE FROM question WHERE questionId = #{questionId}")
    int deleteById(@Param("questionId") Integer questionId);

    @Update("UPDATE question SET title=#{title}, optionA=#{optionA}, optionB=#{optionB}, optionC=#{optionC}, optionD=#{optionD}, answer=#{answer}, examId=#{examId}, score=#{score} WHERE questionId=#{questionId}")
    int update(Question question);
}
