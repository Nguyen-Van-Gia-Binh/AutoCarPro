<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - AutoWash Pro</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;600;700&family=JetBrains+Mono:wght@500&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <script>
        tailwind.config = {
            theme: { extend: {
                colors: {
                    "primary": "#000000", "on-primary": "#ffffff",
                    "primary-container": "#131b2e",
                    "secondary": "#00687a", "on-secondary": "#ffffff",
                    "secondary-container": "#57dffe", "on-secondary-container": "#006172",
                    "tertiary-fixed-dim": "#ffb95f", "on-tertiary-fixed": "#2a1700",
                    "surface": "#f8f9ff", "on-surface": "#0b1c30",
                    "on-surface-variant": "#45464d",
                    "surface-container-lowest": "#ffffff",
                    "surface-container-low": "#eff4ff",
                    "surface-container-highest": "#d3e4fe",
                    "surface-variant": "#d3e4fe",
                    "outline": "#76777d", "outline-variant": "#c6c6cd",
                    "background": "#f8f9ff", "on-background": "#0b1c30",
                    "error": "#ba1a1a", "error-container": "#ffdad6",
                },
                fontFamily: { "sans": ["Hanken Grotesk"], "mono": ["JetBrains Mono"] },
            }}
        }
    </script>
    <style>
        .material-symbols-outlined { font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24; }
        /* Thanh tiến trình animation */
        @keyframes progressGrow { from { width: 0%; } }
        .progress-bar { animation: progressGrow 1s ease-out; }
    </style>
</head>
<body class="bg-background text-on-background min-h-screen flex flex-col font-sans antialiased pb-24 md:pb-0">

    <c:set var="user" value="${sessionScope.user}" />

    <!-- Desktop Customer Sidebar (Ẩn trên mobile) -->
    <jsp:include page="/components/customer-sidebar.jsp">
        <jsp:param name="activeMenu" value="dashboard" />
    </jsp:include>

    <!-- Main Outer Wrapper: md:pl-[280px] để nhường chỗ cho sidebar trên desktop -->
    <div class="flex-1 flex flex-col min-h-screen md:pl-[280px]">
        
        <!-- Header Component (Breadcrumb trên desktop, logo trên mobile) -->
        <jsp:include page="/components/header.jsp">
            <jsp:param name="pageTitle" value="Dashboard" />
        </jsp:include>

        <!-- Main Canvas: mt-16 nhường chỗ cho header fixed, pb-24 nhường chỗ cho bottom-nav mobile -->
        <main class="flex-1 mt-16 p-4 md:p-8 w-full max-w-container-max-width mx-auto">

            <%-- Thông báo đặt lịch thành công --%>
            <c:if test="${param.bookingSuccess == 'true'}">
                <div class="mb-6 p-4 bg-green-50 border border-green-200 rounded-xl text-green-800 text-sm text-center flex items-center justify-center gap-2 shadow-sm animate-pulse">
                    <span class="material-symbols-outlined text-lg">check_circle</span>
                    <span>Đặt lịch thành công! Mã cuộc hẹn đã được lưu.</span>
                </div>
            </c:if>

            <!-- Responsive Grid Layout: 1 cột trên mobile, 12 cột trên desktop -->
            <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 md:gap-8">
                
                <!-- CỘT TRÁI (Rộng): Lời chào & Danh sách xe / Lịch hẹn -->
                <div class="lg:col-span-8 flex flex-col gap-6">
                    
                    <!-- Welcome Card (Style cao cấp từ StitchMCP) -->
                    <div class="relative overflow-hidden rounded-2xl bg-primary-container text-white shadow-sm h-[160px] md:h-[200px] flex items-center p-6 md:p-8 group">
                        <!-- Abstract Tech Background Gradient -->
                        <div class="absolute inset-0 z-0 opacity-10 bg-[radial-gradient(circle_at_bottom_right,rgba(87,223,254,0.6)_0%,rgba(15,23,42,0)_50%)] pointer-events-none"></div>
                        <div class="absolute inset-0 z-0 bg-gradient-to-r from-primary-container via-primary-container/80 to-transparent"></div>
                        <div class="relative z-10">
                            <h2 class="text-3xl md:text-4xl font-bold mb-2">Chào ${user.fullName},</h2>
                            <p class="text-sm md:text-base text-slate-400">Hệ thống đang hoạt động ổn định. Theo dõi dịch vụ và điểm thưởng của bạn bên dưới.</p>
                        </div>
                    </div>

                    <!-- Lịch hẹn sắp tới -->
                    <div>
                        <h3 class="text-xl font-bold text-on-surface mb-3 flex items-center gap-2">
                            <span class="material-symbols-outlined text-secondary">calendar_today</span>
                            Lịch hẹn sắp tới
                        </h3>

                        <c:choose>
                            <c:when test="${upcomingBooking != null}">
                                <div class="bg-white shadow-sm border border-outline-variant/30 rounded-2xl p-6 flex flex-col sm:flex-row items-center gap-6 relative overflow-hidden group hover:shadow-md transition-shadow">
                                    <div class="w-16 h-16 rounded-xl bg-surface-container flex items-center justify-center shrink-0">
                                        <span class="material-symbols-outlined text-3xl text-secondary" style="font-variation-settings: 'FILL' 1;">local_car_wash</span>
                                    </div>
                                    <div class="flex-1 w-full text-center sm:text-left">
                                        <div class="flex flex-wrap items-center justify-center sm:justify-start gap-3 mb-2">
                                            <span class="px-3 py-1 bg-amber-100 text-amber-800 font-mono text-xs uppercase rounded-full border border-amber-200">
                                                ${upcomingBooking.status}
                                            </span>
                                            <span class="text-sm text-outline">Đã đặt chỗ</span>
                                        </div>
                                        <div class="flex flex-wrap items-center justify-center sm:justify-start gap-4 text-sm text-on-surface-variant">
                                            <span class="flex items-center gap-1"><span class="material-symbols-outlined text-[18px]">directions_car</span> Biển số: <strong class="text-on-surface font-semibold">${upcomingBooking.plateNumber}</strong></span>
                                            <span class="hidden sm:inline text-outline-variant">•</span>
                                            <span class="flex items-center gap-1"><span class="material-symbols-outlined text-[18px]">event</span> <fmt:formatDate value="${upcomingBooking.bookingDate}" pattern="dd/MM/yyyy" /></span>
                                            <span class="hidden sm:inline text-outline-variant">•</span>
                                            <span class="flex items-center gap-1"><span class="material-symbols-outlined text-[18px]">schedule</span> ${upcomingBooking.timeSlot}</span>
                                        </div>
                                    </div>
                                    <!-- Decorative corner element -->
                                    <div class="absolute -bottom-4 -right-4 w-24 h-24 bg-slate-50 opacity-50 rounded-full group-hover:scale-110 transition-transform"></div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <!-- Không có booking nào -->
                                <div class="bg-white border border-outline-variant/30 rounded-2xl p-8 text-center shadow-sm">
                                    <span class="material-symbols-outlined text-5xl text-outline mb-2">calendar_today</span>
                                    <p class="text-on-surface-variant text-sm">Bạn chưa có lịch hẹn nào sắp tới.</p>
                                    <a href="${pageContext.request.contextPath}/customer/booking"
                                       class="inline-flex items-center gap-2 mt-4 px-6 py-3 bg-secondary text-white rounded-xl font-mono text-xs uppercase hover:bg-on-secondary-container transition-colors active:scale-95 shadow-sm">
                                        <span class="material-symbols-outlined text-base">add</span>
                                        Đặt lịch rửa xe ngay
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Quick Stats Row (Tổng số lần rửa, hệ số điểm) -->
                    <div class="grid grid-cols-2 gap-4 mt-2">
                        <div class="bg-white border border-outline-variant/30 rounded-2xl p-5 text-center shadow-sm">
                            <div class="text-3xl font-bold text-on-surface">${user.totalWashes}</div>
                            <div class="font-mono text-xs text-on-surface-variant uppercase mt-1">Lần rửa hoàn tất</div>
                        </div>
                        <div class="bg-white border border-outline-variant/30 rounded-2xl p-5 text-center shadow-sm">
                            <div class="text-3xl font-bold text-secondary">x${currentTier.multiplier}</div>
                            <div class="font-mono text-xs text-on-surface-variant uppercase mt-1">Hệ số tích điểm</div>
                        </div>
                    </div>
                </div>

                <!-- CỘT PHẢI (Hẹp): Ví điểm thưởng & Tiến trình phần thưởng -->
                <div class="lg:col-span-4 flex flex-col gap-6">
                    
                    <!-- Reward Wallet Card -->
                    <div class="bg-white rounded-2xl shadow-sm border border-outline-variant/30 p-6 relative overflow-hidden">
                        <div class="absolute -right-8 -top-8 w-32 h-32 bg-secondary/5 rounded-full blur-2xl pointer-events-none"></div>
                        <div class="flex justify-between items-start mb-4">
                            <h3 class="font-mono text-xs text-on-surface-variant uppercase tracking-wider">Reward Wallet</h3>
                            <span class="material-symbols-outlined text-secondary">account_balance_wallet</span>
                        </div>
                        <div class="mb-2">
                            <span class="text-4xl font-bold text-on-surface"><fmt:formatNumber value="${user.pointsBalance}" pattern="#,###" /></span>
                            <span class="font-mono text-sm text-on-surface-variant ml-1">Điểm</span>
                        </div>
                        <p class="text-base text-secondary font-medium">~ ${formattedPointsValue} VNĐ chiết khấu</p>
                    </div>

                    <!-- Next Reward progress card -->
                    <div class="bg-white rounded-2xl shadow-sm border border-outline-variant/30 p-6">
                        <div class="flex justify-between items-center mb-6">
                            <h3 class="font-mono text-xs text-on-surface-variant uppercase tracking-wider">Hạng Thẻ Hiện Tại</h3>
                            <span class="px-3 py-1 bg-tertiary-fixed-dim text-on-tertiary-fixed rounded-full font-mono text-xs uppercase tracking-wider font-bold">
                                ${currentTier.tierName}
                            </span>
                        </div>
                        <div class="mb-4">
                            <div class="flex justify-between font-mono text-xs mb-2">
                                <span class="text-on-surface-variant">Hạng hiện tại</span>
                                <span class="text-secondary font-bold">Mục tiêu: ${progress.nextTierName}</span>
                            </div>
                            <!-- Progress Bar -->
                            <div class="h-3 w-full bg-surface-container rounded-full overflow-hidden">
                                <div class="h-full bg-gradient-to-r from-secondary to-blue-500 rounded-full shadow-[0_0_8px_rgba(0,104,122,0.3)] transition-all duration-1000 progress-bar"
                                     style="width: ${progress.percentage}%"></div>
                            </div>
                            <div class="flex justify-between mt-1 font-mono text-[10px] text-outline">
                                <span>${progress.percentage}% hoàn thành</span>
                                <span>${progress.nextTierName}</span>
                            </div>
                        </div>
                        <p class="text-sm text-on-surface-variant leading-relaxed">
                            ${progress.message}
                        </p>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <%-- Bottom Navigation (Chỉ hiện trên mobile) --%>
    <jsp:include page="/components/bottom-nav.jsp">
        <jsp:param name="activeTab" value="dashboard" />
    </jsp:include>

</body>

</html>
