package com.autowash.autowashpro.controller.admin;

import com.autowash.autowashpro.dao.BookingDAO;
import com.autowash.autowashpro.dao.TierDAO;
import com.autowash.autowashpro.dao.UserDAO;
import com.autowash.autowashpro.model.Booking;
import com.autowash.autowashpro.model.Tier;
import com.autowash.autowashpro.model.User;
import com.autowash.autowashpro.service.LoyaltyService;
import com.google.gson.Gson;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CheckoutServlet — Xử lý tiếp đón & thanh toán nhanh (Admin/Staff Desktop).
 *
 * === LUỒNG DỮ LIỆU ===
 * GET /admin/checkout → hiển thị danh sách booking PENDING hôm nay → checkout.jsp
 * GET /admin/checkout?action=customer&bookingId=5 → AJAX lấy thông tin khách cho modal
 * POST /admin/checkout → nhận bookingId + billAmount + redeemPoints →
 *   LoyaltyService.processCheckout() → Database Transaction 6 bước →
 *   trả về JSON kết quả → JS hiển thị trên Modal.
 */
@WebServlet("/admin/checkout")
public class CheckoutServlet extends HttpServlet {

    private BookingDAO bookingDAO = new BookingDAO();
    private UserDAO userDAO = new UserDAO();
    private TierDAO tierDAO = new TierDAO();
    private LoyaltyService loyaltyService = new LoyaltyService();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            // === AJAX: Lấy thông tin khách hàng cho modal checkout ===
            if ("customer".equals(action)) {
                handleCustomerInfo(request, response);
                return;
            }

            // === Trang checkout chính ===
            Date today = new Date(System.currentTimeMillis());
            List<Booking> pendingBookings = bookingDAO.getBookingsByDate(today);

            request.setAttribute("bookings", pendingBookings);
            request.setAttribute("today", today);

            request.getRequestDispatcher("/admin/checkout.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi tải dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/admin/checkout.jsp").forward(request, response);
        }
    }

    /**
     * POST: Thực hiện checkout — gọi LoyaltyService.processCheckout().
     * Trả về JSON kết quả cho JavaScript Modal hiển thị.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            double billAmount = Double.parseDouble(request.getParameter("billAmount"));
            int redeemPoints = Integer.parseInt(request.getParameter("redeemPoints"));

            // Gọi Loyalty Engine — Database Transaction 6 bước
            LoyaltyService.CheckoutResult result = loyaltyService.processCheckout(
                    bookingId, billAmount, redeemPoints);

            response.getWriter().write(gson.toJson(result));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * AJAX: Trả về thông tin khách hàng của booking để hiển thị trên Modal.
     */
    private void handleCustomerInfo(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        int bookingId = Integer.parseInt(request.getParameter("bookingId"));

        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Booking not found\"}");
            return;
        }

        User customer = userDAO.findById(booking.getUserId());
        Tier customerTier = (customer != null) ? tierDAO.getTierById(customer.getCurrentTierId()) : null;

        // Tạo response object chứa thông tin cần thiết cho Modal
        java.util.Map<String, Object> info = new java.util.LinkedHashMap<>();
        info.put("bookingId", booking.getBookingId());
        info.put("customerName", booking.getCustomerName());
        info.put("plateNumber", booking.getPlateNumber());
        info.put("vehicleModel", booking.getVehicleModel());
        info.put("timeSlot", booking.getTimeSlot());

        if (customer != null) {
            info.put("pointsBalance", customer.getPointsBalance());
            info.put("currentTierId", customer.getCurrentTierId());
        }
        if (customerTier != null) {
            info.put("tierName", customerTier.getTierName());
            info.put("multiplier", customerTier.getMultiplier());
        }

        response.getWriter().write(gson.toJson(info));
    }
}
