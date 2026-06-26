-- ============================================
-- University 在线考试管理系统 数据库初始化脚本
-- 用法：mysql -u root -p < init.sql
-- ============================================

CREATE DATABASE IF NOT EXISTS bjpowernode DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bjpowernode;

-- ============================================
-- 建表（共 8 张）
-- ============================================

-- 1. 班级表
CREATE TABLE IF NOT EXISTS sys_class (
    class_id    INT AUTO_INCREMENT PRIMARY KEY,
    class_name  VARCHAR(50) NOT NULL UNIQUE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 用户表
CREATE TABLE IF NOT EXISTS users (
    userId      INT AUTO_INCREMENT PRIMARY KEY,
    userName    VARCHAR(50),
    password    VARCHAR(100),
    real_name   VARCHAR(20) DEFAULT NULL,
    sex         VARCHAR(10),
    email       VARCHAR(100),
    role        VARCHAR(20) DEFAULT 'student',
    phone       VARCHAR(11) DEFAULT NULL,
    status      TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    class_id    INT DEFAULT NULL,
    FOREIGN KEY (class_id) REFERENCES sys_class(class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 考试科目/试卷表
CREATE TABLE IF NOT EXISTS exam (
    examId        INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    duration      INT NOT NULL DEFAULT 10,
    description   VARCHAR(500) DEFAULT '',
    maxAttempts   INT DEFAULT 1,
    questionCount INT DEFAULT 4,
    paper_status  TINYINT DEFAULT 1,
    start_time    DATETIME DEFAULT NULL,
    end_time      DATETIME DEFAULT NULL,
    createdAt     DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 试题表
CREATE TABLE IF NOT EXISTS question (
    questionId    INT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(500),
    optionA       VARCHAR(200),
    optionB       VARCHAR(200),
    optionC       VARCHAR(200),
    optionD       VARCHAR(200),
    answer        VARCHAR(1000),
    examId        INT DEFAULT NULL,
    score         INT DEFAULT 25,
    question_type TINYINT DEFAULT 1,
    difficulty    TINYINT DEFAULT 2,
    is_use        TINYINT DEFAULT 1,
    FOREIGN KEY (examId) REFERENCES exam(examId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 试卷题目关联表（多对多中间表，组卷灵活配置用）
CREATE TABLE IF NOT EXISTS paper_question_rel (
    rel_id         INT AUTO_INCREMENT PRIMARY KEY,
    paper_id       INT NOT NULL,
    question_id    INT NOT NULL,
    question_score INT DEFAULT 0,
    sort           INT DEFAULT 0,
    FOREIGN KEY (paper_id)    REFERENCES exam(examId),
    FOREIGN KEY (question_id) REFERENCES question(questionId),
    UNIQUE KEY uk_paper_question (paper_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. 答题记录表
CREATE TABLE IF NOT EXISTS exam_record (
    recordId      INT AUTO_INCREMENT PRIMARY KEY,
    userId        INT NOT NULL,
    class_id      INT DEFAULT NULL,
    questionId    INT NOT NULL,
    userAnswer    VARCHAR(500) DEFAULT NULL,
    isCorrect     TINYINT DEFAULT 0,
    score         INT DEFAULT 0,
    manual_score  INT DEFAULT NULL,
    review_status TINYINT DEFAULT 0,
    examTime      DATETIME DEFAULT CURRENT_TIMESTAMP,
    examId        INT DEFAULT NULL,
    exam_status   TINYINT DEFAULT 2,
    is_pass       TINYINT DEFAULT NULL,
    FOREIGN KEY (userId)     REFERENCES users(userId),
    FOREIGN KEY (questionId) REFERENCES question(questionId),
    FOREIGN KEY (examId)     REFERENCES exam(examId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. 错题集表
CREATE TABLE IF NOT EXISTS exam_error_question (
    error_id       INT AUTO_INCREMENT PRIMARY KEY,
    user_id        INT NOT NULL,
    question_id    INT NOT NULL,
    error_times    INT DEFAULT 1,
    last_exam_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_master      TINYINT DEFAULT 0,
    user_answer    VARCHAR(500) DEFAULT NULL,
    right_answer   VARCHAR(500) DEFAULT NULL,
    FOREIGN KEY (user_id)     REFERENCES users(userId),
    FOREIGN KEY (question_id) REFERENCES question(questionId),
    UNIQUE KEY uk_user_question (user_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. 重考申请表
CREATE TABLE IF NOT EXISTS reset_request (
    requestId INT AUTO_INCREMENT PRIMARY KEY,
    userId    INT NOT NULL,
    examId    INT NOT NULL,
    score     INT DEFAULT 0,
    reason    VARCHAR(500) DEFAULT '',
    status    VARCHAR(20) DEFAULT 'pending',
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES users(userId),
    FOREIGN KEY (examId) REFERENCES exam(examId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 初始化数据
-- ============================================

-- 班级
INSERT IGNORE INTO sys_class (class_name) VALUES ('软件工程2411班'), ('软件工程2412班');

-- 管理员
INSERT IGNORE INTO users (userName, password, real_name, sex, email, role, status)
VALUES ('admin', 'admin123', '管理员', '男', 'admin@test.com', 'admin', 1);

-- 科目（每科 5 题，含 1 道简答）
INSERT IGNORE INTO exam (name, duration, description, maxAttempts, questionCount, paper_status)
VALUES
    ('Java程序设计', 10, 'Java基础语法考试', 1, 5, 1),
    ('数据库原理',   15, 'SQL与关系数据库理论', 1, 5, 1);

-- Java 程序设计试题（4 单选 + 1 简答，每题 20 分）
INSERT IGNORE INTO question (title, optionA, optionB, optionC, optionD, answer, examId, score, question_type)
VALUES
    ('Java中用于定义类的关键字是？',       'class', 'interface', 'struct', 'object', 'A', 1, 20, 1),
    ('以下哪个不是Java的基本数据类型？',   'int', 'float', 'String', 'boolean', 'C', 1, 20, 1),
    ('Java程序入口方法是？',               'start()', 'run()', 'main()', 'init()', 'C', 1, 20, 1),
    ('System.out.println()的作用是？',     '读取输入', '输出到控制台', '写入文件', '启动线程', 'B', 1, 20, 1),
    ('请简述Java中面向对象的三大特性。',   '-', '-', '-', '-', '封装、继承、多态', 1, 20, 4);

-- 数据库原理试题（4 单选 + 1 简答，每题 20 分）
INSERT IGNORE INTO question (title, optionA, optionB, optionC, optionD, answer, examId, score, question_type)
VALUES
    ('SQL中用于删除记录的命令是？',                'DELETE', 'DROP', 'REMOVE', 'CLEAR', 'A', 2, 20, 1),
    ('关系数据库中的主键必须满足？',               '唯一性', '非空性', '唯一且非空', '以上都不是', 'C', 2, 20, 1),
    ('SELECT语句中用于排序的子句是？',             'GROUP BY', 'ORDER BY', 'SORT BY', 'HAVING', 'B', 2, 20, 1),
    ('数据库事务的正确特性是？',                   'ACID', 'BASE', 'CAP', 'SOAP', 'A', 2, 20, 1),
    ('请解释数据库ACID特性中A（原子性）的含义。',  '-', '-', '-', '-', '原子性指事务中的所有操作要么全部成功，要么全部失败回滚。', 2, 20, 4);
