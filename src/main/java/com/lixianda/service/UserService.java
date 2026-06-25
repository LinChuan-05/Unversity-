package com.lixianda.service;

import com.lixianda.entity.Users;
import com.lixianda.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public Users login(String userName, String password) {
        return userMapper.login(userName, password);
    }

    public int register(Users user) {
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("student");
        }
        return userMapper.insert(user);
    }

    public List<Users> findAll() {
        return userMapper.findAll();
    }

    public int delete(Integer userId, Users currentUser) {
        if (currentUser != null && currentUser.getUserId().equals(userId)) {
            return -1;
        }
        // 级联删除关联数据
        userMapper.deleteRecordsByUserId(userId);
        userMapper.deleteRequestsByUserId(userId);
        return userMapper.deleteById(userId);
    }

    public boolean isAdmin(Users user) {
        return user != null && "admin".equals(user.getRole());
    }
}
