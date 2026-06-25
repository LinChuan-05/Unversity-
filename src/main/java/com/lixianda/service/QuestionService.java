package com.lixianda.service;

import com.lixianda.entity.Question;
import com.lixianda.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    public int add(Question question) {
        return questionMapper.insert(question);
    }

    public List<Question> findAll() {
        return questionMapper.findAll();
    }

    public List<Question> findByExamId(Integer examId) {
        return questionMapper.findByExamId(examId);
    }

    public Question findById(Integer questionId) {
        return questionMapper.findById(questionId);
    }

    public int delete(Integer questionId) {
        return questionMapper.deleteById(questionId);
    }

    public int update(Question question) {
        return questionMapper.update(question);
    }

    public List<Question> findRandByExamId(Integer examId, int limit) {
        return questionMapper.findRandByExamId(examId, limit);
    }
}
