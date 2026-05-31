package com.autowash.autowashpro.dao;

import com.autowash.autowashpro.model.Tier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * TierDAO — Truy xuất dữ liệu bảng Tiers (cấu hình hạng thẻ thành viên).
 *
 * === LUỒNG DỮ LIỆU (DATA FLOW) ===
 * LoyaltyService cần biết cấu hình hạng tiếp theo → gọi TierDAO.getNextTier()
 *   → trả về Tier object chứa MinWashes, MinSpent, Multiplier
 *   → LoyaltyService tính % tiến trình dựa trên cấu hình này.
 *
 * Admin Tier Settings → TierDAO.getAllTiers() → hiển thị bảng cấu hình.
 */
public class TierDAO extends BaseDAO {

    /**
     * Lấy tất cả hạng thẻ, sắp xếp theo MinWashes tăng dần.
     * Dùng cho: Admin Tier Settings, dropdown chọn hạng.
     *
     * SQL: SELECT * FROM Tiers ORDER BY MinWashes ASC
     */
    public List<Tier> getAllTiers() throws SQLException {
        List<Tier> tiers = new ArrayList<>();
        String sql = "SELECT TierId, TierName, MinWashes, MinSpent, Multiplier, BookingDays "
                   + "FROM Tiers ORDER BY MinWashes ASC";

        // try-with-resources: Connection, PreparedStatement, ResultSet đều tự đóng
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Tier tier = mapResultSetToTier(rs);
                tiers.add(tier);
            }
        }
        return tiers;
    }

    /**
     * Tìm hạng thẻ theo mã ID.
     * Dùng cho: LoyaltyService khi cần lấy Multiplier của hạng hiện tại.
     *
     * @param tierId Mã hạng (VD: "GOLD")
     * @return Tier hoặc null nếu không tìm thấy
     */
    public Tier getTierById(String tierId) throws SQLException {
        String sql = "SELECT TierId, TierName, MinWashes, MinSpent, Multiplier, BookingDays "
                   + "FROM Tiers WHERE TierId = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tierId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTier(rs);
                }
            }
        }
        return null;
    }

    /**
     * Lấy hạng thẻ tiếp theo (cao hơn hạng hiện tại).
     * 
     * === THUẬT TOÁN ===
     * Tìm hạng có MinWashes nhỏ nhất mà LỚN HƠN MinWashes của hạng hiện tại.
     * VD: Nếu hạng hiện tại là SILVER (MinWashes=5) → trả về GOLD (MinWashes=15).
     * Nếu hạng hiện tại là PLATINUM (cao nhất) → trả về null.
     *
     * @param currentTierId Mã hạng hiện tại
     * @return Tier tiếp theo, hoặc null nếu đã ở hạng cao nhất
     */
    public Tier getNextTier(String currentTierId) throws SQLException {
        // Bước 1: Lấy MinWashes của hạng hiện tại
        Tier currentTier = getTierById(currentTierId);
        if (currentTier == null) {
            return null;
        }

        // Bước 2: Tìm hạng có MinWashes gần nhất phía trên
        // TOP 1 + ORDER BY MinWashes ASC → lấy hạng thấp nhất mà vẫn cao hơn
        String sql = "SELECT TOP 1 TierId, TierName, MinWashes, MinSpent, Multiplier, BookingDays "
                   + "FROM Tiers "
                   + "WHERE MinWashes > ? "
                   + "ORDER BY MinWashes ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, currentTier.getMinWashes());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTier(rs);
                }
            }
        }
        // Nếu không tìm thấy → đã ở hạng cao nhất (PLATINUM)
        return null;
    }

    /**
     * Xác định hạng thẻ phù hợp nhất dựa trên số lần rửa và tổng chi tiêu.
     * 
     * === THUẬT TOÁN AUTO-UPGRADE ===
     * Tìm hạng CAO NHẤT mà user đủ điều kiện (đạt MinWashes HOẶC MinSpent).
     * Sắp xếp giảm dần theo MinWashes → TOP 1 → lấy hạng cao nhất phù hợp.
     *
     * @param totalWashes Tổng số lần rửa xe
     * @param totalSpent  Tổng chi tiêu (VNĐ)
     * @return Tier phù hợp nhất (luôn trả về ít nhất MEMBER)
     */
    public Tier determineTier(int totalWashes, double totalSpent) throws SQLException {
        // Điều kiện: MinWashes <= totalWashes HOẶC MinSpent <= totalSpent
        // Sắp xếp giảm dần để lấy hạng cao nhất thỏa mãn
        String sql = "SELECT TOP 1 TierId, TierName, MinWashes, MinSpent, Multiplier, BookingDays "
                   + "FROM Tiers "
                   + "WHERE MinWashes <= ? OR MinSpent <= ? "
                   + "ORDER BY MinWashes DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, totalWashes);
            ps.setDouble(2, totalSpent);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTier(rs);
                }
            }
        }
        // Fallback: Nếu không tìm thấy (trường hợp bất thường), trả về MEMBER
        return getTierById("MEMBER");
    }

    // ========================
    // PHƯƠNG THỨC NỘI BỘ
    // ========================

    /**
     * Chuyển đổi một dòng ResultSet thành đối tượng Tier.
     * 
     * Tại sao tách riêng phương thức này?
     * → Nguyên tắc DRY: Tránh viết lại code mapping ở mỗi method truy vấn.
     * → Nếu bảng Tiers thêm cột mới, chỉ cần sửa ở đây.
     */
    private Tier mapResultSetToTier(ResultSet rs) throws SQLException {
        return new Tier(
                rs.getString("TierId"),
                rs.getString("TierName"),
                rs.getInt("MinWashes"),
                rs.getDouble("MinSpent"),
                rs.getDouble("Multiplier"),
                rs.getInt("BookingDays")
        );
    }
}
