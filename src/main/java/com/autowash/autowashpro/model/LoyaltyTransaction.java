package com.autowash.autowashpro.model;

import java.util.Date;

/**
 * LoyaltyTransaction — Ghi lại lịch sử giao dịch tích/đổi điểm thưởng.
 *
 * === LUỒNG DỮ LIỆU (DATA FLOW) ===
 * 1. Tích điểm (EARN): Khi checkout → LoyaltyService.processCheckout() tính số điểm →
 *    LoyaltyTransactionDAO.insertTransaction(Type="EARN") → lưu vào DB.
 * 2. Đổi điểm (REDEEM): Khi checkout nếu khách dùng điểm giảm giá →
 *    LoyaltyTransactionDAO.insertTransaction(Type="REDEEM") → lưu vào DB.
 * 3. Hiển thị lịch sử: DashboardServlet hoặc trang Profile gọi
 *    LoyaltyTransactionDAO.getTransactionsByUserId() → JSP render danh sách.
 *
 * === TẠI SAO CẦN BẢNG NÀY? ===
 * - Đây là bảng "audit trail" (nhật ký kiểm toán) cho hệ thống điểm thưởng.
 * - Cho phép truy vết: điểm được tích từ hóa đơn nào, đổi khi nào.
 * - Bảng Users chỉ lưu PointsBalance (số dư hiện tại), không lưu lịch sử.
 */
public class LoyaltyTransaction {

    private int transactionId;         // ID tự tăng (PK)
    private int userId;                // Khách hàng (FK → Users)
    private String type;               // Loại giao dịch: "EARN" (tích) hoặc "REDEEM" (đổi)
    private int points;                // Số điểm giao dịch
    private double relatedBillAmount;  // Số tiền hóa đơn liên quan (null nếu là REDEEM)
    private Date createdAt;            // Thời điểm giao dịch

    // === CONSTRUCTORS ===

    public LoyaltyTransaction() {
    }

    /**
     * Constructor khi tạo giao dịch mới (từ LoyaltyService).
     */
    public LoyaltyTransaction(int userId, String type, int points, double relatedBillAmount) {
        this.userId = userId;
        this.type = type;
        this.points = points;
        this.relatedBillAmount = relatedBillAmount;
    }

    /**
     * Constructor đầy đủ — dùng khi đọc từ ResultSet.
     */
    public LoyaltyTransaction(int transactionId, int userId, String type,
                               int points, double relatedBillAmount, Date createdAt) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.type = type;
        this.points = points;
        this.relatedBillAmount = relatedBillAmount;
        this.createdAt = createdAt;
    }

    // === GETTERS & SETTERS ===

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public double getRelatedBillAmount() {
        return relatedBillAmount;
    }

    public void setRelatedBillAmount(double relatedBillAmount) {
        this.relatedBillAmount = relatedBillAmount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // === HELPER METHODS ===

    /**
     * Kiểm tra đây có phải giao dịch tích điểm không.
     */
    public boolean isEarn() {
        return "EARN".equals(type);
    }

    /**
     * Kiểm tra đây có phải giao dịch đổi điểm không.
     */
    public boolean isRedeem() {
        return "REDEEM".equals(type);
    }

    @Override
    public String toString() {
        return "LoyaltyTransaction{" +
                "id=" + transactionId +
                ", type='" + type + '\'' +
                ", points=" + points +
                ", billAmount=" + relatedBillAmount +
                '}';
    }
}
