package com.autowash.autowashpro.controller.auth;

import com.autowash.autowashpro.dao.NotificationDAO;
import com.autowash.autowashpro.dao.UserDAO;
import com.autowash.autowashpro.model.Notification;
import com.autowash.autowashpro.model.User;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * RegisterServlet — Xử lý đăng ký tài khoản khách hàng mới.
 *
 * === LUỒNG DỮ LIỆU ===
 * GET /register → hiển thị form đăng ký (register.jsp)
 * POST /register → nhận fullName + phone + password →
 *   kiểm tra trùng SĐT → UserDAO.register() → tạo notification chào mừng →
 *   redirect về /login với thông báo thành công.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        try {
            // === VALIDATE ===

            // Kiểm tra mật khẩu xác nhận
            if (!password.equals(confirmPassword)) {
                request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
                request.setAttribute("fullName", fullName);
                request.setAttribute("phone", phone);
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            // Kiểm tra SĐT đã tồn tại
            if (userDAO.findByPhone(phone) != null) {
                request.setAttribute("error", "Số điện thoại đã được đăng ký.");
                request.setAttribute("fullName", fullName);
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            // === TẠO TÀI KHOẢN MỚI ===
            User newUser = new User(phone, password, fullName);
            boolean success = userDAO.register(newUser);

            if (success) {
                // Tạo thông báo chào mừng cho user mới
                User createdUser = userDAO.findByPhone(phone);
                if (createdUser != null) {
                    Notification welcomeNotif = new Notification(
                            createdUser.getUserId(),
                            "Chào mừng đến AutoWash Pro!",
                            "Cảm ơn bạn đã đăng ký tài khoản. Hãy thêm xe và đặt lịch rửa ngay để bắt đầu tích điểm nhé!"
                    );
                    notificationDAO.createNotification(welcomeNotif);
                }

                // Redirect về login với thông báo thành công
                response.sendRedirect(request.getContextPath() + "/login?registered=true");
            } else {
                request.setAttribute("error", "Không thể tạo tài khoản. Vui lòng thử lại.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi hệ thống. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
