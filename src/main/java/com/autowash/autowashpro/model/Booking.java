package com.autowash.autowashpro.model;

import java.util.Date;

/**
 * Booking — Đại diện cho một lịch đặt rửa xe.
 *
 * === LUỒNG DỮ LIỆU (DATA FLOW) ===
 * 1. Tạo booking: BookingWizardServlet (bước 4 xác nhận) → BookingDAO.createBooking() →
 *    INSERT vào bảng Bookings với Status = "PENDING".
 * 2. Dashboard khách: DashboardServlet gọi BookingDAO.getUpcomingByUserId() →
 *    trả về booking gần nhất có Status = "PENDING" → hiển thị trên card "Upcoming Booking".
 * 3. Admin quản lý: ManageBookingsServlet load danh sách booking theo ngày →
 *    Nhân viên thực hiện checkout → CheckoutServlet cập nhật Status = "COMPLETED".
 * 4. Hủy booking: Khách hàng hủy → Status = "CANCELLED".
 *
 * === VÒNG ĐỜI TRẠNG THÁI (STATUS LIFECYCLE) ===
 *   PENDING → COMPLETED (khi checkout thành công)
 *   PENDING → CANCELLED (khi khách hủy hoặc admin hủy)
 */
public class Booking {

    private int bookingId;         // ID tự tăng (PK)
    private int userId;            // Khách đặt lịch (FK → Users)
    private int plateId;           // Xe được rửa (FK → LicensePlates)
    private Date bookingDate;      // Ngày hẹn rửa xe (chỉ lấy ngày, không giờ)
    private String timeSlot;       // Khung giờ (VD: "09:00-10:00")
    private String status;         // Trạng thái: "PENDING", "COMPLETED", "CANCELLED"
    private String notes;          // Ghi chú từ khách hàng (có thể null)
    private Date createdAt;        // Thời điểm tạo booking

    // --- Các trường bổ trợ (không có trong DB, dùng cho hiển thị trên JSP) ---
    private String plateNumber;    // Biển số xe (JOIN từ LicensePlates khi cần hiển thị)
    private String customerName;   // Tên khách (JOIN từ Users khi hiển thị ở Admin)
    private String vehicleModel;   // Loại xe (JOIN từ LicensePlates)

    // === CONSTRUCTORS ===

    public Booking() {
    }

    /**
     * Constructor khi tạo booking mới từ Booking Wizard.
     */
    public Booking(int userId, int plateId, Date bookingDate, String timeSlot, String notes) {
        this.userId = userId;
        this.plateId = plateId;
        this.bookingDate = bookingDate;
        this.timeSlot = timeSlot;
        this.status = "PENDING";
        this.notes = notes;
    }

    /**
     * Constructor đầy đủ — dùng khi đọc từ ResultSet.
     */
    public Booking(int bookingId, int userId, int plateId, Date bookingDate,
                   String timeSlot, String status, String notes, Date createdAt) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.plateId = plateId;
        this.bookingDate = bookingDate;
        this.timeSlot = timeSlot;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    // === GETTERS & SETTERS ===

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPlateId() {
        return plateId;
    }

    public void setPlateId(int plateId) {
        this.plateId = plateId;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // --- Getters/Setters cho trường bổ trợ (dùng khi JOIN) ---

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    // === HELPER METHODS ===

    /**
     * Kiểm tra booking đang ở trạng thái chờ xử lý hay không.
     */
    public boolean isPending() {
        return "PENDING".equals(status);
    }

    /**
     * Kiểm tra booking đã hoàn thành hay chưa.
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", date=" + bookingDate +
                ", timeSlot='" + timeSlot + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
