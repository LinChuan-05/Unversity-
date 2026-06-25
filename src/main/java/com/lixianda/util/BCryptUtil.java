package com.lixianda.util;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * BCrypt 密码工具 — 静态方法，无需 Spring 注入
 */
public class BCryptUtil {

    public static String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        try {
            return BCrypt.checkpw(rawPassword, encodedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    /** 是否为 BCrypt 密文 */
    public static boolean isBCrypt(String password) {
        return password != null && password.startsWith("$2");
    }
}
