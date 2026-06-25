package com.lixianda.service;

import com.lixianda.entity.Question;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScoreService {

    /**
     * 自动判分，每题分值 = 100 / 题数，余数分配给最后一题
     */
    public List<Map<String, Object>> scoreDetail(List<Question> questionList, Map<String, String> answers) {
        List<Map<String, Object>> details = new ArrayList<>();
        int totalScore = 0;
        int count = questionList.size();
        int baseScore = 100 / count;
        int remainder = 100 % count;

        for (int i = 0; i < count; i++) {
            Question question = questionList.get(i);
            int qScore = (i == count - 1) ? baseScore + remainder : baseScore;

            String correctAnswer = question.getAnswer();
            Integer questionId = question.getQuestionId();
            String userAnswer = answers.get("answer_" + questionId);
            boolean correct = userAnswer != null && userAnswer.equals(correctAnswer);
            int earned = correct ? qScore : 0;
            totalScore += earned;

            Map<String, Object> detail = new LinkedHashMap<>();
            detail.put("questionId", questionId);
            detail.put("title", question.getTitle());
            detail.put("userAnswer", userAnswer != null ? userAnswer : "未作答");
            detail.put("correctAnswer", correctAnswer);
            detail.put("isCorrect", correct ? 1 : 0);
            detail.put("score", earned);
            detail.put("maxScore", qScore);
            details.add(detail);
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalScore", totalScore);
        summary.put("maxScore", 100);
        summary.put("questionCount", count);
        summary.put("message", getResultMessage(totalScore, 100));
        details.add(summary);
        return details;
    }

    public String getResultMessage(int score, int maxScore) {
        double ratio = maxScore > 0 ? (double) score / maxScore : 0;
        if (ratio >= 1.0) {
            return "恭喜！满分通过！本次考试成绩 : " + score + " / " + maxScore + " 分";
        } else if (ratio >= 0.6) {
            return "合格！本次考试成绩 : " + score + " / " + maxScore + " 分";
        } else {
            return "不合格！本次考试成绩 : " + score + " / " + maxScore + " 分";
        }
    }
}
