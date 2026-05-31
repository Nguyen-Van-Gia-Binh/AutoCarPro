package com.autowash.autowashpro.controller.auth;

import com.autowash.autowashpro.dao.UserDAO;
import com.autowash.autowashpro.model.User;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * LoginServlet — Xử lý đăng nhập cho tất cả vai trò.
 *
 * === LUỒNG DỮ LIỆU (DATA FLOW) ===
 * 1. GET /login → hiển thị form đăng nhập (login.jsp).
 * 2. POST /login → nhận phone + password từ form →
 *    UserDAO.findByPhone(phone) → kiểm tra password →
 *    NẾU ĐÚNG → lưu User vào session → redirect theo role:
 *      - CUSTOMER → /customer/dashboard
 *      - ADMIN/STAFF → /admin/dashboard
 *    NẾU SAI → quay lại login.jsp với thông báo lỗi.
 *
 * === ANNOTATION @WebServlet("/login") ===
 * Tomcat tự động map URL "/login" tới servlet này.
 * Không cần khai báo trong web.xml.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    /**
     * GET: Hiển thị trang đăng nhập.
     * Nếu user đã đăng nhập (session có "user") → redirect thẳng về dashboard.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra đã đăng nhập chưa
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            redirectByRole(request, response, user);
            return;
        }

        // Chưa đăng nhập → hiển thị form login
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    /**
     * POST: Xử lý đăng nhập.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Đặt encoding UTF-8 để đọc đúng tiếng Việt
        request.setCharacterEncoding("UTF-8");

        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

        try {
            // Tìm user trong database theo số điện thoại
            User user = userDAO.findByPhone(phone);

            if (user != null && user.getPassword().equals(password)) {
                // === ĐĂNG NHẬP THÀNH CÔNG ===
                // Tạo session mới và lưu thông tin user
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user);

                // Redirect theo vai trò
                redirectByRole(request, response, user);
            } else {
                // === ĐĂNG NHẬP THẤT BẠI ===
                // Đặt thông báo lỗi vào request attribute
                request.setAttribute("error", "Số điện thoại hoặc mật khẩu không đúng.");
                request.setAttribute("phone", phone); // Giữ lại SĐT đã nhập
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            // Lỗi database → hiển thị thông báo lỗi chung
            request.setAttribute("error", "Lỗi hệ thống. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    /**
     * Redirect user về dashboard phù hợp với vai trò.
     *
     * Tại sao dùng redirect thay vì forward?
     * → Redirect tạo URL mới trên thanh địa chỉ (/customer/dashboard).
     * → Forward giữ nguyên URL (/login) → gây nhầm lẫn cho user.
     * → Redirect cũng tránh lỗi "double submit" khi user nhấn F5.
     */
    private void redirectByRole(HttpServletRequest request,
                                 HttpServletResponse response, User user)
            throws IOException {

        String contextPath = request.getContextPath();

        if (user.isAdmin() || user.isStaff()) {
            response.sendRedirect(contextPath + "/admin/dashboard");
        } else {
            response.sendRedirect(contextPath + "/customer/dashboard");
        }
    }
}
