<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- 
    customer-sidebar.jsp — Sidebar navigation cho Customer Desktop.
    
    === CÁCH SỬ SỤNG ===
    Include trong mỗi trang customer:
    <jsp:include page="/components/customer-sidebar.jsp">
        <jsp:param name="activeMenu" value="dashboard" />
    </jsp:include>
    
    Giá trị activeMenu: "dashboard", "booking", "cars", "notifications"
--%>
<c:set var="activeMenu" value="${param.activeMenu}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="user" value="${sessionScope.user}" />

<!-- Customer Sidebar — Ẩn trên di động (md:hidden), hiện từ md trở lên -->
<aside class="hidden md:flex flex-col bg-primary-container text-white font-sans fixed left-0 top-0 h-full w-[280px] border-r border-outline-variant/20 shadow-md py-8 z-50">
    <div class="px-6 mb-8">
        <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded bg-secondary flex items-center justify-center text-white">
                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">local_car_wash</span>
            </div>
            <div>
                <h1 class="text-xl font-bold tracking-tight text-white">AutoWash Pro</h1>
                <p class="font-mono text-[12px] text-slate-400 uppercase">Customer Portal</p>
            </div>
        </div>
    </div>

    <!-- Navigation Items -->
    <nav class="flex-grow px-4 flex flex-col justify-between">
        <div class="space-y-2 flex flex-col">
            <%-- Dashboard --%>
            <a href="${ctx}/customer/dashboard"
               class="flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-200 ${activeMenu == 'dashboard' ? 'border-l-4 border-cyan-400 bg-white/10 text-cyan-400 font-bold' : 'border-l-4 border-transparent text-slate-400 opacity-70 hover:opacity-100 hover:bg-white/5'}">
                <span class="material-symbols-outlined ${activeMenu == 'dashboard' ? 'fill' : ''}">dashboard</span>
                <span class="font-mono text-sm">Dashboard</span>
            </a>

            <%-- Booking --%>
            <a href="${ctx}/customer/booking"
               class="flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-200 ${activeMenu == 'booking' ? 'border-l-4 border-cyan-400 bg-white/10 text-cyan-400 font-bold' : 'border-l-4 border-transparent text-slate-400 opacity-70 hover:opacity-100 hover:bg-white/5'}">
                <span class="material-symbols-outlined ${activeMenu == 'booking' ? 'fill' : ''}">calendar_month</span>
                <span class="font-mono text-sm">Đặt lịch</span>
            </a>

            <%-- My Cars --%>
            <a href="${ctx}/customer/my-cars"
               class="flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-200 ${activeMenu == 'cars' ? 'border-l-4 border-cyan-400 bg-white/10 text-cyan-400 font-bold' : 'border-l-4 border-transparent text-slate-400 opacity-70 hover:opacity-100 hover:bg-white/5'}">
                <span class="material-symbols-outlined ${activeMenu == 'cars' ? 'fill' : ''}">directions_car</span>
                <span class="font-mono text-sm">Xe của tôi</span>
            </a>

            <%-- Notifications --%>
            <a href="${ctx}/customer/notifications"
               class="flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-200 ${activeMenu == 'notifications' ? 'border-l-4 border-cyan-400 bg-white/10 text-cyan-400 font-bold' : 'border-l-4 border-transparent text-slate-400 opacity-70 hover:opacity-100 hover:bg-white/5'}">
                <span class="material-symbols-outlined ${activeMenu == 'notifications' ? 'fill' : ''}">notifications</span>
                <span class="font-mono text-sm">Thông báo</span>
            </a>
        </div>

        <%-- Bottom Info --%>
        <div class="pt-4 border-t border-white/10">
            <div class="flex items-center gap-3 px-2 py-3">
                <div class="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center">
                    <span class="material-symbols-outlined text-white text-sm">person</span>
                </div>
                <div class="flex flex-col flex-1 min-w-0">
                    <span class="font-mono text-sm text-slate-300 truncate">${user.fullName}</span>
                    <span class="font-mono text-[11px] text-slate-500">${user.phoneNumber}</span>
                </div>
                <a href="${ctx}/logout" class="text-slate-500 hover:text-red-400 transition-colors" title="Đăng xuất">
                    <span class="material-symbols-outlined text-lg">logout</span>
                </a>
            </div>
        </div>
    </nav>
</aside>
