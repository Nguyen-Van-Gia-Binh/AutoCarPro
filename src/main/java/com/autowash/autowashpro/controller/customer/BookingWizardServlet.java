package com.autowash.autowashpro.controller.customer;

import com.autowash.autowashpro.dao.LicensePlateDAO;
import com.autowash.autowashpro.dao.TierDAO;
import com.autowash.autowashpro.model.LicensePlate;
import com.autowash.autowashpro.model.Tier;
import com.autowash.autowashpro.model.User;
import com.autowash.autowashpro.service.BookingService;
import com.google.gson.Gson;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * BookingWizardServlet — Quy trình đặt lịch 4 bước (Wizard Pattern).
 *
 * === LUỒNG DỮ LIỆU (4 BƯỚC) ===
 * GET /customer/booking → load dữ liệu ban đầu (danh sách xe) → booking.jsp
 * GET /customer/booking?action=slots&date=2025-06-01 → AJAX check slot → JSON
 * POST /customer/booking → nhận plateId + date + slot + notes → tạo booking
 *
 * Bước 1 (Chọn Xe): Hiển thị danh sách xe dưới dạng Cards
 * Bước 2 (Chọn Ngày): Calendar giới hạn theo hạng thẻ (BookingDays)
 * Bước 3 (Chọn Giờ): Nút bấm slot + AJAX check tình trạng
 * Bước 4 (Xác nhận): Bảng tổng hợp → POST tạo booking
 */
@WebServlet("/customer/booking")
public class BookingWizardServlet extends HttpServlet {

    private LicensePlateDAO plateDAO = new LicensePlateDAO();
    private TierDAO tierDAO = new TierDAO();
    private BookingService bookingService = new BookingService();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");
        String action = request.getParameter("action");

        try {
            // === AJAX: Kiểm tra slot khả dụng cho bước 3 ===
            if ("slots".equals(action)) {
                handleSlotCheck(request, response);
                return;
            }

            // === Trang booking wizard chính ===
            // Load danh sách xe của user (bước 1)
            List<LicensePlate> plates = plateDAO.getPlatesByUserId(user.getUserId());

            // Load thông tin tier để tính BookingDays (bước 2)
            Tier currentTier = tierDAO.getTierById(user.getCurrentTierId());
            int bookingDays = (currentTier != null) ? currentTier.getBookingDays() : 7;

            // Tính danh sách ngày khả dụng (bước 2)
            List<Date> availableDates = bookingService.getAvailableDates(bookingDays);

            // Lấy danh sách time slots
            String[] timeSlots = bookingService.getTimeSlots();

            request.setAttribute("plates", plates);
            request.setAttribute("currentTier", currentTier);
            request.setAttribute("bookingDays", bookingDays);
            request.setAttribute("availableDates", availableDates);
            request.setAttribute("timeSlots", timeSlots);

            request.getRequestDispatcher("/customer/booking.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi tải dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/customer/booking.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        User user = (User) request.getSession().getAttribute("user");

        try {
            int plateId = Integer.parseInt(request.getParameter("plateId"));
            Date bookingDate = Date.valueOf(request.getParameter("bookingDate"));
            String timeSlot = request.getParameter("timeSlot");
            String notes = request.getParameter("notes");

            // Validate và tạo booking
            boolean created = bookingService.validateAndCreateBooking(
                    user.getUserId(), plateId, bookingDate, timeSlot, notes);

            if (created) {
                // Redirect về dashboard với thông báo thành công
                response.sendRedirect(request.getContextPath()
                        + "/customer/dashboard?bookingSuccess=true");
            } else {
                request.setAttribute("error", "Không thể tạo đặt lịch. Vui lòng thử lại.");
                doGet(request, response);
            }

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            doGet(request, response);
        }
    }

    /**
     * Xử lý AJAX request kiểm tra slot khả dụng.
     * JS gửi: GET /customer/booking?action=slots&date=2025-06-01
     * Trả về: JSON { "08:00-09:00": {available: true, remaining: 3}, ... }
     */
    private void handleSlotCheck(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String dateStr = request.getParameter("date");
            Date bookingDate = Date.valueOf(dateStr);

            Map<String, BookingService.SlotInfo> slots = bookingService.getSlotAvailability(bookingDate);
            response.getWriter().write(gson.toJson(slots));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
