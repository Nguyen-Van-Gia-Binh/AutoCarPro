package com.autowash.autowashpro.model;

/**
 * Tier — Đại diện cho một hạng thẻ thành viên trong hệ thống loyalty.
 *
 * === LUỒNG DỮ LIỆU (DATA FLOW) ===
 * Bảng Tiers → TierDAO.getAllTiers() → List<Tier> → Servlet đặt vào request →
 * JSP hiển thị danh sách hạng thẻ trên giao diện (VD: Booking Wizard bước 2,
 * Admin Tier Settings).
 *
 * === VAI TRÒ TRONG HỆ THỐNG ===
 * - Mỗi User có một CurrentTierId trỏ tới một Tier.
 * - LoyaltyService dùng Multiplier của Tier để tính điểm tích lũy.
 * - BookingWizardServlet dùng BookingDays để giới hạn ngày đặt lịch.
 * - Bảng Tiers là bảng cấu hình (ít thay đổi), chứa 4 bản ghi cố định.
 */
public class Tier {

    private String tierId;       // Mã hạng (PK): "MEMBER", "SILVER", "GOLD", "PLATINUM"
    private String tierName;     // Tên hiển thị: "Thành Viên", "Bạc", "Vàng", "Bạch Kim"
    private int minWashes;       // Số lần rửa tối thiểu để đạt hạng này
    private double minSpent;     // Tổng chi tiêu tối thiểu (VNĐ) để đạt hạng
    private double multiplier;   // Hệ số nhân tích điểm (1.0, 1.1, 1.2, 1.3)
    private int bookingDays;     // Số ngày tối đa được phép đặt trước

    // === CONSTRUCTORS ===

    /**
     * Constructor mặc định — Cần thiết khi tạo đối tượng rỗng rồi set từng field.
     */
    public Tier() {
    }

    /**
     * Constructor đầy đủ — Dùng khi đọc dữ liệu từ ResultSet trong DAO.
     */
    public Tier(String tierId, String tierName, int minWashes, double minSpent,
                double multiplier, int bookingDays) {
        this.tierId = tierId;
        this.tierName = tierName;
        this.minWashes = minWashes;
        this.minSpent = minSpent;
        this.multiplier = multiplier;
        this.bookingDays = bookingDays;
    }

    // === GETTERS & SETTERS ===

    public String getTierId() {
        return tierId;
    }

    public void setTierId(String tierId) {
        this.tierId = tierId;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public int getMinWashes() {
        return minWashes;
    }

    public void setMinWashes(int minWashes) {
        this.minWashes = minWashes;
    }

    public double getMinSpent() {
        return minSpent;
    }

    public void setMinSpent(double minSpent) {
        this.minSpent = minSpent;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public int getBookingDays() {
        return bookingDays;
    }

    public void setBookingDays(int bookingDays) {
        this.bookingDays = bookingDays;
    }

    // === toString() ===
    // Hữu ích khi debug: System.out.println(tier) sẽ hiển thị thông tin đầy đủ
    @Override
    public String toString() {
        return "Tier{" +
                "tierId='" + tierId + '\'' +
                ", tierName='" + tierName + '\'' +
                ", multiplier=" + multiplier +
                ", bookingDays=" + bookingDays +
                '}';
    }
}
