package com.autowash.autowashpro.filter;

import com.autowash.autowashpro.model.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * AuthorizationFilter — Bộ lọc phân quyền trung tâm cho toàn bộ ứng dụng.
 *
 * === LUỒNG DỮ LIỆU (DATA FLOW) ===
 * 1. Mọi HTTP request đều đi qua Filter này TRƯỚC KHI tới Servlet.
 * 2. Filter kiểm tra:
 *    a. URL có phải trang công khai không? (login, register, tài nguyên tĩnh)
 *       → NẾU PHẢI → cho đi tiếp (chain.doFilter).
 *    b. User đã đăng nhập chưa? (kiểm tra session attribute "user")
 *       → CHƯA → redirect về /login.
 *    c. User có quyền truy cập URL này không? (kiểm tra role vs URL pattern)
 *       → KHÔNG → redirect về trang phù hợp với role.
 *       → CÓ → cho đi tiếp tới Servlet.
 *
 * === TẠI SAO DÙNG FILTER THAY VÌ CHECK TRONG TỪNG SERVLET? ===
 * → DRY: Không phải copy paste code kiểm tra session vào mỗi Servlet.
 * → Tập trung: Tất cả logic phân quyền ở một chỗ, dễ audit bảo mật.
 * → Tự động: Khi thêm Servlet mới, tự động được bảo vệ (nếu nằm trong URL pattern).
 *
 * === ANNOTATION @WebFilter("/*") ===
 * → Áp dụng cho TẤT CẢ URL trong ứng dụng (/*).
 * → Mỗi request đều được filter kiểm tra trước.
 */
@WebFilter("/*")
public class AuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Không cần khởi tạo gì đặc biệt
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Lấy đường dẫn tương đối (VD: "/customer/dashboard", "/login")
        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = uri.substring(contextPath.length());

        // =============================================
        // BƯỚC 1: Cho phép truy cập tự do các trang/tài nguyên công khai
        // =============================================
        if (isPublicResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        // =============================================
        // BƯỚC 2: Kiểm tra đã đăng nhập chưa
        // =============================================
        HttpSession session = httpRequest.getSession(false); // false = không tạo session mới
        User user = null;

        if (session != null) {
            user = (User) session.getAttribute("user");
        }

        // Nếu chưa đăng nhập → redirect về trang login
        if (user == null) {
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        // =============================================
        // BƯỚC 3: Kiểm tra quyền truy cập theo role
        // =============================================

        // URL bắt đầu bằng /customer/ → chỉ CUSTOMER được truy cập
        if (path.startsWith("/customer/")) {
            if (!user.isCustomer()) {
                // Admin/Staff cố truy cập trang customer → redirect về admin dashboard
                httpResponse.sendRedirect(contextPath + "/admin/dashboard");
                return;
            }
        }

        // URL bắt đầu bằng /admin/ → chỉ ADMIN hoặc STAFF được truy cập
        if (path.startsWith("/admin/")) {
            if (!user.isAdmin() && !user.isStaff()) {
                // Customer cố truy cập trang admin → redirect về customer dashboard
                httpResponse.sendRedirect(contextPath + "/customer/dashboard");
                return;
            }
        }

        // =============================================
        // BƯỚC 4: Cho phép request đi tiếp tới Servlet
        // =============================================
        chain.doFilter(request, response);
    }

    /**
     * Kiểm tra đường dẫn có phải tài nguyên công khai không.
     *
     * Danh sách tài nguyên công khai:
     * - /login, /register, /logout: Trang xác thực
     * - /css/, /js/, /images/: Tài nguyên tĩnh (CSS, JavaScript, hình ảnh)
     * - Trang gốc "/" hoặc "/index": Trang chủ/landing page
     *
     * @param path Đường dẫn tương đối (không bao gồm context path)
     * @return true nếu là tài nguyên công khai
     */
    private boolean isPublicResource(String path) {
        return path.equals("/")
                || path.equals("/login")
                || path.equals("/register")
                || path.equals("/logout")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/fonts/")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".ico");
    }

    @Override
    public void destroy() {
        // Không cần dọn dẹp gì đặc biệt
    }
}
