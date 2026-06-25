package com.lixianda.service;

import com.lixianda.entity.Question;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ScoreService {

    public List<Map<String, Object>> scoreDetail(List<Question> questionList, Map<String, String> answers) {
        List<Map<String, Object>> details = new ArrayList<>();
        int objectiveTotal = 0, subjectiveMax = 0;
        int count = questionList.size();

        long objCount = questionList.stream().filter(q -> q.getQuestionType() == null || q.getQuestionType() != 4).count();
        int objBase = objCount > 0 ? 100 / (int) objCount : 0;
        int objRemainder = objCount > 0 ? 100 % (int) objCount : 0;
        int objIdx = 0;

        for (Question question : questionList) {
            Integer qId = question.getQuestionId();
            Integer qType = question.getQuestionType() != null ? question.getQuestionType() : 1;
            String userAnswer = answers.get("answer_" + qId);

            Map<String, Object> detail = new LinkedHashMap<>();
            detail.put("questionId", qId);
            detail.put("title", question.getTitle());
            detail.put("questionType", qType);

            if (qType == 4) {
                detail.put("userAnswer", userAnswer != null ? userAnswer : "未作答");
                detail.put("correctAnswer", question.getAnswer());
                detail.put("isCorrect", -1);
                detail.put("score", 0);
                detail.put("maxScore", question.getScore() != null ? question.getScore() : 40);
                detail.put("pending", true);
                subjectiveMax += (question.getScore() != null ? question.getScore() : 40);
            } else {
                int qScore = (objIdx == objCount - 1) ? objBase + objRemainder : objBase;
                objIdx++;
                String correctAnswer = question.getAnswer();
                boolean correct = userAnswer != null && userAnswer.equals(correctAnswer);
                int earned = correct ? qScore : 0;
                objectiveTotal += earned;
                detail.put("userAnswer", userAnswer != null ? userAnswer : "未作答");
                detail.put("correctAnswer", correctAnswer);
                detail.put("isCorrect", correct ? 1 : 0);
                detail.put("score", earned);
                detail.put("maxScore", qScore);
                detail.put("pending", false);
            }
            details.add(detail);
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("hasSubjective", subjectiveMax > 0);
        summary.put("totalScore", objectiveTotal);
        summary.put("maxScore", 100);
        summary.put("questionCount", count);
        summary.put("objectiveScore", objectiveTotal);
        summary.put("subjectiveMax", subjectiveMax);
        summary.put("message", subjectiveMax > 0 ?
            "试卷已提交，含简答题待教师批阅，批阅完成后可查看成绩" :
            getResultMessage(objectiveTotal, 100));
        details.add(summary);
        return details;
    }

    public String getResultMessage(int score, int maxScore) {
        double ratio = maxScore > 0 ? (double) score / maxScore : 0;
        if (ratio >= 1.0) return "满分通过！" + score + " / " + maxScore + " 分";
        if (ratio >= 0.6) return "合格！" + score + " / " + maxScore + " 分";
        return "不合格！" + score + " / " + maxScore + " 分";
    }
}
