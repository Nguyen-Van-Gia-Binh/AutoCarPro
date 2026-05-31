package com.autowash.autowashpro.controller.customer;

import com.autowash.autowashpro.dao.*;
import com.autowash.autowashpro.model.*;
import com.autowash.autowashpro.service.LoyaltyService;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * DashboardServlet — Trang chủ Dashboard cho khách hàng (Mobile-First).
 *
 * === LUỒNG DỮ LIỆU ===
 * 1. User đăng nhập → redirect tới /customer/dashboard.
 * 2. Servlet lấy User từ session.
 * 3. Gọi LoyaltyService.calculateNextRewardProgress(user) → tính % tiến trình.
 * 4. Gọi BookingDAO.getUpcomingByUserId() → lấy booking gần nhất.
 * 5. Gọi NotificationDAO.getUnreadCount() → đếm thông báo chưa đọc.
 * 6. Đặt tất cả vào request attributes → forward tới dashboard.jsp.
 * 7. JSP render giao diện bento grid: Reward Wallet + Progress + Upcoming Booking.
 */
@WebServlet("/customer/dashboard")
public class DashboardServlet extends HttpServlet {

    private LoyaltyService loyaltyService = new LoyaltyService();
    private BookingDAO bookingDAO = new BookingDAO();
    private TierDAO tierDAO = new TierDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");

        try {
            // === Refresh thông tin user từ DB (có thể đã thay đổi sau checkout) ===
            UserDAO userDAO = new UserDAO();
            User freshUser = userDAO.findById(user.getUserId());
            if (freshUser != null) {
                user = freshUser;
                request.getSession().setAttribute("user", user); // Cập nhật session
            }

            // === Lấy thông tin hạng thẻ hiện tại ===
            Tier currentTier = tierDAO.getTierById(user.getCurrentTierId());

            // === Tính tiến trình phần thưởng kế tiếp ===
            LoyaltyService.RewardProgress progress = loyaltyService.calculateNextRewardProgress(user);

            // === Lấy booking sắp tới ===
            Booking upcomingBooking = bookingDAO.getUpcomingByUserId(user.getUserId());

            // === Đếm thông báo chưa đọc ===
            int unreadCount = notificationDAO.getUnreadCount(user.getUserId());

            // === Format số tiền cho hiển thị ===
            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            String formattedPointsValue = nf.format((long) user.getPointsValueInVnd());

            // === Đặt vào request attributes ===
            request.setAttribute("currentTier", currentTier);
            request.setAttribute("progress", progress);
            request.setAttribute("upcomingBooking", upcomingBooking);
            request.setAttribute("unreadCount", unreadCount);
            request.setAttribute("formattedPointsValue", formattedPointsValue);

            // Forward tới JSP
            request.getRequestDispatcher("/customer/dashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi tải dữ liệu Dashboard: " + e.getMessage());
            request.getRequestDispatcher("/customer/dashboard.jsp").forward(request, response);
        }
    }
}
