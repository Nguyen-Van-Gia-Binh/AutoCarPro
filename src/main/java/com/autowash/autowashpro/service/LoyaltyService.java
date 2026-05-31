package com.autowash.autowashpro.service;

import com.autowash.autowashpro.connection.DBContext;
import com.autowash.autowashpro.dao.*;
import com.autowash.autowashpro.model.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * LoyaltyService — "Trái Tim" của hệ thống: Thuật toán Loyalty Engine.
 *
 * === VAI TRÒ ===
 * Lớp này chứa toàn bộ logic nghiệp vụ cốt lõi liên quan đến:
 * 1. Tính phần trăm tiến trình thăng hạng (Next Reward Progress)
 * 2. Tạo dòng thông báo động (Visual Text) cho giao diện
 * 3. Xử lý thanh toán (Checkout) với Database Transaction đầy đủ
 *
 * === LUỒNG DỮ LIỆU TỔNG QUAN ===
 * DashboardServlet → LoyaltyService.calculateNextRewardProgress(user) →
 *   trả về RewardProgress object → JSP hiển thị thanh tiến trình + thông báo.
 *
 * CheckoutServlet → LoyaltyService.processCheckout(bookingId, billAmount, redeemPoints) →
 *   Database Transaction (6 bước) → cập nhật tất cả bảng liên quan.
 *
 * === TẠI SAO TÁCH SERVICE RA KHỎI SERVLET? ===
 * → Nguyên tắc SRP: Servlet chỉ xử lý HTTP request/response.
 * → Service chứa logic nghiệp vụ phức tạp, có thể test độc lập.
 * → Nếu cần thay đổi thuật toán, chỉ sửa ở đây mà không ảnh hưởng Controller.
 */
public class LoyaltyService {

    // Các DAO được sử dụng bởi service
    private TierDAO tierDAO = new TierDAO();
    private UserDAO userDAO = new UserDAO();
    private BookingDAO bookingDAO = new BookingDAO();
    private LoyaltyTransactionDAO transactionDAO = new LoyaltyTransactionDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();

    // Hằng số quy đổi điểm → tiền
    // 1 điểm = 100 VNĐ giảm giá khi thanh toán
    private static final double POINTS_TO_VND_RATE = 100.0;

    // Tỉ lệ tích điểm cơ sở: chi 1.000 VNĐ = 1 điểm cơ sở
    private static final double VND_PER_BASE_POINT = 1000.0;

    // Mốc điểm mục tiêu cho PLATINUM (khi đã ở hạng cao nhất)
    private static final int PLATINUM_REWARD_TARGET_POINTS = 300;

    // Công suất tối đa mỗi slot (số xe tối đa trong 1 khung giờ)
    // Có thể điều chỉnh tùy quy mô cửa hàng
    public static final int MAX_SLOT_CAPACITY = 5;

    // ======================================================
    // 1. THUẬT TOÁN: Tiến trình Phần thưởng Kế tiếp
    // ======================================================

    /**
     * Tính phần trăm tiến trình thăng hạng của khách hàng.
     *
     * === THUẬT TOÁN CHI TIẾT ===
     * Bước 1: Xác định hạng thẻ hiện tại và hạng tiếp theo.
     * Bước 2: Nếu đã ở PLATINUM (hạng cao nhất) → mục tiêu là "Quà Nano" (300 điểm).
     * Bước 3: Tính 2 chỉ số phần trăm:
     *   - p_wash = min(100%, TotalWashes / TargetWashes × 100%)
     *   - p_spent = min(100%, TotalSpent / TargetSpent × 100%)
     * Bước 4: Lấy chỉ số CAO HƠN làm phần trăm tổng:
     *   - P = max(p_wash, p_spent)
     *   → Lý do: Khuyến khích khách bằng chỉ số gần đạt nhất.
     *
     * @param user Đối tượng User với thông tin hiện tại
     * @return RewardProgress chứa %, thông báo, và thông tin hạng tiếp theo
     */
    public RewardProgress calculateNextRewardProgress(User user) throws SQLException {
        String currentTierId = user.getCurrentTierId();
        RewardProgress progress = new RewardProgress();

        // Trường hợp đặc biệt: Hạng PLATINUM (đỉnh) → Mục tiêu là "Quà Nano Cơ Bản"
        if ("PLATINUM".equals(currentTierId)) {
            // Tính % dựa trên PointsBalance / 300 (mốc đổi quà)
            double percentage = Math.min(100.0,
                    (double) user.getPointsBalance() / PLATINUM_REWARD_TARGET_POINTS * 100.0);

            int remainingPoints = Math.max(0, PLATINUM_REWARD_TARGET_POINTS - user.getPointsBalance());

            progress.setPercentage(percentage);
            progress.setNextTierName("Quà Nano Cơ Bản");
            progress.setMessage(String.format(
                    "Bạn đang ở hạng cao nhất! Còn %d điểm nữa để đổi Quà Nano Cơ Bản.",
                    remainingPoints));
            progress.setMaxTier(true);
            return progress;
        }

        // Lấy cấu hình hạng tiếp theo từ DB
        Tier nextTier = tierDAO.getNextTier(currentTierId);
        if (nextTier == null) {
            // Fallback: Nếu không tìm thấy hạng tiếp theo
            progress.setPercentage(100.0);
            progress.setNextTierName("N/A");
            progress.setMessage("Bạn đã đạt hạng cao nhất!");
            progress.setMaxTier(true);
            return progress;
        }

        // === Tính phần trăm theo lượt rửa ===
        // Công thức: p_wash = min(100%, TotalWashes / TargetWashes × 100%)
        double pWash = Math.min(100.0,
                (double) user.getTotalWashes() / nextTier.getMinWashes() * 100.0);

        // === Tính phần trăm theo chi tiêu ===
        // Công thức: p_spent = min(100%, TotalSpent / TargetSpent × 100%)
        double pSpent = Math.min(100.0,
                user.getTotalSpent() / nextTier.getMinSpent() * 100.0);

        // === Lấy chỉ số cao hơn ===
        // Lý do: Hiển thị chỉ số gần đạt nhất để khuyến khích khách hàng
        double percentage = Math.max(pWash, pSpent);

        // === Tạo dòng thông báo động ===
        int remainingWashes = Math.max(0, nextTier.getMinWashes() - user.getTotalWashes());
        double remainingSpent = Math.max(0, nextTier.getMinSpent() - user.getTotalSpent());

        // Format số tiền theo kiểu Việt Nam (VD: 9.000.000)
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedSpent = nf.format((long) remainingSpent);

        String message = String.format(
                "Bạn chỉ còn cách hạng %s %d lượt rửa hoặc %s VNĐ chi tiêu để nhận ưu đãi nhân %.1f lần điểm thưởng!",
                nextTier.getTierName(),
                remainingWashes,
                formattedSpent,
                nextTier.getMultiplier()
        );

        progress.setPercentage(percentage);
        progress.setNextTierName(nextTier.getTierName());
        progress.setNextTierId(nextTier.getTierId());
        progress.setMessage(message);
        progress.setRemainingWashes(remainingWashes);
        progress.setRemainingSpent(remainingSpent);
        progress.setMaxTier(false);

        return progress;
    }

    // ======================================================
    // 2. THUẬT TOÁN: Checkout Realtime (Database Transaction)
    // ======================================================

    /**
     * Xử lý thanh toán — Database Transaction đầy đủ 6 bước.
     *
     * === TỔNG QUAN TRANSACTION ===
     * Tất cả 6 bước bên dưới được thực hiện trong cùng 1 transaction.
     * Nếu BẤT KỲ bước nào lỗi → ROLLBACK tất cả → không có gì bị thay đổi.
     * Điều này đảm bảo tính toàn vẹn dữ liệu (ACID properties).
     *
     * === 6 BƯỚC CỤ THỂ ===
     * a. Cập nhật Booking → Status = "COMPLETED"
     * b. Tính toán điểm earn/redeem, cập nhật ví điểm User
     * c. Cộng dồn TotalSpent và TotalWashes
     * d. Chèn LoyaltyTransactions (EARN + REDEEM)
     * e. Kiểm tra auto-upgrade tier
     * f. Tạo Notification tự động
     *
     * @param bookingId    ID booking đang checkout
     * @param billAmount   Tổng tiền hóa đơn gốc (A)
     * @param redeemPoints Số điểm khách muốn đổi giảm giá (P_redeem)
     * @return CheckoutResult chứa thông tin kết quả thanh toán
     */
    public CheckoutResult processCheckout(int bookingId, double billAmount, int redeemPoints)
            throws SQLException, ClassNotFoundException {

        // Lấy thông tin booking và user trước khi bắt đầu transaction
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null || !booking.isPending()) {
            throw new SQLException("Booking không tồn tại hoặc đã được xử lý.");
        }

        User user = userDAO.findById(booking.getUserId());
        if (user == null) {
            throw new SQLException("Không tìm thấy khách hàng.");
        }

        Tier currentTier = tierDAO.getTierById(user.getCurrentTierId());
        if (currentTier == null) {
            throw new SQLException("Không tìm thấy hạng thẻ hiện tại.");
        }

        // === VALIDATE: Khách không thể đổi nhiều điểm hơn số dư ===
        if (redeemPoints > user.getPointsBalance()) {
            throw new SQLException("Số điểm đổi vượt quá số dư. Hiện có: " + user.getPointsBalance());
        }

        // === TÍNH TOÁN (phía server, verify lại kết quả từ JS frontend) ===
        // D = Số tiền giảm trừ = P_redeem × 100 VNĐ
        double discount = redeemPoints * POINTS_TO_VND_RATE;

        // T = Số tiền thực thu = max(0, A - D)
        double actualAmount = Math.max(0, billAmount - discount);

        // P_earn = Điểm tích lũy mới = round(T / 1000 × Multiplier)
        int earnedPoints = (int) Math.round(actualAmount / VND_PER_BASE_POINT * currentTier.getMultiplier());

        // === Tính toán giá trị mới cho User ===
        // PointsBalance_mới = PointsBalance_hiện_tại - P_redeem + P_earn
        int newPointsBalance = user.getPointsBalance() - redeemPoints + earnedPoints;

        // TotalSpent_mới = TotalSpent_hiện_tại + T (chỉ tính tiền thực chi)
        double newTotalSpent = user.getTotalSpent() + actualAmount;

        // TotalWashes_mới = TotalWashes_hiện_tại + 1
        int newTotalWashes = user.getTotalWashes() + 1;

        // === BẮT ĐẦU DATABASE TRANSACTION ===
        // Tạo Connection riêng, tắt auto-commit, dùng chung cho tất cả DAO
        Connection conn = DBContext.getConnection();
        conn.setAutoCommit(false); // Tắt auto-commit → bắt đầu transaction

        try {
            // =============================================
            // Bước a: Cập nhật Booking → COMPLETED
            // =============================================
            bookingDAO.updateStatus(conn, bookingId, "COMPLETED");

            // =============================================
            // Bước b+c: Cập nhật User (điểm + chi tiêu + lần rửa)
            // =============================================
            // Kiểm tra auto-upgrade tier TRƯỚC khi update
            Tier newTier = tierDAO.determineTier(newTotalWashes, newTotalSpent);
            String newTierId = (newTier != null) ? newTier.getTierId() : user.getCurrentTierId();
            boolean tierUpgraded = !newTierId.equals(user.getCurrentTierId());

            userDAO.updateTierAndPoints(conn, user.getUserId(),
                    newTierId, newPointsBalance, newTotalSpent, newTotalWashes);

            // =============================================
            // Bước d: Chèn LoyaltyTransactions
            // =============================================
            // Ghi lại giao dịch REDEEM (nếu có đổi điểm)
            if (redeemPoints > 0) {
                LoyaltyTransaction redeemTx = new LoyaltyTransaction(
                        user.getUserId(), "REDEEM", redeemPoints, billAmount);
                transactionDAO.insertTransaction(conn, redeemTx);
            }

            // Ghi lại giao dịch EARN (điểm tích lũy mới)
            if (earnedPoints > 0) {
                LoyaltyTransaction earnTx = new LoyaltyTransaction(
                        user.getUserId(), "EARN", earnedPoints, actualAmount);
                transactionDAO.insertTransaction(conn, earnTx);
            }

            // =============================================
            // Bước e+f: Tạo Notification tự động
            // =============================================
            // Thông báo thanh toán thành công
            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            Notification paymentNotif = new Notification(
                    user.getUserId(),
                    "Thanh toán thành công!",
                    String.format("Hóa đơn %s VNĐ. Bạn tích được %d điểm mới. Số dư ví: %d điểm.",
                            nf.format((long) actualAmount), earnedPoints, newPointsBalance)
            );
            notificationDAO.createNotification(conn, paymentNotif);

            // Thông báo thăng hạng (nếu đủ điều kiện)
            if (tierUpgraded && newTier != null) {
                Notification upgradeNotif = new Notification(
                        user.getUserId(),
                        "Chúc mừng thăng hạng!",
                        String.format("Bạn đã được nâng lên hạng %s! Từ giờ mỗi lần rửa xe bạn sẽ nhận được gấp %.1f lần điểm thưởng.",
                                newTier.getTierName(), newTier.getMultiplier())
                );
                notificationDAO.createNotification(conn, upgradeNotif);
            }

            // =============================================
            // COMMIT: Tất cả thành công → lưu vĩnh viễn
            // =============================================
            conn.commit();

            // Tạo kết quả trả về cho Servlet
            CheckoutResult result = new CheckoutResult();
            result.setSuccess(true);
            result.setBillAmount(billAmount);
            result.setDiscount(discount);
            result.setActualAmount(actualAmount);
            result.setRedeemedPoints(redeemPoints);
            result.setEarnedPoints(earnedPoints);
            result.setNewPointsBalance(newPointsBalance);
            result.setNewTierId(newTierId);
            result.setTierUpgraded(tierUpgraded);
            if (tierUpgraded && newTier != null) {
                result.setNewTierName(newTier.getTierName());
            }
            return result;

        } catch (SQLException e) {
            // =============================================
            // ROLLBACK: Có lỗi → hủy tất cả thay đổi
            // =============================================
            // Đảm bảo dữ liệu không bị "nửa vời" — tất cả hoặc không gì cả
            conn.rollback();
            throw e; // Ném lỗi lên Servlet để xử lý hiển thị
        } finally {
            // Bật lại auto-commit và đóng Connection
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    // ======================================================
    // 3. INNER CLASSES: Kết quả trả về cho Servlet/JSP
    // ======================================================

    /**
     * RewardProgress — Chứa kết quả tính toán tiến trình thăng hạng.
     * Servlet đặt object này vào request attribute, JSP đọc ra để hiển thị.
     */
    public static class RewardProgress {
        private double percentage;       // Phần trăm tiến trình (0-100)
        private String nextTierName;     // Tên hạng tiếp theo (VD: "Bạch Kim")
        private String nextTierId;       // Mã hạng tiếp theo
        private String message;          // Dòng thông báo động
        private int remainingWashes;     // Số lần rửa còn thiếu
        private double remainingSpent;   // Số tiền chi tiêu còn thiếu
        private boolean maxTier;         // true nếu đã ở hạng cao nhất

        // Getters & Setters
        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }

        public String getNextTierName() { return nextTierName; }
        public void setNextTierName(String nextTierName) { this.nextTierName = nextTierName; }

        public String getNextTierId() { return nextTierId; }
        public void setNextTierId(String nextTierId) { this.nextTierId = nextTierId; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public int getRemainingWashes() { return remainingWashes; }
        public void setRemainingWashes(int remainingWashes) { this.remainingWashes = remainingWashes; }

        public double getRemainingSpent() { return remainingSpent; }
        public void setRemainingSpent(double remainingSpent) { this.remainingSpent = remainingSpent; }

        public boolean isMaxTier() { return maxTier; }
        public void setMaxTier(boolean maxTier) { this.maxTier = maxTier; }
    }

    /**
     * CheckoutResult — Chứa kết quả thanh toán.
     * Servlet chuyển thành JSON (qua Gson) trả về cho JS Modal hiển thị.
     */
    public static class CheckoutResult {
        private boolean success;
        private double billAmount;       // Tổng tiền hóa đơn gốc (A)
        private double discount;         // Số tiền giảm trừ (D)
        private double actualAmount;     // Số tiền thực thu (T)
        private int redeemedPoints;      // Điểm đã đổi
        private int earnedPoints;        // Điểm tích lũy mới
        private int newPointsBalance;    // Số dư ví mới
        private String newTierId;        // Hạng thẻ mới (sau auto-upgrade)
        private String newTierName;      // Tên hạng mới (nếu thăng hạng)
        private boolean tierUpgraded;    // true nếu đã thăng hạng

        // Getters & Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public double getBillAmount() { return billAmount; }
        public void setBillAmount(double billAmount) { this.billAmount = billAmount; }

        public double getDiscount() { return discount; }
        public void setDiscount(double discount) { this.discount = discount; }

        public double getActualAmount() { return actualAmount; }
        public void setActualAmount(double actualAmount) { this.actualAmount = actualAmount; }

        public int getRedeemedPoints() { return redeemedPoints; }
        public void setRedeemedPoints(int redeemedPoints) { this.redeemedPoints = redeemedPoints; }

        public int getEarnedPoints() { return earnedPoints; }
        public void setEarnedPoints(int earnedPoints) { this.earnedPoints = earnedPoints; }

        public int getNewPointsBalance() { return newPointsBalance; }
        public void setNewPointsBalance(int newPointsBalance) { this.newPointsBalance = newPointsBalance; }

        public String getNewTierId() { return newTierId; }
        public void setNewTierId(String newTierId) { this.newTierId = newTierId; }

        public String getNewTierName() { return newTierName; }
        public void setNewTierName(String newTierName) { this.newTierName = newTierName; }

        public boolean isTierUpgraded() { return tierUpgraded; }
        public void setTierUpgraded(boolean tierUpgraded) { this.tierUpgraded = tierUpgraded; }
    }
}
