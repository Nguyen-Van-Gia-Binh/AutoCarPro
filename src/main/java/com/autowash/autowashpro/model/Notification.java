package com.autowash.autowashpro.model;

import java.util.Date;

/**
 * Notification — Đại diện cho một thông báo trong hộp thư cá nhân của người dùng.
 *
 * === LUỒNG DỮ LIỆU (DATA FLOW) ===
 * 1. Tạo thông báo: LoyaltyService.processCheckout() tự động tạo thông báo khi:
 *    - Thanh toán thành công → "Đặt lịch thành công"
 *    - Thăng hạng thẻ → "Chúc mừng thăng hạng!"
 *    → NotificationDAO.createNotification() → INSERT vào DB.
 * 2. Hiển thị chuông: Header.jsp gọi NotificationDAO.getUnreadCount() →
 *    Hiển thị badge đỏ trên biểu tượng chuông nếu có thông báo chưa đọc.
 * 3. Dropdown thông báo: Khi user click chuông → NotificationServlet (AJAX) →
 *    trả về JSON danh sách thông báo mới nhất.
 * 4. Đánh dấu đã đọc: User click vào thông báo → AJAX fetch gửi POST →
 *    NotificationDAO.markAsRead(notificationId) → cập nhật IsRead = 1 →
 *    Badge đỏ biến mất mượt mà trên giao diện mà không load lại trang.
 *
 * === TẠI SAO DÙNG AJAX? ===
 * - Trải nghiệm mượt mà: Người dùng không bị gián đoạn bởi page reload.
 * - Realtime: Badge cập nhật tức thì sau khi đọc thông báo.
 */
public class Notification {

    private int notificationId;    // ID tự tăng (PK)
    private int userId;            // Người nhận (FK → Users)
    private String title;          // Tiêu đề thông báo (VD: "Chúc mừng thăng hạng!")
    private String content;        // Nội dung chi tiết
    private boolean isRead;        // Trạng thái đọc: false = chưa đọc, true = đã đọc
    private Date createdAt;        // Thời điểm tạo thông báo

    // === CONSTRUCTORS ===

    public Notification() {
    }

    /**
     * Constructor khi tạo thông báo mới (từ LoyaltyService hoặc hệ thống).
     */
    public Notification(int userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.isRead = false; // Mặc định chưa đọc
    }

    /**
     * Constructor đầy đủ — dùng khi đọc từ ResultSet.
     */
    public Notification(int notificationId, int userId, String title,
                        String content, boolean isRead, Date createdAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // === GETTERS & SETTERS ===

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + notificationId +
                ", title='" + title + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}
