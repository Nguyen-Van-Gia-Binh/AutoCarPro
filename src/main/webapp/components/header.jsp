<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- 
    header.jsp — Thanh điều hướng trên cùng cho trang Customer (Mobile).
    
    === CÁCH SỬ DỤNG ===
    Include vào đầu mỗi trang customer:
    <jsp:include page="/components/header.jsp" />
    
    === YÊU CẦU ===
    Session phải có attribute "user" (đảm bảo bởi AuthorizationFilter).
    Request attribute "unreadCount" (từ Servlet, hoặc mặc định 0).
    Request attribute "activeTab" (VD: "dashboard", "booking", "cars", "alerts").
--%>
<c:set var="user" value="${sessionScope.user}" />
<c:set var="unreadCount" value="${requestScope.unreadCount != null ? requestScope.unreadCount : 0}" />
<c:set var="pageTitle" value="${param.pageTitle != null ? param.pageTitle : 'Dashboard'}" />

<!-- TopAppBar — Fixed ở trên cùng, responsive width và position trên desktop (md) -->
<header class="fixed top-0 left-0 w-full md:left-[280px] md:w-[calc(100%-280px)] h-16 bg-surface/80 backdrop-blur-md border-b border-outline-variant/30 shadow-sm flex items-center justify-between px-4 md:px-8 z-40 transition-all duration-200">
    <!-- Trái: Logo trên mobile, breadcrumbs trên desktop -->
    <div class="flex items-center gap-4">
        <!-- Hiện logo di động (ẩn trên desktop vì đã có ở sidebar) -->
        <div class="flex md:hidden items-center gap-2">
            <div class="h-8 w-8 rounded-full overflow-hidden bg-surface-container-highest border border-outline-variant flex items-center justify-center">
                <span class="material-symbols-outlined text-on-surface-variant text-lg">person</span>
            </div>
            <h1 class="font-sans text-xl font-bold text-primary">AutoWash Pro</h1>
        </div>

        <!-- Hiện breadcrumbs trên desktop -->
        <div class="hidden md:flex items-center gap-2 text-on-surface-variant font-mono text-sm">
            <span>Portal</span>
            <span class="material-symbols-outlined text-sm">chevron_right</span>
            <span class="text-secondary font-bold">${pageTitle}</span>
        </div>
    </div>

    <!-- Phải: Nút chuông thông báo & Profile avatar -->
    <div class="flex items-center gap-4">
        <!-- Nút chuông thông báo — Badge đỏ hiển thị số chưa đọc -->
        <a href="${pageContext.request.contextPath}/customer/notifications"
           class="h-10 w-10 flex items-center justify-center rounded-full text-secondary hover:bg-surface-variant transition-colors active:scale-95 duration-150 relative"
           id="notificationBell">
            <span class="material-symbols-outlined">notifications</span>
            <%-- Badge đỏ: chỉ hiển thị khi có thông báo chưa đọc --%>
            <c:if test="${unreadCount > 0}">
                <span class="absolute top-1 right-1 w-5 h-5 bg-error text-white text-[10px] font-bold rounded-full flex items-center justify-center" id="notifBadge">
                    ${unreadCount > 9 ? '9+' : unreadCount}
                </span>
            </c:if>
        </a>

        <!-- Hỗ trợ / User profile ảnh đại diện (chỉ hiện trên desktop) -->
        <div class="hidden md:flex items-center gap-4 border-l border-outline-variant/30 pl-4">
            <span class="font-mono text-xs text-on-surface-variant">${user.fullName}</span>
            <div class="h-8 w-8 rounded-full bg-slate-200 overflow-hidden border border-outline-variant">
                <div class="h-full w-full flex items-center justify-center bg-secondary/10 text-secondary font-bold text-xs uppercase">
                    ${user.fullName.substring(0,1)}
                </div>
            </div>
        </div>
    </div>
</header>
