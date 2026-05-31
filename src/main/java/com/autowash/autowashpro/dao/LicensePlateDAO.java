package com.autowash.autowashpro.dao;

import com.autowash.autowashpro.model.LicensePlate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * LicensePlateDAO — Truy xuất và quản lý biển số xe của khách hàng.
 *
 * === LUỒNG DỮ LIỆU (DATA FLOW) ===
 * 1. Trang "Xe của tôi": MyCarsServlet → getPlatesByUserId() → JSP render cards.
 * 2. Booking Wizard bước 1: BookingWizardServlet → getPlatesByUserId() → JSP hiển thị
 *    danh sách xe để khách chọn.
 * 3. Thêm xe: MyCarsServlet (POST) → validate regex biển số → addPlate() → redirect.
 * 4. Xóa xe: MyCarsServlet (POST action=delete) → deletePlate() → redirect.
 */
public class LicensePlateDAO extends BaseDAO {

    /**
     * Lấy danh sách tất cả xe của một khách hàng.
     */
    public List<LicensePlate> getPlatesByUserId(int userId) throws SQLException {
        List<LicensePlate> plates = new ArrayList<>();
        String sql = "SELECT PlateId, UserId, PlateNumber, VehicleModel, CreatedAt "
                   + "FROM LicensePlates WHERE UserId = ? ORDER BY CreatedAt DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    plates.add(new LicensePlate(
                            rs.getInt("PlateId"),
                            rs.getInt("UserId"),
                            rs.getString("PlateNumber"),
                            rs.getString("VehicleModel"),
                            rs.getTimestamp("CreatedAt")
                    ));
                }
            }
        }
        return plates;
    }

    /**
     * Tìm biển số xe theo ID.
     * Dùng cho: BookingWizardServlet khi cần hiển thị thông tin xe đã chọn.
     */
    public LicensePlate getPlateById(int plateId) throws SQLException {
        String sql = "SELECT PlateId, UserId, PlateNumber, VehicleModel, CreatedAt "
                   + "FROM LicensePlates WHERE PlateId = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, plateId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new LicensePlate(
                            rs.getInt("PlateId"),
                            rs.getInt("UserId"),
                            rs.getString("PlateNumber"),
                            rs.getString("VehicleModel"),
                            rs.getTimestamp("CreatedAt")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Thêm xe mới cho khách hàng.
     * 
     * Lưu ý: Biển số (PlateNumber) có constraint UNIQUE trong DB,
     * nếu trùng sẽ ném SQLException → Servlet bắt lỗi và thông báo cho user.
     *
     * @return true nếu thêm thành công
     */
    public boolean addPlate(LicensePlate plate) throws SQLException {
        String sql = "INSERT INTO LicensePlates (UserId, PlateNumber, VehicleModel) "
                   + "VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, plate.getUserId());
            ps.setString(2, plate.getPlateNumber());
            ps.setString(3, plate.getVehicleModel());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Xóa xe của khách hàng.
     * 
     * Bảo mật: Kiểm tra cả userId để đảm bảo khách chỉ xóa được xe của chính mình.
     * Nếu không kiểm tra userId → khách có thể giả mạo plateId để xóa xe người khác.
     */
    public boolean deletePlate(int plateId, int userId) throws SQLException {
        String sql = "DELETE FROM LicensePlates WHERE PlateId = ? AND UserId = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, plateId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Kiểm tra biển số xe đã tồn tại trong hệ thống chưa.
     * Dùng để validate trước khi INSERT, tránh lỗi UNIQUE constraint.
     */
    public boolean isPlateNumberExists(String plateNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM LicensePlates WHERE PlateNumber = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, plateNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
