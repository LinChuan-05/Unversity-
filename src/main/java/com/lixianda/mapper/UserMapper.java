package com.lixianda.mapper;

import com.lixianda.entity.Users;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE userName = #{userName} AND password = #{password} LIMIT 1")
    @Results({
        @Result(property = "userId", column = "userId"),
        @Result(property = "userName", column = "userName"),
        @Result(property = "password", column = "password"),
        @Result(property = "sex", column = "sex"),
        @Result(property = "email", column = "email"),
        @Result(property = "role", column = "role")
    })
    Users login(@Param("userName") String userName, @Param("password") String password);

    @Insert("INSERT INTO users(userName, password, sex, email, role) VALUES(#{userName}, #{password}, #{sex}, #{email}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(Users user);

    @Select("SELECT * FROM users")
    List<Users> findAll();

    @Delete("DELETE FROM users WHERE userId = #{userId}")
    int deleteById(@Param("userId") Integer userId);
}
