package com.autowash.autowashpro.model;

import java.util.Date;

/**
 * LicensePlate — Đại diện cho một biển số xe đã đăng ký của khách hàng.
 *
 * === LUỒNG DỮ LIỆU (DATA FLOW) ===
 * 1. Trang "Xe của tôi": MyCarsServlet gọi LicensePlateDAO.getPlatesByUserId(userId) →
 *    trả về List<LicensePlate> → đặt vào request → JSP render danh sách xe dạng Card.
 * 2. Booking Wizard bước 1: BookingWizardServlet load danh sách xe của user hiện tại →
 *    khách chọn xe muốn rửa → plateId được gửi qua form ẩn sang bước tiếp theo.
 * 3. Thêm xe mới: Khách nhập biển số + tên xe → MyCarsServlet validate regex biển số VN →
 *    LicensePlateDAO.addPlate() → lưu vào DB.
 *
 * === REGEX BIỂN SỐ VIỆT NAM ===
 * Pattern: ^\d{2}[A-Z]\d{1}-\d{4,5}$|^\d{2}[A-Z]\d{1}-\d{3}\.\d{2}$
 * Ví dụ hợp lệ: "30A1-1234", "51F1-999.99", "43C2-88888"
 */
public class LicensePlate {

    private int plateId;           // ID tự tăng (PK)
    private int userId;            // Chủ xe (FK → Users)
    private String plateNumber;    // Biển số xe (UNIQUE, VD: "30A1-1234")
    private String vehicleModel;   // Loại/tên xe (VD: "Toyota Camry 2024")
    private Date createdAt;        // Thời điểm đăng ký xe

    // === CONSTRUCTORS ===

    public LicensePlate() {
    }

    /**
     * Constructor khi thêm xe mới — chỉ cần userId, biển số, và tên xe.
     */
    public LicensePlate(int userId, String plateNumber, String vehicleModel) {
        this.userId = userId;
        this.plateNumber = plateNumber;
        this.vehicleModel = vehicleModel;
    }

    /**
     * Constructor đầy đủ — dùng khi đọc từ ResultSet.
     */
    public LicensePlate(int plateId, int userId, String plateNumber,
                        String vehicleModel, Date createdAt) {
        this.plateId = plateId;
        this.userId = userId;
        this.plateNumber = plateNumber;
        this.vehicleModel = vehicleModel;
        this.createdAt = createdAt;
    }

    // === GETTERS & SETTERS ===

    public int getPlateId() {
        return plateId;
    }

    public void setPlateId(int plateId) {
        this.plateId = plateId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "LicensePlate{" +
                "plateId=" + plateId +
                ", plateNumber='" + plateNumber + '\'' +
                ", vehicleModel='" + vehicleModel + '\'' +
                '}';
    }
}
