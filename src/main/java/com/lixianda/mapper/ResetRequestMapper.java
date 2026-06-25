package com.lixianda.mapper;

import com.lixianda.entity.ResetRequest;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface ResetRequestMapper {

    @Insert({"INSERT INTO reset_request(userId, examId, score, reason, status) ",
             "VALUES(#{userId}, #{examId}, #{score}, #{reason}, 'pending')"})
    @Options(useGeneratedKeys = true, keyProperty = "requestId")
    int insert(ResetRequest req);

    @Select("SELECT r.*, e.name as examName FROM reset_request r LEFT JOIN exam e ON r.examId = e.examId WHERE r.userId = #{userId} ORDER BY r.createdAt DESC")
    List<Map<String, Object>> findByUserId(@Param("userId") Integer userId);

    @Select({"SELECT r.requestId, r.userId, r.examId, r.score, r.reason, r.status, r.createdAt, ",
             "u.userName, e.name as examName ",
             "FROM reset_request r ",
             "LEFT JOIN users u ON r.userId = u.userId ",
             "LEFT JOIN exam e ON r.examId = e.examId ",
             "ORDER BY r.status ASC, r.createdAt DESC"})
    List<Map<String, Object>> findAllRequests();

    @Update("UPDATE reset_request SET status = #{status} WHERE requestId = #{requestId}")
    int updateStatus(@Param("requestId") Integer requestId, @Param("status") String status);

    @Select("SELECT * FROM reset_request WHERE userId = #{userId} AND examId = #{examId} AND status = 'pending' LIMIT 1")
    ResetRequest findPending(@Param("userId") Integer userId, @Param("examId") Integer examId);
}
