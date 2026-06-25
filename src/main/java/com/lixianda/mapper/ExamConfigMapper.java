package com.lixianda.mapper;

import com.lixianda.entity.ExamConfig;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ExamConfigMapper {

    @Select("SELECT * FROM exam_config WHERE id = 1 LIMIT 1")
    ExamConfig get();

    @Update("UPDATE exam_config SET duration = #{duration} WHERE id = 1")
    int update(@Param("duration") Integer duration);

    @Insert("INSERT INTO exam_config (id, duration) VALUES (1, #{duration}) ON DUPLICATE KEY UPDATE duration = #{duration}")
    int upsert(@Param("duration") Integer duration);
}
