package com.LinChuan.mapper;

import com.LinChuan.entity.Users;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE userName = #{userName} AND status = 1 LIMIT 1")
    // 密码在 Service 层用 BCrypt 验证
    @Results({
        @Result(property = "userId", column = "userId"),
        @Result(property = "userName", column = "userName"),
        @Result(property = "password", column = "password"),
        @Result(property = "realName", column = "real_name"),
        @Result(property = "sex", column = "sex"),
        @Result(property = "email", column = "email"),
        @Result(property = "role", column = "role"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "status", column = "status"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "classId", column = "class_id")
    })
    Users login(@Param("userName") String userName, @Param("password") String password);

    @Insert("INSERT INTO users(userName, password, real_name, sex, email, role, phone, status, class_id) VALUES(#{userName}, #{password}, #{realName}, #{sex}, #{email}, #{role}, #{phone}, 1, #{classId})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(Users user);

    @Select("SELECT u.*, c.class_name FROM users u LEFT JOIN sys_class c ON u.class_id = c.class_id WHERE u.role = 'student' ORDER BY u.userId")
    List<Users> findAll();

    @Delete("DELETE FROM users WHERE userId = #{userId}")
    int deleteById(@Param("userId") Integer userId);

    @Delete("DELETE FROM exam_record WHERE userId = #{userId}")
    int deleteRecordsByUserId(@Param("userId") Integer userId);

    @Delete("DELETE FROM reset_request WHERE userId = #{userId}")
    int deleteRequestsByUserId(@Param("userId") Integer userId);

    @Select("SELECT * FROM sys_class ORDER BY class_id")
    List<Map<String, Object>> findAllClasses();

    @Select("SELECT u.userId, u.userName, u.real_name as realName, u.sex, u.email, c.class_name as className, " +
            "ex.examId, ex.name as examName, " +
            "COALESCE(SUM(er.score), 0) + COALESCE(SUM(er.manual_score), 0) as totalScore, COUNT(er.recordId) as answerCount, MAX(er.examTime) as lastExamTime " +
            "FROM users u CROSS JOIN exam ex " +
            "LEFT JOIN exam_record er ON er.userId = u.userId AND er.examId = ex.examId " +
            "LEFT JOIN sys_class c ON u.class_id = c.class_id " +
            "WHERE u.role = 'student' AND u.class_id = (SELECT class_id FROM sys_class WHERE class_name = #{className} LIMIT 1) " +
            "GROUP BY u.userId, u.userName, u.real_name, u.sex, u.email, c.class_name, ex.examId, ex.name " +
            "ORDER BY u.userName, ex.name")
    List<Map<String, Object>> findClassStudentsWithScores(@Param("className") String className);

    @Update("UPDATE users SET password = #{password} WHERE userId = #{userId}")
    int updatePassword(@Param("userId") Integer userId, @Param("password") String password);

    @Insert("INSERT INTO sys_class (class_name) VALUES (#{className})")
    int insertClass(@Param("className") String className);

    @Select("SELECT * FROM users WHERE role = 'student' AND (class_id IS NULL OR class_id = 0)")
    List<Users> findUnassignedStudents();

    @Update("UPDATE users SET class_id = #{classId} WHERE userId = #{userId}")
    int assignClass(@Param("userId") Integer userId, @Param("classId") Integer classId);

    @Update("UPDATE users SET class_id = NULL WHERE class_id = #{classId}")
    int unassignStudentsByClassId(@Param("classId") Integer classId);

    @Delete("DELETE FROM sys_class WHERE class_id = #{classId}")
    int deleteClass(@Param("classId") Integer classId);
}
