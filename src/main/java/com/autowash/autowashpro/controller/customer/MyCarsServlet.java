package com.autowash.autowashpro.controller.customer;

import com.autowash.autowashpro.dao.LicensePlateDAO;
import com.autowash.autowashpro.model.LicensePlate;
import com.autowash.autowashpro.model.User;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * MyCarsServlet — Quản lý biển số xe của khách hàng.
 *
 * === LUỒNG DỮ LIỆU ===
 * GET /customer/my-cars → load danh sách xe → my-cars.jsp (card layout)
 * POST /customer/my-cars?action=add → validate regex biển số VN → thêm xe mới
 * POST /customer/my-cars?action=delete → xóa xe (kiểm tra quyền sở hữu)
 */
@WebServlet("/customer/my-cars")
public class MyCarsServlet extends HttpServlet {

    private LicensePlateDAO plateDAO = new LicensePlateDAO();

    // Regex biển số xe Việt Nam (theo tài liệu)
    // Ví dụ hợp lệ: 30A1-1234, 51F1-999.99, 43C2-88888
    private static final String VN_PLATE_REGEX =
            "^\\d{2}[A-Z]\\d{1}-\\d{4,5}$|^\\d{2}[A-Z]\\d{1}-\\d{3}\\.\\d{2}$";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");

        try {
            List<LicensePlate> plates = plateDAO.getPlatesByUserId(user.getUserId());
            request.setAttribute("plates", plates);
            request.getRequestDispatcher("/customer/my-cars.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi tải danh sách xe: " + e.getMessage());
            request.getRequestDispatcher("/customer/my-cars.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        User user = (User) request.getSession().getAttribute("user");
        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                handleAddPlate(request, response, user);
            } else if ("delete".equals(action)) {
                handleDeletePlate(request, response, user);
            } else {
                response.sendRedirect(request.getContextPath() + "/customer/my-cars");
            }

        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            doGet(request, response);
        }
    }

    /**
     * Thêm xe mới với validation regex biển số Việt Nam.
     */
    private void handleAddPlate(HttpServletRequest request, HttpServletResponse response,
                                 User user) throws SQLException, ServletException, IOException {

        String plateNumber = request.getParameter("plateNumber");
        String vehicleModel = request.getParameter("vehicleModel");

        // Chuẩn hóa biển số: chuyển chữ thường → chữ hoa, bỏ khoảng trắng
        if (plateNumber != null) {
            plateNumber = plateNumber.trim().toUpperCase();
        }

        // Validate regex biển số VN
        if (plateNumber == null || !plateNumber.matches(VN_PLATE_REGEX)) {
            request.setAttribute("error",
                    "Biển số xe không hợp lệ. Định dạng đúng: 30A1-1234 hoặc 51F1-999.99");
            doGet(request, response);
            return;
        }

        // Kiểm tra biển số đã tồn tại
        if (plateDAO.isPlateNumberExists(plateNumber)) {
            request.setAttribute("error", "Biển số xe " + plateNumber + " đã được đăng ký.");
            doGet(request, response);
            return;
        }

        // Tạo và lưu xe mới
        LicensePlate plate = new LicensePlate(user.getUserId(), plateNumber, vehicleModel);
        plateDAO.addPlate(plate);

        response.sendRedirect(request.getContextPath() + "/customer/my-cars?added=true");
    }

    /**
     * Xóa xe — kiểm tra quyền sở hữu thông qua userId.
     */
    private void handleDeletePlate(HttpServletRequest request, HttpServletResponse response,
                                    User user) throws SQLException, IOException {

        int plateId = Integer.parseInt(request.getParameter("plateId"));

        // deletePlate kiểm tra cả userId → đảm bảo user chỉ xóa xe của mình
        plateDAO.deletePlate(plateId, user.getUserId());

        response.sendRedirect(request.getContextPath() + "/customer/my-cars?deleted=true");
    }
}
