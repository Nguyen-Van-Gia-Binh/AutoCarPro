package com.autowash.autowashpro.model;

import java.util.Date;

/**
 * User — Đại diện cho một người dùng trong hệ thống AutoWash Pro.
 *
 * === LUỒNG DỮ LIỆU (DATA FLOW) ===
 * 1. Đăng nhập: LoginServlet gọi UserDAO.findByPhone(phone) → trả về User →
 *    lưu vào HttpSession → các Servlet/JSP sau đó đọc từ session.
 * 2. Dashboard: DashboardServlet đọc User từ session → truyền vào 
 *    LoyaltyService.calculateNextRewardProgress(user) → trả về % tiến trình.
 * 3. Checkout: CheckoutServlet cập nhật PointsBalance, TotalSpent, TotalWashes
 *    của User sau khi thanh toán.
 *
 * === VAI TRÒ TRONG HỆ THỐNG ===
 * - Là thực thể trung tâm, liên kết với hầu hết các bảng khác.
 * - Field "role" quyết định quyền truy cập (phân quyền bởi AuthorizationFilter).
 * - Field "currentTierId" liên kết với bảng Tiers để xác định hạng thẻ.
 * - PointsBalance là "ví điểm" — số điểm thưởng hiện có để đổi giảm giá.
 */
public class User {

    private int userId;            // ID tự tăng (PK)
    private String phoneNumber;    // Số điện thoại (UNIQUE, dùng làm tài khoản đăng nhập)
    private String password;       // Mật khẩu (plaintext cho demo, nên hash trong thực tế)
    private String fullName;       // Họ tên đầy đủ
    private String role;           // Vai trò: "CUSTOMER", "STAFF", "ADMIN"
    private String currentTierId;  // Mã hạng thẻ hiện tại (FK → Tiers.TierId)
    private int pointsBalance;     // Số điểm thưởng hiện có trong ví
    private double totalSpent;     // Tổng tiền đã chi tiêu tích lũy (VNĐ)
    private int totalWashes;       // Tổng số lần rửa xe đã hoàn thành
    private Date createdAt;        // Thời điểm tạo tài khoản

    // === CONSTRUCTORS ===

    public User() {
    }

    /**
     * Constructor dùng khi đăng ký tài khoản mới.
     * Các trường còn lại (tierId, points, totalSpent, totalWashes) dùng giá trị mặc định.
     */
    public User(String phoneNumber, String password, String fullName) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.fullName = fullName;
        this.role = "CUSTOMER";         // Mặc định khi đăng ký là Khách hàng
        this.currentTierId = "MEMBER";  // Mặc định hạng Thành Viên
        this.pointsBalance = 0;
        this.totalSpent = 0;
        this.totalWashes = 0;
    }

    /**
     * Constructor đầy đủ — Dùng khi đọc từ ResultSet trong DAO.
     */
    public User(int userId, String phoneNumber, String password, String fullName,
                String role, String currentTierId, int pointsBalance,
                double totalSpent, int totalWashes, Date createdAt) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.currentTierId = currentTierId;
        this.pointsBalance = pointsBalance;
        this.totalSpent = totalSpent;
        this.totalWashes = totalWashes;
        this.createdAt = createdAt;
    }

    // === GETTERS & SETTERS ===

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCurrentTierId() {
        return currentTierId;
    }

    public void setCurrentTierId(String currentTierId) {
        this.currentTierId = currentTierId;
    }

    public int getPointsBalance() {
        return pointsBalance;
    }

    public void setPointsBalance(int pointsBalance) {
        this.pointsBalance = pointsBalance;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public int getTotalWashes() {
        return totalWashes;
    }

    public void setTotalWashes(int totalWashes) {
        this.totalWashes = totalWashes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // === HELPER METHODS ===

    /**
     * Kiểm tra user có phải Admin hay không.
     * Dùng trong AuthorizationFilter và JSP để hiển thị/ẩn nội dung theo role.
     */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    /**
     * Kiểm tra user có phải Staff hay không.
     */
    public boolean isStaff() {
        return "STAFF".equals(role);
    }

    /**
     * Kiểm tra user có phải Customer hay không.
     */
    public boolean isCustomer() {
        return "CUSTOMER".equals(role);
    }

    /**
     * Tính số tiền tương đương với số điểm hiện có.
     * Công thức: 1 điểm = 100 VNĐ giảm giá.
     */
    public double getPointsValueInVnd() {
        return pointsBalance * 100.0;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                ", tier='" + currentTierId + '\'' +
                ", points=" + pointsBalance +
                '}';
    }
}
