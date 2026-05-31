package com.autowash.autowashpro.dao;

import com.autowash.autowashpro.connection.DBContext;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * BaseDAO — Lớp cha chứa các phương thức JDBC bổ trợ dùng chung cho tất cả DAO.
 *
 * === LUỒNG DỮ LIỆU (DATA FLOW) ===
 * Tất cả các DAO con (UserDAO, BookingDAO, v.v.) kế thừa BaseDAO để:
 * 1. Gọi getConnection() → nhận Connection từ DBContext.
 * 2. Sử dụng Connection để tạo PreparedStatement → thực thi SQL.
 * 3. Connection được đóng tự động nhờ try-with-resources ở mỗi method trong DAO con.
 *
 * === TẠI SAO CẦN LỚP NÀY? ===
 * - Nguyên tắc DRY: Không phải viết lại code lấy Connection ở mỗi DAO.
 * - Nguyên tắc SRP: Lớp này chỉ chịu trách nhiệm cung cấp kết nối DB.
 * - Nếu muốn đổi cách kết nối (VD: dùng Connection Pooling), chỉ sửa ở đây.
 */
public abstract class BaseDAO {

    /**
     * Lấy kết nối tới SQL Server từ DBContext.
     *
     * Cách dùng trong DAO con:
     * <pre>
     *     try (Connection conn = getConnection()) {
     *         PreparedStatement ps = conn.prepareStatement("SELECT ...");
     *         ResultSet rs = ps.executeQuery();
     *         // Xử lý kết quả...
     *     }
     * </pre>
     *
     * @return Connection — kết nối JDBC
     * @throws SQLException nếu không thể kết nối
     */
    protected Connection getConnection() throws SQLException {
        try {
            return DBContext.getConnection();
        } catch (ClassNotFoundException e) {
            // Bọc ClassNotFoundException thành SQLException để đơn giản hóa
            // xử lý lỗi ở lớp DAO con (chỉ cần catch 1 loại exception)
            throw new SQLException("Không tìm thấy JDBC Driver. Kiểm tra pom.xml.", e);
        }
    }
}
