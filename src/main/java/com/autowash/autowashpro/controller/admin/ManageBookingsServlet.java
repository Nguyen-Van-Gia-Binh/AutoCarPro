package com.autowash.autowashpro.controller.admin;

import com.autowash.autowashpro.dao.BookingDAO;
import com.autowash.autowashpro.model.Booking;
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
 * ManageBookingsServlet — Quản lý danh sách booking theo ngày (Admin/Staff).
 *
 * === LUỒNG DỮ LIỆU ===
 * GET /admin/bookings → load booking ngày hôm nay → bookings.jsp
 * GET /admin/bookings?date=2025-06-01 → load booking theo ngày chọn
 * POST /admin/bookings?action=cancel&bookingId=5 → hủy booking
 */
@WebServlet("/admin/bookings")
public class ManageBookingsServlet extends HttpServlet {

    private BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy ngày từ param, mặc định là hôm nay
            String dateStr = request.getParameter("date");
            Date filterDate;

            if (dateStr != null && !dateStr.isEmpty()) {
                filterDate = Date.valueOf(dateStr);
            } else {
                filterDate = new Date(System.currentTimeMillis());
            }

            // Load booking theo ngày
            List<Booking> bookings = bookingDAO.getBookingsByDate(filterDate);

            request.setAttribute("bookings", bookings);
            request.setAttribute("filterDate", filterDate);

            request.getRequestDispatcher("/admin/bookings.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi tải danh sách: " + e.getMessage());
            request.getRequestDispatcher("/admin/bookings.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("cancel".equals(action)) {
                int bookingId = Integer.parseInt(request.getParameter("bookingId"));
                // Dùng connection riêng (không trong transaction)
                try (java.sql.Connection conn = com.autowash.autowashpro.connection.DBContext.getConnection()) {
                    bookingDAO.updateStatus(conn, bookingId, "CANCELLED");
                } catch (ClassNotFoundException e) {
                    throw new SQLException("Driver not found", e);
                }
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi: " + e.getMessage());
        }

        // Reload trang
        doGet(request, response);
    }
}
