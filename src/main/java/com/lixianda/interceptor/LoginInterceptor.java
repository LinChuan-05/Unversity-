package com.lixianda.interceptor;

import com.lixianda.entity.Users;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // 1. 登录相关和静态资源放行
        if (uri.contains("login") || uri.equals("/myWeb/") || uri.contains(".css") || uri.contains(".js")) {
            return true;
        }

        // 2. 检查是否登录
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("/myWeb/login.html");
            return false;
        }

        // 3. 考试接口仅学生可访问，管理员不需要考试
        boolean isExamUri = uri.contains("/api/exam/rand")
                || uri.contains("/api/exam/submit");

        if (isExamUri) {
            Users user = (Users) session.getAttribute("user");
            if (user != null && "admin".equals(user.getRole())) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"msg\":\"管理员无需参加考试\"}");
                return false;
            }
        }

        // 4. 管理员权限检查
        boolean isAdminUri = uri.contains("/api/user/add")
                || uri.contains("/api/user/delete")
                || uri.contains("/api/question/add")
                || uri.contains("/api/question/delete")
                || uri.contains("/api/question/update")
                || uri.contains("/api/question/findById")
                || uri.contains("/api/examManage")
                || uri.contains("/api/exam/studentStatus")
                || uri.contains("/api/exam/resetAttempts")
                || uri.contains("/api/exam/allRequests")
                || uri.contains("/api/exam/approveReset")
                || uri.contains("/api/exam/rejectReset")
                || uri.contains("/api/exam/pendingReviews")
                || uri.contains("/api/exam/manualScore");

        if (isAdminUri) {
            Users user = (Users) session.getAttribute("user");
            if (!"admin".equals(user.getRole())) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"msg\":\"无管理员权限\"}");
                return false;
            }
        }

        return true;
    }
}
