package com.lixianda.service;

import com.lixianda.entity.Users;
import com.lixianda.mapper.UserMapper;
import com.lixianda.util.BCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public Users login(String userName, String rawPassword) {
        Users user = userMapper.login(userName, rawPassword);
        if (user == null) return null;
        // BCrypt 密文验证
        if (BCryptUtil.isBCrypt(user.getPassword())) {
            return BCryptUtil.matches(rawPassword, user.getPassword()) ? user : null;
        }
        // 兼容老明文密码，验证后自动升级
        if (rawPassword.equals(user.getPassword())) {
            userMapper.updatePassword(user.getUserId(), BCryptUtil.encode(rawPassword));
            return user;
        }
        return null;
    }

    public int register(Users user) {
        if (user.getRole() == null || user.getRole().isEmpty()) user.setRole("student");
        if (user.getStatus() == null) user.setStatus(1);
        user.setPassword(BCryptUtil.encode(user.getPassword()));
        return userMapper.insert(user);
    }

    public List<Users> findAll() { return userMapper.findAll(); }

    public int delete(Integer userId, Users currentUser) {
        if (currentUser != null && currentUser.getUserId().equals(userId)) return -1;
        userMapper.deleteRecordsByUserId(userId);
        userMapper.deleteRequestsByUserId(userId);
        return userMapper.deleteById(userId);
    }

    public boolean isAdmin(Users user) { return user != null && "admin".equals(user.getRole()); }
    public List<Map<String, Object>> findAllClasses() { return userMapper.findAllClasses(); }
    public List<Map<String, Object>> findClassStudentsWithScores(String className) { return userMapper.findClassStudentsWithScores(className); }

    public int createClass(String className) {
        List<Map<String, Object>> existing = userMapper.findAllClasses();
        for (Map<String, Object> c : existing) {
            if (className.equals(c.get("class_name"))) return -1;
        }
        return userMapper.insertClass(className);
    }

    public List<Users> findUnassignedStudents() { return userMapper.findUnassignedStudents(); }

    public void assignClass(Integer userId, Integer classId) { userMapper.assignClass(userId, classId); }
}
