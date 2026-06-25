package com.lixianda.controller;

import com.lixianda.entity.*;
import com.lixianda.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exam")
public class ExamController {

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ExamRecordService examRecordService;
    @Autowired
    private ExamManageService examManageService;
    @Autowired
    private ResetRequestService resetRequestService;
    @Autowired
    private ExamConcurrencyService examConcurrencyService;

    // 学生端：可用科目列表（含剩余次数）
    @GetMapping("/subjects")
    public Result subjects(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        List<Exam> exams = examManageService.findAll();
        if (user != null) {
            List<Map<String, Object>> list = new java.util.ArrayList<>();
            for (Exam e : exams) {
                int taken = examRecordService.countAttempts(user.getUserId(), e.getExamId());
                int remaining = e.getMaxAttempts() - taken;
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("examId", e.getExamId());
                m.put("name", e.getName());
                m.put("duration", e.getDuration());
                m.put("description", e.getDescription());
                m.put("maxAttempts", e.getMaxAttempts());
                m.put("taken", taken);
                m.put("remaining", Math.max(0, remaining));
                list.add(m);
            }
            return Result.ok("ok", list);
        }
        return Result.ok("ok", exams);
    }

    // 学生端：进入指定科目的考试
    @GetMapping("/rand")
    public Result rand(@RequestParam Integer examId, HttpSession session) {
        Exam exam = examManageService.findById(examId);
        if (exam == null) {
            return Result.fail(404, "科目不存在");
        }

        // 检查是否已有正在进行的考试
        Long existingDeadline = (Long) session.getAttribute("examDeadline");
        Integer existingExamId = (Integer) session.getAttribute("examExamId");
        if (existingDeadline != null && existingExamId != null && existingExamId.equals(examId)) {
            long now = System.currentTimeMillis();
            if (now >= existingDeadline) {
                session.removeAttribute("examQuestions");
                session.removeAttribute("examDeadline");
                session.removeAttribute("examInfo");
                session.removeAttribute("examExamId");
                return Result.fail(410, "考试时间已截止，无法重新进入");
            }
            @SuppressWarnings("unchecked")
            List<Question> questions = (List<Question>) session.getAttribute("examQuestions");
            long remainingSeconds = (existingDeadline - now) / 1000;
            Map<String, Object> extra = new LinkedHashMap<>();
            extra.put("questions", questions);
            extra.put("deadline", existingDeadline);
            extra.put("remainingSeconds", remainingSeconds);
            return Result.ok("ok", extra);
        }

        // 检查参加次数
        Users user = (Users) session.getAttribute("user");
        int taken = examRecordService.countAttempts(user.getUserId(), examId);
        int remaining = exam.getMaxAttempts() - taken;
        if (remaining <= 0) {
            return Result.fail(403, "该科目考试次数已用完（最多 " + exam.getMaxAttempts() + " 次）");
        }

        int qCount = exam.getQuestionCount() != null ? exam.getQuestionCount() : 4;
        List<Question> questions = questionService.findRandByExamId(examId, qCount);
        if (questions == null || questions.size() < qCount) {
            return Result.fail(400, "该科目试题不足" + qCount + "道，无法考试");
        }

        long deadline = System.currentTimeMillis() + exam.getDuration() * 60L * 1000L;

        // 并发控制：同时在线考试人数不能超过 50 人
        if (!examConcurrencyService.tryEnter(session.getId(), deadline)) {
            return Result.fail(429, "当前考试人数已达上限（50人），请稍后再试");
        }

        session.setAttribute("examQuestions", questions);
        session.setAttribute("examDeadline", deadline);
        session.setAttribute("examInfo", exam);
        session.setAttribute("examExamId", examId);

        int maxScore = 100;

        Map<String, Object> extra = new LinkedHashMap<>();
        extra.put("questions", questions);
        extra.put("deadline", deadline);
        extra.put("remainingSeconds", exam.getDuration() * 60L);
        extra.put("remainingAttempts", remaining - 1);
        extra.put("maxScore", maxScore);
        return Result.ok("ok", extra);
    }

    // 学生端：提交试卷
    @PostMapping("/submit")
    public Result submit(@RequestParam Map<String, String> answers, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Question> questions = (List<Question>) session.getAttribute("examQuestions");
        Long deadline = (Long) session.getAttribute("examDeadline");
        Integer examId = (Integer) session.getAttribute("examExamId");
        if (questions == null || questions.isEmpty()) {
            return Result.fail(400, "考试题目已过期，请重新进入考试");
        }
        Users user = (Users) session.getAttribute("user");
        List<Map<String, Object>> details = scoreService.scoreDetail(questions, answers);
        int totalScore = 0, maxScore = 100;
        for (Map<String, Object> detail : details) {
            if (detail.containsKey("totalScore")) {
                totalScore = (int) detail.get("totalScore");
                maxScore = (int) detail.get("maxScore");
                break;
            }
            ExamRecord record = new ExamRecord(
                user.getUserId(),
                (Integer) detail.get("questionId"),
                (String) detail.get("userAnswer"),
                (Integer) detail.get("isCorrect"),
                (Integer) detail.get("score"),
                examId
            );
            examRecordService.save(record);
        }
        session.removeAttribute("examQuestions");
        session.removeAttribute("examDeadline");
        session.removeAttribute("examInfo");
        session.removeAttribute("examExamId");
        examConcurrencyService.leave(session.getId());
        return Result.ok(scoreService.getResultMessage(totalScore, maxScore), details);
    }

    /** 获取当前在线考试人数 */
    @GetMapping("/onlineCount")
    public Result onlineCount() {
        int count = examConcurrencyService.getOnlineCount();
        return Result.ok("ok", count);
    }

    /** 学生端：成绩汇总（仅按场次显示，不含逐题详情） */
    @GetMapping("/history")
    public Result history(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return Result.fail(401, "请先登录");
        return Result.ok("ok", examRecordService.findSummaryByUserId(user.getUserId()));
    }

    /** 管理员端：查看所有学生的考试状态 */
    @GetMapping("/studentStatus")
    public Result studentStatus() {
        return Result.ok("ok", examRecordService.findAllStudentExamStatus());
    }

    /** 学生端：提交重考申请 */
    @PostMapping("/requestReset")
    public Result requestReset(@RequestParam Integer examId, @RequestParam(defaultValue = "") String reason,
                               HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return Result.fail(401, "请先登录");
        // 获取该科目最近一次考试成绩
        List<Map<String, Object>> summaries = examRecordService.findSummaryByUserId(user.getUserId());
        int score = 0;
        for (Map<String, Object> s : summaries) {
            if (examId.equals(s.get("examId"))) { score = ((Number)s.get("totalScore")).intValue(); break; }
        }
        int r = resetRequestService.submit(user.getUserId(), examId, score, reason);
        if (r == -1) return Result.fail(400, "该科目已有待处理的申请，请等待管理员处理");
        if (r == 1) return Result.ok("重考申请已提交，请等待管理员审批");
        return Result.fail(500, "提交失败");
    }

    /** 学生端：查看我的重考申请状态 */
    @GetMapping("/myRequests")
    public Result myRequests(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return Result.fail(401, "请先登录");
        return Result.ok("ok", resetRequestService.findByUserId(user.getUserId()));
    }

    /** 管理员端：查看所有重考请求 */
    @GetMapping("/allRequests")
    public Result allRequests() {
        return Result.ok("ok", resetRequestService.findAllRequests());
    }

    /** 管理员端：批准重考申请，清除该学生在对应科目的答题记录 */
    @PostMapping("/approveReset")
    public Result approveReset(@RequestParam Integer requestId, @RequestParam Integer userId,
                               @RequestParam Integer examId, HttpSession session) {
        // 管理员权限检查
        Users admin = (Users) session.getAttribute("user");
        if (admin == null || !"admin".equals(admin.getRole())) return Result.fail(403, "无权限");
        // 清除该学生该科目成绩
        examRecordService.resetAttempts(userId, examId);
        // 更新请求状态
        resetRequestService.approve(requestId);
        return Result.ok("已批准重考，学生可重新参加考试");
    }

    /** 管理员端：拒绝重考申请 */
    @PostMapping("/rejectReset")
    public Result rejectReset(@RequestParam Integer requestId, HttpSession session) {
        Users admin = (Users) session.getAttribute("user");
        if (admin == null || !"admin".equals(admin.getRole())) return Result.fail(403, "无权限");
        resetRequestService.reject(requestId);
        return Result.ok("已拒绝重考申请");
    }

    /** 管理员端：重置学生考试次数 */
    @DeleteMapping("/resetAttempts")
    public Result resetAttempts(@RequestParam Integer userId, @RequestParam Integer examId) {
        int count = examRecordService.resetAttempts(userId, examId);
        return Result.ok("已清除 " + count + " 条记录，该学生可重新参加考试");
    }

    /** 学生端：查看错题集 */
    @GetMapping("/wrongAnswers")
    public Result wrongAnswers(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) return Result.fail(401, "请先登录");
        return Result.ok("ok", examRecordService.findWrongAnswers(user.getUserId()));
    }
}
