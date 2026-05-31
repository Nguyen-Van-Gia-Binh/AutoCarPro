package com.autowash.autowashpro.dao;

import com.autowash.autowashpro.model.LoyaltyTransaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * LoyaltyTransactionDAO — Truy xuất lịch sử giao dịch tích/đổi điểm thưởng.
 *
 * === LUỒNG DỮ LIỆU (DATA FLOW) ===
 * 1. Checkout: LoyaltyService.processCheckout() → insertTransaction(Type="EARN") →
 *    ghi lại số điểm tích được. Nếu khách đổi điểm → insertTransaction(Type="REDEEM").
 * 2. Dashboard/Profile: Servlet → getTransactionsByUserId() → JSP render lịch sử.
 *
 * === LƯU Ý QUAN TRỌNG ===
 * Method insertTransaction() nhận Connection từ bên ngoài (transaction) vì nó
 * được gọi trong cùng một Database Transaction với nhiều thao tác khác.
 * Nếu tự tạo Connection mới → sẽ không thuộc transaction → mất tính toàn vẹn.
 */
public class LoyaltyTransactionDAO extends BaseDAO {

    /**
     * Lấy lịch sử giao dịch điểm của khách hàng, mới nhất trước.
     */
    public List<LoyaltyTransaction> getTransactionsByUserId(int userId) throws SQLException {
        List<LoyaltyTransaction> transactions = new ArrayList<>();
        String sql = "SELECT TransactionId, UserId, Type, Points, RelatedBillAmount, CreatedAt "
                   + "FROM LoyaltyTransactions WHERE UserId = ? "
                   + "ORDER BY CreatedAt DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        return transactions;
    }

    /**
     * Chèn bản ghi giao dịch điểm mới.
     *
     * === QUAN TRỌNG: Dùng Connection từ transaction ===
     * Phương thức này KHÔNG tự tạo Connection mới.
     * Nó nhận Connection từ LoyaltyService.processCheckout() để đảm bảo
     * tất cả các thao tác (update user, insert transaction, update booking)
     * nằm trong cùng một transaction — nếu bất kỳ bước nào lỗi → rollback tất cả.
     *
     * @param conn Connection đang trong transaction
     * @param transaction Đối tượng LoyaltyTransaction cần chèn
     * @return true nếu INSERT thành công
     */
    public boolean insertTransaction(Connection conn, LoyaltyTransaction transaction) throws SQLException {
        String sql = "INSERT INTO LoyaltyTransactions (UserId, Type, Points, RelatedBillAmount) "
                   + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transaction.getUserId());
            ps.setString(2, transaction.getType());
            ps.setInt(3, transaction.getPoints());
            ps.setDouble(4, transaction.getRelatedBillAmount());

            return ps.executeUpdate() > 0;
        }
    }

    // ========================
    // PHƯƠNG THỨC NỘI BỘ
    // ========================

    private LoyaltyTransaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new LoyaltyTransaction(
                rs.getInt("TransactionId"),
                rs.getInt("UserId"),
                rs.getString("Type"),
                rs.getInt("Points"),
                rs.getDouble("RelatedBillAmount"),
                rs.getTimestamp("CreatedAt")
        );
    }
}
