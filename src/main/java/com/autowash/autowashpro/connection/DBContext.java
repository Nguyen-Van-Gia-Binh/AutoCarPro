package com.autowash.autowashpro.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBContext — Lớp quản lý kết nối cơ sở dữ liệu SQL Server.
 *
 * === LUỒNG DỮ LIỆU (DATA FLOW) ===
 * 1. Khi bất kỳ lớp DAO nào cần truy vấn database, nó gọi DBContext.getConnection().
 * 2. Phương thức này nạp JDBC Driver của SQL Server vào bộ nhớ.
 * 3. Sau đó tạo và trả về một đối tượng Connection mới tới database AutoWashProDB.
 * 4. Lớp DAO sẽ dùng Connection này để tạo PreparedStatement và thực thi SQL.
 * 5. Sau khi xong, Connection được đóng tự động nhờ try-with-resources ở lớp DAO.
 *
 * === TẠI SAO LÀM NHƯ VẬY? ===
 * - Tách riêng logic kết nối DB ra khỏi logic truy vấn → dễ bảo trì.
 * - Nếu đổi database (VD: từ SQL Server sang MySQL), chỉ cần sửa file này.
 * - Phương thức main() cho phép test kết nối độc lập mà không cần chạy Tomcat.
 */
public class DBContext {

    // === CẤU HÌNH KẾT NỐI ===
    // Thay đổi các giá trị bên dưới cho phù hợp với máy của bạn.
    // - serverName: Tên máy chủ SQL Server (thường là "localhost" hoặc tên instance)
    // - databaseName: Tên database vừa tạo bằng script SQL
    // - user/password: Tài khoản SQL Server Authentication
    private static final String SERVER_NAME   = "localhost";
    private static final String DATABASE_NAME = "AutoWashProDB";
    private static final String USER          = "sa";
    private static final String PASSWORD      = "12345";

    // Chuỗi kết nối JDBC cho SQL Server
    // Giải thích tham số:
    // - encrypt=true: Mã hóa kết nối (bảo mật)
    // - trustServerCertificate=true: Chấp nhận chứng chỉ tự ký (dùng cho dev/local)
    private static final String CONNECTION_URL = String.format(
            "jdbc:sqlserver://%s:1433;databaseName=%s;encrypt=true;trustServerCertificate=true",
            SERVER_NAME, DATABASE_NAME
    );

    /**
     * Tạo và trả về một kết nối mới tới SQL Server.
     *
     * Cách sử dụng trong DAO:
     * <pre>
     *     try (Connection conn = DBContext.getConnection()) {
     *         // Sử dụng conn để truy vấn...
     *     } // Connection tự động đóng khi ra khỏi try block
     * </pre>
     *
     * @return Connection — đối tượng kết nối JDBC
     * @throws SQLException nếu không thể kết nối tới database
     * @throws ClassNotFoundException nếu không tìm thấy JDBC Driver
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // Bước 1: Nạp JDBC Driver vào bộ nhớ JVM
        // Driver này đã được thêm vào pom.xml (mssql-jdbc)
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        // Bước 2: Tạo kết nối mới và trả về
        return DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
    }

    /**
     * Phương thức main() để test kết nối database.
     * Chạy trực tiếp file này (Run File) để kiểm tra kết nối thành công hay thất bại.
     *
     * Kết quả mong đợi:
     *   ✅ Kết nối SQL Server thành công!
     *   📦 Database: AutoWashProDB
     */
    public static void main(String[] args) {
        System.out.println("🔌 Đang kiểm tra kết nối tới SQL Server...");
        System.out.println("   Server: " + SERVER_NAME);
        System.out.println("   Database: " + DATABASE_NAME);
        System.out.println();

        // Dùng try-with-resources để tự động đóng Connection sau khi test
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Kết nối SQL Server thành công!");
                System.out.println("📦 Database: " + conn.getCatalog());
                System.out.println("🔗 URL: " + CONNECTION_URL);
            }
        } catch (ClassNotFoundException e) {
            // Lỗi này xảy ra khi không tìm thấy JDBC Driver trong classpath
            System.err.println("❌ Không tìm thấy JDBC Driver!");
            System.err.println("   Hãy kiểm tra dependency mssql-jdbc trong pom.xml.");
            e.printStackTrace();
        } catch (SQLException e) {
            // Lỗi này xảy ra khi không thể kết nối tới SQL Server
            System.err.println("❌ Không thể kết nối tới SQL Server!");
            System.err.println("   Kiểm tra: SQL Server đang chạy? Username/password đúng?");
            System.err.println("   Chi tiết lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
