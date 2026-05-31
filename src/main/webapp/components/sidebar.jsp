<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- 
    sidebar.jsp — Sidebar navigation cho Admin Desktop.
    
    === CÁCH SỬ DỤNG ===
    Include trong mỗi trang admin:
    <jsp:include page="/components/sidebar.jsp">
        <jsp:param name="activeMenu" value="dashboard" />
    </jsp:include>
    
    Giá trị activeMenu: "dashboard", "bookings", "checkout", "tiers"
--%>
<c:set var="activeMenu" value="${param.activeMenu}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="user" value="${sessionScope.user}" />

<!-- Sidebar — Fixed bên trái, ẩn trên mobile, hiện từ md trở lên -->
<nav class="hidden md:flex flex-col bg-primary-container text-white font-sans fixed left-0 h-full w-[250px] shadow-md py-6 z-20">
    <!-- Logo + Brand -->
    <div class="px-6 mb-8">
        <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded bg-secondary flex items-center justify-center text-white">
                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">local_car_wash</span>
            </div>
            <div>
                <h1 class="text-xl font-bold tracking-tight text-white">AutoWash Pro</h1>
                <p class="font-mono text-[12px] text-slate-400 uppercase">Admin Portal</p>
            </div>
        </div>
    </div>

    <!-- Menu Items -->
    <ul class="flex flex-col flex-grow w-full gap-1 px-4">
        <%-- Dashboard --%>
        <li>
            <a href="${ctx}/admin/dashboard"
               class="flex items-center gap-4 px-4 py-3 rounded-lg transition-all duration-200 ${activeMenu == 'dashboard' ? 'border-l-4 border-cyan-400 bg-white/10 text-white font-bold' : 'border-l-4 border-transparent text-slate-400 opacity-70 hover:opacity-100 hover:bg-white/5 ml-1'}">
                <span class="material-symbols-outlined">dashboard</span>
                <span>Dashboard</span>
            </a>
        </li>

        <%-- Today's Bookings --%>
        <li>
            <a href="${ctx}/admin/bookings"
               class="flex items-center gap-4 px-4 py-3 rounded-lg transition-all duration-200 ${activeMenu == 'bookings' ? 'border-l-4 border-cyan-400 bg-white/10 text-white font-bold' : 'border-l-4 border-transparent text-slate-400 opacity-70 hover:opacity-100 hover:bg-white/5 ml-1'}">
                <span class="material-symbols-outlined">calendar_today</span>
                <span>Lịch hẹn hôm nay</span>
            </a>
        </li>

        <%-- Quick Checkout --%>
        <li>
            <a href="${ctx}/admin/checkout"
               class="flex items-center gap-4 px-4 py-3 rounded-lg transition-all duration-200 ${activeMenu == 'checkout' ? 'border-l-4 border-cyan-400 bg-white/10 text-white font-bold' : 'border-l-4 border-transparent text-slate-400 opacity-70 hover:opacity-100 hover:bg-white/5 ml-1'}">
                <span class="material-symbols-outlined">point_of_sale</span>
                <span>Thanh toán nhanh</span>
            </a>
        </li>
    </ul>

    <!-- User Info + Logout -->
    <div class="px-6 mt-auto">
        <div class="flex items-center gap-3 px-2 py-3 border-t border-white/10">
            <div class="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center">
                <span class="material-symbols-outlined text-white text-sm">person</span>
            </div>
            <div class="flex flex-col flex-1">
                <span class="font-mono text-sm text-slate-300">${user.fullName}</span>
                <span class="font-mono text-[11px] text-slate-500">${user.role}</span>
            </div>
            <a href="${ctx}/logout" class="text-slate-500 hover:text-red-400 transition-colors" title="Đăng xuất">
                <span class="material-symbols-outlined text-lg">logout</span>
            </a>
        </div>
    </div>
</nav>
