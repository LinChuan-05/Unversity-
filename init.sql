-- ============================================
-- University 在线考试管理系统 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS bjpowernode DEFAULT CHARACTER SET utf8mb4;
USE bjpowernode;

-- 1. 班级表
CREATE TABLE IF NOT EXISTS sys_class (
    class_id   INT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(50) NOT NULL UNIQUE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 用户表
CREATE TABLE IF NOT EXISTS users (
    userId     INT AUTO_INCREMENT PRIMARY KEY,
    userName   VARCHAR(50),
    password   VARCHAR(100),
    real_name  VARCHAR(20) DEFAULT NULL,
    sex        VARCHAR(10),
    email      VARCHAR(100),
    role       VARCHAR(20) DEFAULT 'student',
    phone      VARCHAR(11) DEFAULT NULL,
    status     TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    class_id   INT DEFAULT NULL,
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
    paper_status  TINYINT DEFAULT 1 COMMENT '0草稿1已发布2已关闭',
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
    answer        VARCHAR(10),
    examId        INT DEFAULT NULL,
    score         INT DEFAULT 25,
    question_type TINYINT DEFAULT 1 COMMENT '1单选2多选3判断4简答',
    difficulty    TINYINT DEFAULT 2 COMMENT '1简单2中等3困难',
    is_use        TINYINT DEFAULT 1,
    FOREIGN KEY (examId) REFERENCES exam(examId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 试卷题目关联表（多对多中间表）
CREATE TABLE IF NOT EXISTS paper_question_rel (
    rel_id        INT AUTO_INCREMENT PRIMARY KEY,
    paper_id      INT NOT NULL,
    question_id   INT NOT NULL,
    question_score INT DEFAULT 0,
    sort          INT DEFAULT 0,
    FOREIGN KEY (paper_id) REFERENCES exam(examId),
    FOREIGN KEY (question_id) REFERENCES question(questionId),
    UNIQUE KEY uk_paper_question (paper_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. 答题记录表
CREATE TABLE IF NOT EXISTS exam_record (
    recordId   INT AUTO_INCREMENT PRIMARY KEY,
    userId     INT NOT NULL,
    class_id   INT DEFAULT NULL,
    questionId INT NOT NULL,
    userAnswer VARCHAR(10) DEFAULT NULL,
    isCorrect  TINYINT DEFAULT 0,
    score      INT DEFAULT 0,
    examTime   DATETIME DEFAULT CURRENT_TIMESTAMP,
    examId     INT DEFAULT NULL,
    FOREIGN KEY (userId) REFERENCES users(userId),
    FOREIGN KEY (questionId) REFERENCES question(questionId),
    exam_status TINYINT DEFAULT 2 COMMENT '0未开始1进行中2已提交',
    is_pass     TINYINT DEFAULT NULL COMMENT '0不及格1及格',
    FOREIGN KEY (examId) REFERENCES exam(examId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. 错题集表
CREATE TABLE IF NOT EXISTS exam_error_question (
    error_id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT NOT NULL,
    question_id   INT NOT NULL,
    error_times   INT DEFAULT 1,
    last_exam_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_master     TINYINT DEFAULT 0,
    user_answer   VARCHAR(500) DEFAULT NULL,
    right_answer  VARCHAR(500) DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES users(userId),
    FOREIGN KEY (question_id) REFERENCES question(questionId),
    UNIQUE KEY uk_user_question (user_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. 重考申请表
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

-- 默认班级
INSERT IGNORE INTO sys_class (class_name) VALUES ('软件工程2411班'), ('软件工程2412班');
-- 默认管理员
INSERT IGNORE INTO users (userName, password, real_name, sex, email, role, status, class_id) VALUES ('admin', 'admin123', '管理员', '男', 'admin@test.com', 'admin', 1, NULL);

-- 示例科目（含抽题数、状态）
INSERT IGNORE INTO exam (name, duration, description, maxAttempts, questionCount, paper_status) VALUES
    ('Java程序设计', 10, 'Java基础语法考试', 1, 4, 1),
    ('数据库原理', 15, 'SQL与关系数据库理论', 1, 4, 1);

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
