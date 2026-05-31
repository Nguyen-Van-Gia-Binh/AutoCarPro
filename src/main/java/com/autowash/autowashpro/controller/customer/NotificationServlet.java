package com.autowash.autowashpro.controller.customer;

import com.autowash.autowashpro.dao.NotificationDAO;
import com.autowash.autowashpro.model.Notification;
import com.autowash.autowashpro.model.User;
import com.google.gson.Gson;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * NotificationServlet — Quản lý thông báo + AJAX mark-as-read.
 *
 * === LUỒNG DỮ LIỆU ===
 * GET /customer/notifications → trang đầy đủ → notifications.jsp
 * GET /customer/notifications?action=list → AJAX → JSON danh sách 5 thông báo mới
 * GET /customer/notifications?action=count → AJAX → JSON số thông báo chưa đọc
 * POST /customer/notifications → AJAX mark-as-read → JSON success/fail
 *
 * === AJAX FLOW (Notification Bell) ===
 * 1. User click chuông → JS fetch GET ?action=list → hiển thị dropdown.
 * 2. User click vào thông báo → JS fetch POST {notificationId: 123} →
 *    NotificationDAO.markAsRead() → trả về JSON → JS ẩn badge đỏ.
 */
@WebServlet("/customer/notifications")
public class NotificationServlet extends HttpServlet {

    private NotificationDAO notificationDAO = new NotificationDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");
        String action = request.getParameter("action");

        try {
            // === AJAX: Lấy danh sách thông báo mới nhất (cho dropdown chuông) ===
            if ("list".equals(action)) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                List<Notification> notifications = notificationDAO.getByUserId(user.getUserId(), 5);
                response.getWriter().write(gson.toJson(notifications));
                return;
            }

            // === AJAX: Đếm thông báo chưa đọc (cho badge chuông) ===
            if ("count".equals(action)) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                int count = notificationDAO.getUnreadCount(user.getUserId());
                Map<String, Integer> result = new HashMap<>();
                result.put("unreadCount", count);
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // === Trang thông báo đầy đủ ===
            List<Notification> allNotifications = notificationDAO.getAllByUserId(user.getUserId());
            int unreadCount = notificationDAO.getUnreadCount(user.getUserId());
            request.setAttribute("notifications", allNotifications);
            request.setAttribute("unreadCount", unreadCount);
            request.getRequestDispatcher("/customer/notifications.jsp").forward(request, response);

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            if ("list".equals(action) || "count".equals(action)) {
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            } else {
                request.setAttribute("error", "Lỗi tải thông báo: " + e.getMessage());
                request.getRequestDispatcher("/customer/notifications.jsp").forward(request, response);
            }
        }
    }

    /**
     * POST: AJAX mark notification as read.
     * Body: notificationId=123
     * Response: JSON {"success": true, "unreadCount": 2}
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        User user = (User) request.getSession().getAttribute("user");
        Map<String, Object> result = new HashMap<>();

        try {
            int notificationId = Integer.parseInt(request.getParameter("notificationId"));

            // Mark as read — kiểm tra userId để đảm bảo bảo mật
            boolean updated = notificationDAO.markAsRead(notificationId, user.getUserId());

            // Đếm lại số chưa đọc
            int newUnreadCount = notificationDAO.getUnreadCount(user.getUserId());

            result.put("success", updated);
            result.put("unreadCount", newUnreadCount);
            response.getWriter().write(gson.toJson(result));

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(result));
        }
    }
}
