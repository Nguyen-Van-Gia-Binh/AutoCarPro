package com.autowash.autowashpro.dao;

import com.autowash.autowashpro.model.Booking;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * BookingDAO — Truy xuất và quản lý lịch đặt rửa xe.
 *
 * === LUỒNG DỮ LIỆU (DATA FLOW) ===
 * 1. Tạo booking: BookingWizardServlet (POST bước 4) → createBooking() → INSERT DB.
 * 2. Dashboard khách: DashboardServlet → getUpcomingByUserId() → Card "Upcoming Booking".
 * 3. Admin Today's Bookings: ManageBookingsServlet → getBookingsByDate() → bảng danh sách.
 * 4. AJAX check slot: BookingWizardServlet (GET bước 3) → countByDateAndSlot() →
 *    trả về JSON số lượng đã đặt → JS disable slot nếu hết chỗ.
 * 5. Checkout: CheckoutServlet → updateStatus("COMPLETED") → LoyaltyService xử lý.
 */
public class BookingDAO extends BaseDAO {

    /**
     * Lấy danh sách booking của một khách hàng, sắp xếp theo ngày mới nhất.
     */
    public List<Booking> getBookingsByUserId(int userId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        // JOIN với LicensePlates để lấy biển số xe hiển thị trên giao diện
        String sql = "SELECT b.BookingId, b.UserId, b.PlateId, b.BookingDate, "
                   + "b.TimeSlot, b.Status, b.Notes, b.CreatedAt, "
                   + "lp.PlateNumber, lp.VehicleModel "
                   + "FROM Bookings b "
                   + "INNER JOIN LicensePlates lp ON b.PlateId = lp.PlateId "
                   + "WHERE b.UserId = ? "
                   + "ORDER BY b.BookingDate DESC, b.TimeSlot ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    // Gán thêm thông tin xe từ JOIN
                    booking.setPlateNumber(rs.getString("PlateNumber"));
                    booking.setVehicleModel(rs.getString("VehicleModel"));
                    bookings.add(booking);
                }
            }
        }
        return bookings;
    }

    /**
     * Lấy booking gần nhất đang PENDING của khách hàng.
     * Dùng cho: Card "Upcoming Booking" trên Dashboard.
     * 
     * TOP 1 + BookingDate >= hôm nay + Status = PENDING → booking gần nhất sắp tới.
     */
    public Booking getUpcomingByUserId(int userId) throws SQLException {
        String sql = "SELECT TOP 1 b.BookingId, b.UserId, b.PlateId, b.BookingDate, "
                   + "b.TimeSlot, b.Status, b.Notes, b.CreatedAt, "
                   + "lp.PlateNumber, lp.VehicleModel "
                   + "FROM Bookings b "
                   + "INNER JOIN LicensePlates lp ON b.PlateId = lp.PlateId "
                   + "WHERE b.UserId = ? AND b.Status = 'PENDING' "
                   + "AND b.BookingDate >= CAST(GETDATE() AS DATE) "
                   + "ORDER BY b.BookingDate ASC, b.TimeSlot ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    booking.setPlateNumber(rs.getString("PlateNumber"));
                    booking.setVehicleModel(rs.getString("VehicleModel"));
                    return booking;
                }
            }
        }
        return null;
    }

    /**
     * Lấy tất cả booking trong một ngày cụ thể (dành cho Admin).
     * JOIN thêm bảng Users để lấy tên khách hàng hiển thị.
     */
    public List<Booking> getBookingsByDate(Date bookingDate) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.BookingId, b.UserId, b.PlateId, b.BookingDate, "
                   + "b.TimeSlot, b.Status, b.Notes, b.CreatedAt, "
                   + "lp.PlateNumber, lp.VehicleModel, u.FullName AS CustomerName "
                   + "FROM Bookings b "
                   + "INNER JOIN LicensePlates lp ON b.PlateId = lp.PlateId "
                   + "INNER JOIN Users u ON b.UserId = u.UserId "
                   + "WHERE b.BookingDate = ? "
                   + "ORDER BY b.TimeSlot ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, bookingDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    booking.setPlateNumber(rs.getString("PlateNumber"));
                    booking.setVehicleModel(rs.getString("VehicleModel"));
                    booking.setCustomerName(rs.getString("CustomerName"));
                    bookings.add(booking);
                }
            }
        }
        return bookings;
    }

    /**
     * Tạo booking mới.
     * 
     * @return true nếu INSERT thành công
     */
    public boolean createBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO Bookings (UserId, PlateId, BookingDate, TimeSlot, Status, Notes) "
                   + "VALUES (?, ?, ?, ?, 'PENDING', ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, booking.getUserId());
            ps.setInt(2, booking.getPlateId());
            ps.setDate(3, new Date(booking.getBookingDate().getTime()));
            ps.setString(4, booking.getTimeSlot());
            ps.setString(5, booking.getNotes());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Cập nhật trạng thái booking.
     * 
     * Được gọi khi:
     * - Checkout thành công → status = "COMPLETED"
     * - Khách/admin hủy → status = "CANCELLED"
     *
     * @param conn Connection từ transaction (không tự tạo mới)
     */
    public boolean updateStatus(Connection conn, int bookingId, String newStatus) throws SQLException {
        String sql = "UPDATE Bookings SET Status = ? WHERE BookingId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Đếm số booking đã đặt trong một ngày + khung giờ cụ thể.
     * 
     * === DÙNG CHO AJAX CHECK SLOT ===
     * Booking Wizard bước 3: Khi khách chọn ngày → JS gửi AJAX tới servlet →
     * servlet gọi countByDateAndSlot() → nếu >= MAX_CAPACITY → trả về "Hết chỗ".
     *
     * Chỉ đếm booking có Status != 'CANCELLED' (booking bị hủy không chiếm chỗ).
     */
    public int countByDateAndSlot(Date bookingDate, String timeSlot) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Bookings "
                   + "WHERE BookingDate = ? AND TimeSlot = ? AND Status != 'CANCELLED'";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, bookingDate);
            ps.setString(2, timeSlot);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Tìm booking theo ID (dành cho Checkout).
     */
    public Booking getBookingById(int bookingId) throws SQLException {
        String sql = "SELECT b.BookingId, b.UserId, b.PlateId, b.BookingDate, "
                   + "b.TimeSlot, b.Status, b.Notes, b.CreatedAt, "
                   + "lp.PlateNumber, lp.VehicleModel, u.FullName AS CustomerName "
                   + "FROM Bookings b "
                   + "INNER JOIN LicensePlates lp ON b.PlateId = lp.PlateId "
                   + "INNER JOIN Users u ON b.UserId = u.UserId "
                   + "WHERE b.BookingId = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    booking.setPlateNumber(rs.getString("PlateNumber"));
                    booking.setVehicleModel(rs.getString("VehicleModel"));
                    booking.setCustomerName(rs.getString("CustomerName"));
                    return booking;
                }
            }
        }
        return null;
    }

    /**
     * Đếm tổng số booking PENDING (dùng cho Admin Dashboard stat card).
     */
    public int countPendingBookings() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Bookings WHERE Status = 'PENDING'";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // ========================
    // PHƯƠNG THỨC NỘI BỘ
    // ========================

    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getInt("BookingId"),
                rs.getInt("UserId"),
                rs.getInt("PlateId"),
                rs.getDate("BookingDate"),
                rs.getString("TimeSlot"),
                rs.getString("Status"),
                rs.getString("Notes"),
                rs.getTimestamp("CreatedAt")
        );
    }
}
