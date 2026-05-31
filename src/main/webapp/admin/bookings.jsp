<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lịch hẹn - AutoWash Pro Admin</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;600;700&family=JetBrains+Mono:wght@500&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <script>
        tailwind.config = {
            theme: { extend: {
                colors: {
                    "primary": "#000000", "on-primary": "#ffffff", "primary-container": "#131b2e",
                    "secondary": "#00687a", "on-secondary": "#ffffff",
                    "secondary-container": "#57dffe", "on-secondary-container": "#006172",
                    "surface": "#f8f9ff", "on-surface": "#0b1c30", "on-surface-variant": "#45464d",
                    "surface-container-lowest": "#ffffff", "surface-container-low": "#eff4ff",
                    "surface-variant": "#d3e4fe", "outline": "#76777d", "outline-variant": "#c6c6cd",
                    "background": "#f8f9ff", "error": "#ba1a1a", "error-container": "#ffdad6",
                },
                fontFamily: { "sans": ["Hanken Grotesk"], "mono": ["JetBrains Mono"] },
            }}
        }
    </script>
    <style>.material-symbols-outlined { font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24; }</style>
</head>
<body class="bg-background text-on-surface min-h-screen font-sans antialiased">

    <c:set var="user" value="${sessionScope.user}" />

    <jsp:include page="/components/sidebar.jsp">
        <jsp:param name="activeMenu" value="bookings" />
    </jsp:include>

    <!-- TopAppBar (Admin Top Bar) -->
    <header class="bg-white border-b border-outline-variant/30 shadow-sm fixed top-0 right-0 w-[calc(100%-250px)] h-16 flex justify-between items-center px-8 z-10">
        <div class="flex items-center gap-2 text-on-surface-variant font-mono text-sm">
            <span>Portal</span>
            <span class="material-symbols-outlined text-sm">chevron_right</span>
            <span class="text-secondary font-bold">Lịch Hẹn</span>
        </div>
        <div class="flex items-center gap-4">
            <button class="w-10 h-10 rounded-full flex items-center justify-center text-on-surface-variant hover:bg-slate-100 transition-colors relative">
                <span class="material-symbols-outlined">notifications</span>
                <span class="absolute top-2.5 right-2.5 w-2 h-2 bg-secondary rounded-full"></span>
            </button>
            <div class="h-8 w-8 rounded-full bg-slate-200 overflow-hidden border border-outline-variant">
                <div class="h-full w-full flex items-center justify-center bg-secondary/10 text-secondary font-bold text-xs uppercase">
                    ${user.fullName.substring(0,1)}
                </div>
            </div>
        </div>
    </header>

    <div class="flex-1 md:ml-[250px] flex flex-col min-h-screen pt-16">
        <main class="flex-grow p-6 lg:p-8 w-full max-w-container-max-width mx-auto">
            
            <!-- Header & Date Filter -->
            <div class="flex flex-col md:flex-row justify-between items-start md:items-end gap-4 mb-8 pb-4 border-b border-outline-variant/20">
                <div>
                    <h1 class="text-3xl font-bold text-on-surface mb-1">Lịch Hẹn</h1>
                    <p class="text-sm text-on-surface-variant">Quản lý và điều phối các lịch hẹn rửa xe trong hệ thống.</p>
                </div>
                <!-- Date Filter Form -->
                <form class="flex items-center gap-3 bg-white px-4 py-2 border border-outline-variant/30 rounded-xl shadow-sm">
                    <label class="font-mono text-xs text-on-surface-variant uppercase tracking-wider">Chọn ngày:</label>
                    <input type="date" name="date" value="<fmt:formatDate value='${filterDate}' pattern='yyyy-MM-dd' />"
                           class="border-transparent p-0 bg-transparent text-sm font-bold text-on-surface focus:ring-0 outline-none cursor-pointer"
                           onchange="this.form.submit()">
                </form>
            </div>

            <c:if test="${not empty error}">
                <div class="mb-6 p-4 bg-error-container border border-error/20 rounded-xl text-error text-sm shadow-sm">${error}</div>
            </c:if>

            <!-- Bookings Table Container -->
            <div class="bg-white border border-outline-variant/30 rounded-2xl shadow-sm overflow-hidden">
                <div class="px-6 py-4 border-b border-outline-variant/20 flex justify-between items-center bg-slate-50/50">
                    <h2 class="text-lg font-bold text-on-surface flex items-center gap-2">
                        <span class="material-symbols-outlined text-secondary">event</span>
                        <fmt:formatDate value="${filterDate}" pattern="EEEE, dd/MM/yyyy" />
                    </h2>
                    <span class="font-mono text-xs text-on-surface-variant bg-slate-100 px-3 py-1 rounded-full border border-outline-variant/20 font-bold">${bookings.size()} cuộc hẹn</span>
                </div>

                <c:choose>
                    <c:when test="${not empty bookings}">
                        <div class="overflow-x-auto">
                            <table class="w-full text-sm">
                                <thead>
                                    <tr class="border-b border-outline-variant/20 bg-slate-50/50">
                                        <th class="px-6 py-4 text-left font-mono text-xs text-on-surface-variant uppercase tracking-wider">Thời gian</th>
                                        <th class="px-6 py-4 text-left font-mono text-xs text-on-surface-variant uppercase tracking-wider">Khách hàng</th>
                                        <th class="px-6 py-4 text-left font-mono text-xs text-on-surface-variant uppercase tracking-wider">Biển số xe</th>
                                        <th class="px-6 py-4 text-left font-mono text-xs text-on-surface-variant uppercase tracking-wider">Tên xe</th>
                                        <th class="px-6 py-4 text-center font-mono text-xs text-on-surface-variant uppercase tracking-wider">Trạng thái</th>
                                        <th class="px-6 py-4 text-center font-mono text-xs text-on-surface-variant uppercase tracking-wider">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody class="divide-y divide-slate-100">
                                    <c:forEach var="booking" items="${bookings}">
                                        <tr class="hover:bg-slate-50/50 transition-colors">
                                            <td class="px-6 py-4 font-bold text-on-surface font-mono">${booking.timeSlot}</td>
                                            <td class="px-6 py-4 font-semibold text-on-surface">${booking.customerName}</td>
                                            <td class="px-6 py-4 font-mono text-secondary font-bold">${booking.plateNumber}</td>
                                            <td class="px-6 py-4 text-on-surface-variant">${booking.vehicleModel != null ? booking.vehicleModel : '—'}</td>
                                            <td class="px-6 py-4 text-center">
                                                <c:choose>
                                                    <c:when test="${booking.status == 'PENDING'}">
                                                        <span class="inline-flex px-3 py-1 rounded-full text-[10px] font-bold font-mono bg-amber-100 text-amber-800 border border-amber-200">PENDING</span>
                                                    </c:when>
                                                    <c:when test="${booking.status == 'COMPLETED'}">
                                                        <span class="inline-flex px-3 py-1 rounded-full text-[10px] font-bold font-mono bg-green-100 text-green-800 border border-green-200">COMPLETED</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="inline-flex px-3 py-1 rounded-full text-[10px] font-bold font-mono bg-red-100 text-red-800 border border-red-200">CANCELLED</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="px-6 py-4">
                                                <div class="flex items-center justify-center gap-2">
                                                    <c:if test="${booking.status == 'PENDING'}">
                                                        <a href="${pageContext.request.contextPath}/admin/checkout?action=customer&bookingId=${booking.bookingId}"
                                                           class="px-3 py-1.5 bg-secondary text-on-secondary rounded-lg font-mono text-xs uppercase hover:bg-on-secondary-container transition-colors shadow-sm"
                                                           title="Thanh toán">Checkout</a>
                                                        <form action="${pageContext.request.contextPath}/admin/bookings" method="POST" class="inline"
                                                              onsubmit="return confirm('Hủy lịch hẹn của xe ${booking.plateNumber}?')">
                                                            <input type="hidden" name="action" value="cancel">
                                                            <input type="hidden" name="bookingId" value="${booking.bookingId}">
                                                            <button class="px-3 py-1.5 border border-error text-error rounded-lg font-mono text-xs uppercase hover:bg-red-50 transition-colors">Hủy</button>
                                                        </form>
                                                    </c:if>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-16 text-on-surface-variant">
                            <span class="material-symbols-outlined text-5xl text-outline mb-2">event_busy</span>
                            <p class="text-sm">Không có lịch hẹn nào được ghi nhận trong ngày này.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>
</body>

</html>
