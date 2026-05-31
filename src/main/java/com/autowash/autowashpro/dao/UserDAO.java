package com.autowash.autowashpro.dao;

import com.autowash.autowashpro.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO — Truy xuất và cập nhật dữ liệu bảng Users.
 *
 * === LUỒNG DỮ LIỆU (DATA FLOW) ===
 * 1. Đăng nhập: LoginServlet → UserDAO.findByPhone(phone) → trả về User →
 *    so sánh password → nếu đúng → lưu User vào HttpSession.
 * 2. Đăng ký: RegisterServlet → UserDAO.register(user) → INSERT vào DB.
 * 3. Checkout: LoyaltyService → UserDAO.updateTierAndPoints() → cập nhật
 *    PointsBalance, TotalSpent, TotalWashes, CurrentTierId.
 */
public class UserDAO extends BaseDAO {

    /**
     * Tìm user theo số điện thoại.
     * Dùng cho: Đăng nhập (phone là tài khoản login duy nhất).
     */
    public User findByPhone(String phoneNumber) throws SQLException {
        String sql = "SELECT UserId, PhoneNumber, Password, FullName, Role, "
                   + "CurrentTierId, PointsBalance, TotalSpent, TotalWashes, CreatedAt "
                   + "FROM Users WHERE PhoneNumber = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phoneNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Tìm user theo ID.
     * Dùng cho: LoyaltyService khi cần refresh thông tin user từ DB.
     */
    public User findById(int userId) throws SQLException {
        String sql = "SELECT UserId, PhoneNumber, Password, FullName, Role, "
                   + "CurrentTierId, PointsBalance, TotalSpent, TotalWashes, CreatedAt "
                   + "FROM Users WHERE UserId = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Đăng ký tài khoản mới.
     * 
     * Quy trình:
     * 1. Kiểm tra số điện thoại đã tồn tại chưa (ở Servlet trước khi gọi).
     * 2. INSERT user mới với role = CUSTOMER, tier = MEMBER, points = 0.
     * 3. Trả về true nếu thêm thành công.
     */
    public boolean register(User user) throws SQLException {
        String sql = "INSERT INTO Users (PhoneNumber, Password, FullName, Role, "
                   + "CurrentTierId, PointsBalance, TotalSpent, TotalWashes) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getPhoneNumber());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getCurrentTierId());
            ps.setInt(6, user.getPointsBalance());
            ps.setDouble(7, user.getTotalSpent());
            ps.setInt(8, user.getTotalWashes());

            // executeUpdate() trả về số dòng bị ảnh hưởng
            // Nếu > 0 → thêm thành công
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Cập nhật hạng thẻ, điểm thưởng, tổng chi tiêu và tổng lần rửa.
     * 
     * === ĐƯỢC GỌI TỪ ĐÂU? ===
     * LoyaltyService.processCheckout() gọi method này sau khi hoàn tất
     * tính toán điểm earn/redeem và kiểm tra auto-upgrade tier.
     *
     * === TẠI SAO UPDATE 4 TRƯỜNG CÙNG LÚC? ===
     * → Đảm bảo tính nhất quán: Tất cả thay đổi liên quan đến loyalty
     *   được cập nhật trong cùng một câu SQL, tránh tình trạng dữ liệu
     *   bị "giữa chừng" (VD: points đã trừ nhưng tier chưa update).
     *
     * @param conn Connection từ transaction (KHÔNG tự tạo mới)
     */
    public boolean updateTierAndPoints(Connection conn, int userId,
                                        String newTierId, int newPointsBalance,
                                        double newTotalSpent, int newTotalWashes) throws SQLException {
        String sql = "UPDATE Users SET CurrentTierId = ?, PointsBalance = ?, "
                   + "TotalSpent = ?, TotalWashes = ? "
                   + "WHERE UserId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newTierId);
            ps.setInt(2, newPointsBalance);
            ps.setDouble(3, newTotalSpent);
            ps.setInt(4, newTotalWashes);
            ps.setInt(5, userId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Lấy danh sách tất cả khách hàng (role = CUSTOMER).
     * Dùng cho: Admin Dashboard, thống kê, tìm kiếm.
     */
    public List<User> getAllCustomers() throws SQLException {
        List<User> customers = new ArrayList<>();
        String sql = "SELECT UserId, PhoneNumber, Password, FullName, Role, "
                   + "CurrentTierId, PointsBalance, TotalSpent, TotalWashes, CreatedAt "
                   + "FROM Users WHERE Role = 'CUSTOMER' ORDER BY CreatedAt DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                customers.add(mapResultSetToUser(rs));
            }
        }
        return customers;
    }

    /**
     * Đếm tổng số khách hàng theo hạng thẻ.
     * Dùng cho: Admin Dashboard — biểu đồ Tier Distribution (Chart.js Doughnut).
     */
    public int countByTier(String tierId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Users WHERE Role = 'CUSTOMER' AND CurrentTierId = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tierId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // ========================
    // PHƯƠNG THỨC NỘI BỘ
    // ========================

    /**
     * Chuyển đổi ResultSet → User object.
     * Tách riêng để tái sử dụng ở nhiều method (DRY principle).
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("UserId"),
                rs.getString("PhoneNumber"),
                rs.getString("Password"),
                rs.getString("FullName"),
                rs.getString("Role"),
                rs.getString("CurrentTierId"),
                rs.getInt("PointsBalance"),
                rs.getDouble("TotalSpent"),
                rs.getInt("TotalWashes"),
                rs.getTimestamp("CreatedAt")
        );
    }
}
