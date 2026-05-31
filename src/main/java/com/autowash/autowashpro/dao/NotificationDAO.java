package com.autowash.autowashpro.dao;

import com.autowash.autowashpro.model.Notification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * NotificationDAO — Quản lý hộp thư thông báo cá nhân.
 *
 * === LUỒNG DỮ LIỆU (DATA FLOW) ===
 * 1. Header chuông: JSP gọi NotificationDAO.getUnreadCount() → hiển thị badge đỏ.
 * 2. Dropdown chuông: AJAX GET → NotificationServlet → getByUserId(limit=5) → JSON.
 * 3. Đánh dấu đã đọc: AJAX POST → NotificationServlet → markAsRead(id) → DB update.
 * 4. Tạo thông báo: LoyaltyService.processCheckout() → createNotification() →
 *    INSERT DB → thông báo tự động xuất hiện cho khách khi reload.
 */
public class NotificationDAO extends BaseDAO {

    /**
     * Lấy danh sách thông báo của user, giới hạn số lượng.
     * 
     * @param userId ID người dùng
     * @param limit  Số thông báo tối đa (VD: 10 cho trang đầy đủ, 5 cho dropdown)
     */
    public List<Notification> getByUserId(int userId, int limit) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT TOP (?) NotificationId, UserId, Title, Content, IsRead, CreatedAt "
                   + "FROM Notifications WHERE UserId = ? "
                   + "ORDER BY CreatedAt DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }

    /**
     * Lấy tất cả thông báo của user (cho trang thông báo đầy đủ).
     */
    public List<Notification> getAllByUserId(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT NotificationId, UserId, Title, Content, IsRead, CreatedAt "
                   + "FROM Notifications WHERE UserId = ? "
                   + "ORDER BY CreatedAt DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }

    /**
     * Đếm số thông báo chưa đọc.
     * Dùng cho: Badge đỏ trên biểu tượng chuông ở Header.
     *
     * === TẠI SAO DÙNG COUNT(*) THAY VÌ LOAD TẤT CẢ? ===
     * → Hiệu năng: Chỉ cần con số, không cần nội dung thông báo.
     * → Badge được gọi ở MỌI trang (trong header.jsp), nên phải nhanh.
     */
    public int getUnreadCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Notifications WHERE UserId = ? AND IsRead = 0";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Đánh dấu một thông báo là đã đọc (IsRead = 1).
     * 
     * === ĐƯỢC GỌI TỪ AJAX ===
     * Khi user click vào thông báo trong dropdown → JavaScript gửi fetch POST →
     * NotificationServlet gọi markAsRead() → cập nhật DB → trả về success JSON →
     * JS ẩn badge đỏ và đánh dấu thông báo đã đọc trên giao diện.
     *
     * Bảo mật: Kiểm tra cả userId để đảm bảo user chỉ đọc thông báo của mình.
     */
    public boolean markAsRead(int notificationId, int userId) throws SQLException {
        String sql = "UPDATE Notifications SET IsRead = 1 "
                   + "WHERE NotificationId = ? AND UserId = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, notificationId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Tạo thông báo mới cho user.
     * 
     * Được gọi khi:
     * - Checkout thành công → "Thanh toán thành công!"
     * - Auto-upgrade tier → "Chúc mừng thăng hạng!"
     * - Đặt lịch thành công → "Đặt lịch thành công!"
     *
     * @param conn Connection từ transaction (nếu gọi trong processCheckout)
     */
    public boolean createNotification(Connection conn, Notification notification) throws SQLException {
        String sql = "INSERT INTO Notifications (UserId, Title, Content, IsRead) "
                   + "VALUES (?, ?, ?, 0)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notification.getUserId());
            ps.setString(2, notification.getTitle());
            ps.setString(3, notification.getContent());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Tạo thông báo mới (phiên bản không cần transaction — tự tạo Connection).
     * Dùng cho: Các trường hợp không nằm trong transaction (VD: đặt lịch mới).
     */
    public boolean createNotification(Notification notification) throws SQLException {
        String sql = "INSERT INTO Notifications (UserId, Title, Content, IsRead) "
                   + "VALUES (?, ?, ?, 0)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, notification.getUserId());
            ps.setString(2, notification.getTitle());
            ps.setString(3, notification.getContent());

            return ps.executeUpdate() > 0;
        }
    }

    // ========================
    // PHƯƠNG THỨC NỘI BỘ
    // ========================

    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        return new Notification(
                rs.getInt("NotificationId"),
                rs.getInt("UserId"),
                rs.getString("Title"),
                rs.getString("Content"),
                rs.getBoolean("IsRead"),
                rs.getTimestamp("CreatedAt")
        );
    }
}
