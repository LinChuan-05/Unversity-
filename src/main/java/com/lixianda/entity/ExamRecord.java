package com.lixianda.entity;

import java.util.Date;

public class ExamRecord {
    private Integer recordId;
    private Integer userId;
    private Integer questionId;
    private String userAnswer;
    private Integer isCorrect;
    private Integer score;
    private Date examTime;
    private Integer examId;
    private Integer manualScore;
    private Integer reviewStatus;

    public ExamRecord() {}

    public ExamRecord(Integer userId, Integer questionId, String userAnswer, Integer isCorrect, Integer score, Integer examId) {
        this.userId = userId;
        this.questionId = questionId;
        this.userAnswer = userAnswer;
        this.isCorrect = isCorrect;
        this.score = score;
        this.examId = examId;
    }

    public Integer getRecordId() { return recordId; }
    public void setRecordId(Integer recordId) { this.recordId = recordId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }
    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
    public Integer getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Integer isCorrect) { this.isCorrect = isCorrect; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Date getExamTime() { return examTime; }
    public void setExamTime(Date examTime) { this.examTime = examTime; }
    public Integer getExamId() { return examId; }
    public void setExamId(Integer examId) { this.examId = examId; }
    public Integer getManualScore() { return manualScore; }
    public void setManualScore(Integer manualScore) { this.manualScore = manualScore; }
    public Integer getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(Integer reviewStatus) { this.reviewStatus = reviewStatus; }
}
