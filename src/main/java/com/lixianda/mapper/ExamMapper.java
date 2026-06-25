package com.lixianda.mapper;

import com.lixianda.entity.Exam;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExamMapper {

    @Select("SELECT * FROM exam ORDER BY examId")
    List<Exam> findAll();

    @Select("SELECT * FROM exam WHERE examId = #{examId} LIMIT 1")
    Exam findById(@Param("examId") Integer examId);

    @Insert("INSERT INTO exam(name, duration, description, maxAttempts, questionCount) VALUES(#{name}, #{duration}, #{description}, #{maxAttempts}, #{questionCount})")
    @Options(useGeneratedKeys = true, keyProperty = "examId")
    int insert(Exam exam);

    @Update("UPDATE exam SET name=#{name}, duration=#{duration}, description=#{description}, maxAttempts=#{maxAttempts}, questionCount=#{questionCount} WHERE examId=#{examId}")
    int update(Exam exam);

    @Delete("DELETE FROM exam WHERE examId = #{examId}")
    int deleteById(@Param("examId") Integer examId);
}
