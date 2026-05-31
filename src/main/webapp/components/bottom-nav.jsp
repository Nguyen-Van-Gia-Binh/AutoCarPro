<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- 
    bottom-nav.jsp — Thanh điều hướng dưới cùng cho Mobile.
    
    === CÁCH SỬ DỤNG ===
    Include vào cuối body mỗi trang customer:
    <jsp:include page="/components/bottom-nav.jsp">
        <jsp:param name="activeTab" value="dashboard" />
    </jsp:include>
    
    Giá trị activeTab: "dashboard", "booking", "cars", "alerts"
--%>
<c:set var="activeTab" value="${param.activeTab}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!-- BottomNavBar — Fixed dưới cùng, ẩn trên desktop (md:hidden) -->
<nav class="md:hidden fixed bottom-0 left-0 w-full z-50 flex justify-around items-center px-4 py-3 bg-surface-container-lowest border-t border-outline-variant shadow-md rounded-t-xl">

    <%-- Dashboard --%>
    <a href="${ctx}/customer/dashboard"
       class="flex flex-col items-center justify-center ${activeTab == 'dashboard' ? 'bg-secondary-container text-on-secondary-container rounded-full px-4 py-1' : 'text-on-surface-variant hover:text-secondary'} active:scale-90 transition-all duration-200">
        <span class="material-symbols-outlined" ${activeTab == 'dashboard' ? 'style="font-variation-settings: \'FILL\' 1;"' : ''}>home</span>
        <span class="font-mono text-[12px] mt-1">Dashboard</span>
    </a>

    <%-- Booking --%>
    <a href="${ctx}/customer/booking"
       class="flex flex-col items-center justify-center ${activeTab == 'booking' ? 'bg-secondary-container text-on-secondary-container rounded-full px-4 py-1' : 'text-on-surface-variant hover:text-secondary'} active:scale-90 transition-all duration-200">
        <span class="material-symbols-outlined" ${activeTab == 'booking' ? 'style="font-variation-settings: \'FILL\' 1;"' : ''}>calendar_today</span>
        <span class="font-mono text-[12px] mt-1">Đặt lịch</span>
    </a>

    <%-- My Cars --%>
    <a href="${ctx}/customer/my-cars"
       class="flex flex-col items-center justify-center ${activeTab == 'cars' ? 'bg-secondary-container text-on-secondary-container rounded-full px-4 py-1' : 'text-on-surface-variant hover:text-secondary'} active:scale-90 transition-all duration-200">
        <span class="material-symbols-outlined" ${activeTab == 'cars' ? 'style="font-variation-settings: \'FILL\' 1;"' : ''}>directions_car</span>
        <span class="font-mono text-[12px] mt-1">Xe của tôi</span>
    </a>

    <%-- Notifications --%>
    <a href="${ctx}/customer/notifications"
       class="flex flex-col items-center justify-center ${activeTab == 'alerts' ? 'bg-secondary-container text-on-secondary-container rounded-full px-4 py-1' : 'text-on-surface-variant hover:text-secondary'} active:scale-90 transition-all duration-200">
        <span class="material-symbols-outlined" ${activeTab == 'alerts' ? 'style="font-variation-settings: \'FILL\' 1;"' : ''}>notifications</span>
        <span class="font-mono text-[12px] mt-1">Thông báo</span>
    </a>
</nav>
