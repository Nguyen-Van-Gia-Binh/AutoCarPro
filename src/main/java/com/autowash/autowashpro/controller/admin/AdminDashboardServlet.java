package com.autowash.autowashpro.controller.admin;

import com.autowash.autowashpro.dao.BookingDAO;
import com.autowash.autowashpro.dao.TierDAO;
import com.autowash.autowashpro.dao.UserDAO;
import com.autowash.autowashpro.model.Tier;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AdminDashboardServlet — Trang Dashboard cho Admin/Staff (Desktop).
 *
 * === LUỒNG DỮ LIỆU ===
 * GET /admin/dashboard → load thống kê:
 *   - Tổng doanh thu, tổng lần rửa, booking đang pending
 *   - Phân bố hạng thẻ (cho Chart.js Doughnut)
 *   - Dữ liệu sẽ được truyền xuống JSP, JSP render Chart.js
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private BookingDAO bookingDAO = new BookingDAO();
    private TierDAO tierDAO = new TierDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // === Thống kê tổng quan ===
            int pendingBookings = bookingDAO.countPendingBookings();

            // === Phân bố hạng thẻ (cho biểu đồ Doughnut) ===
            List<Tier> allTiers = tierDAO.getAllTiers();
            int memberCount = userDAO.countByTier("MEMBER");
            int silverCount = userDAO.countByTier("SILVER");
            int goldCount = userDAO.countByTier("GOLD");
            int platinumCount = userDAO.countByTier("PLATINUM");
            int totalCustomers = memberCount + silverCount + goldCount + platinumCount;

            // === Đặt vào request attributes ===
            request.setAttribute("pendingBookings", pendingBookings);
            request.setAttribute("memberCount", memberCount);
            request.setAttribute("silverCount", silverCount);
            request.setAttribute("goldCount", goldCount);
            request.setAttribute("platinumCount", platinumCount);
            request.setAttribute("totalCustomers", totalCustomers);
            request.setAttribute("allTiers", allTiers);

            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi tải Dashboard: " + e.getMessage());
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        }
    }
}
