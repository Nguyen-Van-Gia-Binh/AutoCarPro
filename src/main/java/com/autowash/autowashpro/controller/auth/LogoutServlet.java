package com.autowash.autowashpro.controller.auth;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * LogoutServlet — Xử lý đăng xuất.
 *
 * === LUỒNG DỮ LIỆU ===
 * GET /logout → invalidate session (xóa toàn bộ dữ liệu session) →
 * redirect về /login.
 *
 * === TẠI SAO DÙNG session.invalidate()? ===
 * → Xóa toàn bộ attribute trong session (bao gồm "user").
 * → Hủy session ID → ngăn chặn session fixation attack.
 * → An toàn hơn so với chỉ removeAttribute("user").
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy session hiện tại (không tạo mới nếu không có)
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate(); // Hủy toàn bộ session
        }

        // Redirect về trang đăng nhập
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
