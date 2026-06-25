-- ============================================
-- University 在线考试管理系统 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS bjpowernode DEFAULT CHARACTER SET utf8mb4;
USE bjpowernode;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS users (
    userId   INT AUTO_INCREMENT PRIMARY KEY,
    userName VARCHAR(50),
    password VARCHAR(50),
    sex      VARCHAR(10),
    email    VARCHAR(100),
    role     VARCHAR(20) DEFAULT 'student'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 考试科目表
CREATE TABLE IF NOT EXISTS exam (
    examId      INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL COMMENT '科目名称',
    duration    INT NOT NULL DEFAULT 10 COMMENT '考试时长(分钟)',
    description VARCHAR(500) DEFAULT '',
    maxAttempts INT DEFAULT 1 COMMENT '最多可考次数',
    createdAt   DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 试题表
CREATE TABLE IF NOT EXISTS question (
    questionId INT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(500),
    optionA    VARCHAR(200),
    optionB    VARCHAR(200),
    optionC    VARCHAR(200),
    optionD    VARCHAR(200),
    answer     VARCHAR(10),
    examId     INT DEFAULT NULL,
    score      INT DEFAULT 25,
    FOREIGN KEY (examId) REFERENCES exam(examId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 答题记录表
CREATE TABLE IF NOT EXISTS exam_record (
    recordId   INT AUTO_INCREMENT PRIMARY KEY,
    userId     INT NOT NULL,
    questionId INT NOT NULL,
    userAnswer VARCHAR(10) DEFAULT NULL,
    isCorrect  TINYINT DEFAULT 0,
    score      INT DEFAULT 0,
    examTime   DATETIME DEFAULT CURRENT_TIMESTAMP,
    examId     INT DEFAULT NULL,
    FOREIGN KEY (userId) REFERENCES users(userId),
    FOREIGN KEY (questionId) REFERENCES question(questionId),
    FOREIGN KEY (examId) REFERENCES exam(examId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 重考申请表
CREATE TABLE IF NOT EXISTS reset_request (
    requestId  INT AUTO_INCREMENT PRIMARY KEY,
    userId     INT NOT NULL,
    examId     INT NOT NULL,
    score      INT DEFAULT 0,
    reason     VARCHAR(500) DEFAULT '',
    status     VARCHAR(20) DEFAULT 'pending' COMMENT 'pending/approved/rejected',
    createdAt  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES users(userId),
    FOREIGN KEY (examId) REFERENCES exam(examId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 初始化数据
-- ============================================

-- 默认管理员
INSERT IGNORE INTO users (userName, password, sex, email, role) VALUES ('admin', 'admin123', '男', 'admin@test.com', 'admin');

-- 示例科目
INSERT IGNORE INTO exam (name, duration, description, maxAttempts) VALUES
    ('Java程序设计', 10, 'Java基础语法考试', 1),
    ('数据库原理', 15, 'SQL与关系数据库理论', 1);

-- 示例试题 (Java程序设计)
INSERT IGNORE INTO question (title, optionA, optionB, optionC, optionD, answer, examId, score) VALUES
    ('Java中用于定义类的关键字是？', 'class', 'interface', 'struct', 'object', 'A', 1, 25),
    ('以下哪个不是Java的基本数据类型？', 'int', 'float', 'String', 'boolean', 'C', 1, 25),
    ('System.out.println() 的作用是？', '读取输入', '输出到控制台', '写入文件', '启动线程', 'B', 1, 20),
    ('Java程序入口方法是？', 'start()', 'run()', 'main()', 'init()', 'C', 1, 30);

-- 示例试题 (数据库原理)
INSERT IGNORE INTO question (title, optionA, optionB, optionC, optionD, answer, examId, score) VALUES
    ('SQL中用于删除记录的命令是？', 'DELETE', 'DROP', 'REMOVE', 'CLEAR', 'A', 2, 25),
    ('关系数据库中的主键必须满足？', '唯一性', '非空性', '唯一且非空', '以上都不是', 'C', 2, 25),
    ('SELECT语句中用于排序的子句是？', 'GROUP BY', 'ORDER BY', 'SORT BY', 'HAVING', 'B', 2, 25),
    ('数据库事务的正确特性是？', 'ACID', 'BASE', 'CAP', 'SOAP', 'A', 2, 25);
