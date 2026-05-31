<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - AutoWash Pro</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;600;700&family=JetBrains+Mono:wght@500&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <!-- Chart.js CDN (theo yêu cầu tài liệu PRJ301) -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.4/dist/chart.umd.min.js"></script>
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
                    "surface-variant": "#d3e4fe",
                    "outline": "#76777d", "outline-variant": "#c6c6cd",
                    "background": "#f8f9ff", "error": "#ba1a1a",
                },
                fontFamily: { "sans": ["Hanken Grotesk"], "mono": ["JetBrains Mono"] },
            }}
        }
    </script>
    <style>.material-symbols-outlined { font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24; }</style>
</head>
<body class="bg-background text-on-background min-h-screen font-sans antialiased">

    <c:set var="user" value="${sessionScope.user}" />

    <%-- Sidebar --%>
    <jsp:include page="/components/sidebar.jsp">
        <jsp:param name="activeMenu" value="dashboard" />
    </jsp:include>

    <!-- TopAppBar (Admin Top Bar) -->
    <header class="bg-white border-b border-outline-variant/30 shadow-sm fixed top-0 right-0 w-[calc(100%-250px)] h-16 flex justify-between items-center px-8 z-10">
        <div class="flex items-center gap-2 text-on-surface-variant font-mono text-sm">
            <span>Portal</span>
            <span class="material-symbols-outlined text-sm">chevron_right</span>
            <span class="text-secondary font-bold">Dashboard</span>
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

    <!-- Main Content Wrapper: md:ml-[250px] for admin sidebar -->
    <div class="flex-1 md:ml-[250px] flex flex-col min-h-screen pt-16">
        
        <!-- Canvas -->
        <main class="flex-grow p-6 md:p-8 w-full max-w-container-max-width mx-auto">
            <div class="flex flex-col gap-8">
                
                <!-- Page Header -->
                <div class="flex justify-between items-end pb-4 border-b border-outline-variant/20">
                    <div>
                        <h2 class="text-3xl font-bold text-on-surface mb-1">Dashboard</h2>
                        <p class="text-sm text-on-surface-variant">Tổng quan hoạt động và dữ liệu thời gian thực của trạm rửa xe.</p>
                    </div>
                    <button onclick="alert('Báo cáo đã được xuất thành công!')" 
                            class="px-5 py-2.5 rounded-xl bg-white border border-outline-variant/40 font-mono text-xs text-on-surface flex items-center gap-2 hover:bg-slate-50 transition-colors shadow-sm">
                        <span class="material-symbols-outlined text-[16px]">download</span>
                        Xuất Báo Cáo
                    </button>
                </div>

                <!-- Bento Grid: Stats Summary -->
                <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <!-- Stat 1: Total Revenue -->
                    <div class="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm flex flex-col justify-between relative overflow-hidden group hover:shadow-md transition-shadow">
                        <div class="absolute top-0 right-0 p-4 opacity-5 group-hover:opacity-10 transition-opacity">
                            <span class="material-symbols-outlined text-6xl">payments</span>
                        </div>
                        <div>
                            <span class="font-mono text-xs text-on-surface-variant uppercase tracking-wider block mb-2">Tổng Doanh Thu</span>
                            <div class="flex items-baseline gap-2">
                                <h3 class="text-4xl font-bold text-on-surface font-mono">145.200K</h3>
                                <span class="font-mono text-xs text-secondary flex items-center gap-0.5"><span class="material-symbols-outlined text-[14px]">arrow_upward</span> 8.2%</span>
                            </div>
                        </div>
                        <div class="mt-4 pt-4 border-t border-slate-100">
                            <span class="font-mono text-[10px] text-outline">So với 7 ngày trước</span>
                        </div>
                    </div>

                    <!-- Stat 2: Total Washes -->
                    <div class="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm flex flex-col justify-between relative overflow-hidden group hover:shadow-md transition-shadow">
                        <div class="absolute top-0 right-0 p-4 opacity-5 group-hover:opacity-10 transition-opacity">
                            <span class="material-symbols-outlined text-6xl">local_car_wash</span>
                        </div>
                        <div>
                            <span class="font-mono text-xs text-on-surface-variant uppercase tracking-wider block mb-2">Lượt Rửa Hoàn Tất</span>
                            <div class="flex items-baseline gap-2">
                                <h3 class="text-4xl font-bold text-on-surface font-mono">1,284</h3>
                                <span class="font-mono text-xs text-secondary flex items-center gap-0.5"><span class="material-symbols-outlined text-[14px]">arrow_upward</span> 3.1%</span>
                            </div>
                        </div>
                        <div class="mt-4 pt-4 border-t border-slate-100">
                            <span class="font-mono text-[10px] text-outline">So với 7 ngày trước</span>
                        </div>
                    </div>

                    <!-- Stat 3: Active Bookings (Secondary highlight color) -->
                    <div class="bg-secondary rounded-2xl p-6 shadow-md flex flex-col justify-between relative overflow-hidden text-white">
                        <div class="absolute -right-6 -top-6 w-32 h-32 bg-white/10 rounded-full blur-2xl"></div>
                        <div>
                            <span class="font-mono text-xs text-white/80 uppercase tracking-wider block mb-2">Lịch Hẹn Hôm Nay</span>
                            <div class="flex items-baseline gap-2">
                                <h3 class="text-4xl font-bold font-mono">${pendingBookings}</h3>
                                <span class="font-mono text-xs text-cyan-200">Đang đợi</span>
                            </div>
                        </div>
                        <div class="mt-4 pt-4 border-t border-white/20 flex justify-between items-center">
                            <div class="flex -space-x-1.5">
                                <div class="px-2.5 py-1 rounded-full bg-white/20 border border-white/10 text-[10px] font-mono">Bay 1</div>
                                <div class="px-2.5 py-1 rounded-full bg-white/20 border border-white/10 text-[10px] font-mono">Bay 2</div>
                                <div class="px-2.5 py-1 rounded-full bg-white/20 border border-white/10 text-[10px] font-mono">+${pendingBookings}</div>
                            </div>
                            <a href="${pageContext.request.contextPath}/admin/bookings" class="text-white font-mono text-xs flex items-center hover:underline">
                                Xem tất cả <span class="material-symbols-outlined text-sm ml-0.5">chevron_right</span>
                            </a>
                        </div>
                    </div>
                </div>

                <!-- Main Data Vis Row (Chart & Configuration) -->
                <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    <!-- Chart: Revenue Growth (Bar Chart) -->
                    <div class="lg:col-span-2 bg-white rounded-2xl border border-slate-200 shadow-sm p-6 flex flex-col">
                        <div class="flex justify-between items-center mb-6">
                            <h3 class="text-lg font-bold text-on-surface">Biểu Đồ Doanh Thu Tuần</h3>
                            <select class="bg-slate-50 border-transparent rounded-lg font-mono text-xs text-on-surface py-1.5 pl-3 pr-8 focus:border-secondary focus:ring-1 focus:ring-secondary cursor-pointer">
                                <option>Tuần này</option>
                                <option>Tuần trước</option>
                                <option>Tháng này</option>
                            </select>
                        </div>
                        <div class="h-[300px] w-full flex-grow">
                            <canvas id="revenueChart"></canvas>
                        </div>
                    </div>

                    <!-- Chart: Tier Distribution (Doughnut) -->
                    <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 flex flex-col justify-between">
                        <h3 class="text-lg font-bold text-on-surface mb-4">Phân Bố Hạng Thẻ</h3>
                        <div class="relative h-[200px] w-full flex justify-center items-center">
                            <canvas id="tierChart"></canvas>
                            <!-- Custom Center Text showing total members -->
                            <div class="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
                                <span class="text-3xl font-bold text-on-surface font-mono">${totalCustomers}</span>
                                <span class="font-mono text-[10px] text-on-surface-variant uppercase">Thành viên</span>
                            </div>
                        </div>
                        <!-- Custom Legend matching designs -->
                        <div class="mt-6 grid grid-cols-2 gap-3">
                            <div class="flex items-center gap-2">
                                <div class="w-3 h-3 rounded-full bg-slate-400"></div>
                                <span class="font-mono text-xs text-on-surface-variant">Member (${memberCount})</span>
                            </div>
                            <div class="flex items-center gap-2">
                                <div class="w-3 h-3 rounded-full bg-slate-300"></div>
                                <span class="font-mono text-xs text-on-surface-variant">Silver (${silverCount})</span>
                            </div>
                            <div class="flex items-center gap-2">
                                <div class="w-3 h-3 rounded-full bg-secondary"></div>
                                <span class="font-mono text-xs text-on-surface-variant">Gold (${goldCount})</span>
                            </div>
                            <div class="flex items-center gap-2">
                                <div class="w-3 h-3 rounded-full bg-primary-container"></div>
                                <span class="font-mono text-xs text-on-surface-variant">Platinum (${platinumCount})</span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Bottom Row: Recent Alerts & Hardware Status -->
                <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    <!-- Notifications/Alerts Widget -->
                    <div class="lg:col-span-1 bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden flex flex-col h-full">
                        <div class="p-4 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
                            <h3 class="text-base font-bold text-on-surface">Cảnh Báo Gần Đây</h3>
                            <button class="text-secondary hover:text-secondary-container transition-colors">
                                <span class="material-symbols-outlined text-[20px]">more_horiz</span>
                            </button>
                        </div>
                        <ul class="flex flex-col divide-y divide-slate-100 overflow-y-auto max-h-[320px]">
                            <li class="p-4 hover:bg-slate-50 transition-colors flex gap-4 items-start">
                                <div class="w-8 h-8 rounded-full bg-error-container text-error flex items-center justify-center shrink-0">
                                    <span class="material-symbols-outlined text-[16px]">warning</span>
                                </div>
                                <div>
                                    <p class="text-sm font-bold text-on-surface mb-0.5">Lỗi cảm biến Bay 3</p>
                                    <p class="text-xs text-on-surface-variant leading-relaxed">Cảm biến tiệm cận báo chỉ số không ổn định. Yêu cầu kiểm tra kỹ thuật.</p>
                                    <span class="text-[10px] text-outline font-mono mt-1 block">10 phút trước</span>
                                </div>
                            </li>
                            <li class="p-4 hover:bg-slate-50 transition-colors flex gap-4 items-start">
                                <div class="w-8 h-8 rounded-full bg-amber-100 text-amber-800 flex items-center justify-center shrink-0">
                                    <span class="material-symbols-outlined text-[16px]">inventory_2</span>
                                </div>
                                <div>
                                    <p class="text-sm font-bold text-on-surface mb-0.5">Dung dịch tẩy rửa thấp</p>
                                    <p class="text-xs text-on-surface-variant leading-relaxed">Hóa chất tạo bọt bóng còn dưới 15%. Cần nạp thêm bình chứa.</p>
                                    <span class="text-[10px] text-outline font-mono mt-1 block">1 giờ trước</span>
                                </div>
                            </li>
                            <li class="p-4 hover:bg-slate-50 transition-colors flex gap-4 items-start">
                                <div class="w-8 h-8 rounded-full bg-green-100 text-green-800 flex items-center justify-center shrink-0">
                                    <span class="material-symbols-outlined text-[16px]">check_circle</span>
                                </div>
                                <div>
                                    <p class="text-sm font-bold text-on-surface mb-0.5">Cập nhật cổng thanh toán</p>
                                    <p class="text-xs text-on-surface-variant leading-relaxed">Đã nâng cấp firmware cổng thẻ RFID thành công trên toàn trạm.</p>
                                    <span class="text-[10px] text-outline font-mono mt-1 block">Hôm qua</span>
                                </div>
                            </li>
                        </ul>
                    </div>

                    <!-- Hardware Status Module -->
                    <div class="lg:col-span-2 bg-white rounded-2xl border border-slate-200 shadow-sm p-6 flex flex-col justify-center items-center text-center">
                        <div class="w-14 h-14 rounded-full bg-slate-50 mb-4 flex items-center justify-center text-secondary">
                            <span class="material-symbols-outlined text-3xl">construction</span>
                        </div>
                        <h4 class="text-lg font-bold text-on-surface mb-1">Hardware Status Module</h4>
                        <p class="text-sm text-on-surface-variant max-w-md">Dữ liệu viễn thám chi tiết từ các cổng (Bays) đang được cấu hình đồng bộ trực tiếp ở phiên bản tiếp theo.</p>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <!-- Chart.js Scripts -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Thiết lập font chữ & mặc định cho Chart.js
            Chart.defaults.font.family = "'JetBrains Mono', monospace";
            Chart.defaults.color = '#76777d';
            Chart.defaults.scale.grid.color = '#f1f5f9';

            // 1. Biểu đồ doanh thu tuần (Bar Chart)
            const revCtx = document.getElementById('revenueChart').getContext('2d');
            new Chart(revCtx, {
                type: 'bar',
                data: {
                    labels: ['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN'],
                    datasets: [{
                        label: 'Doanh thu (K VNĐ)',
                        data: [12000, 19000, 15000, 22000, 28000, 35000, 31000],
                        backgroundColor: '#00687a',
                        borderRadius: 6,
                        barPercentage: 0.5,
                        hoverBackgroundColor: '#004e5c'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            border: { display: false },
                            ticks: {
                                callback: function(value) { return value + 'K'; },
                                maxTicksLimit: 6
                            }
                        },
                        x: {
                            border: { display: false },
                            grid: { display: false }
                        }
                    }
                }
            });

            // 2. Biểu đồ Doughnut phân bố hạng thẻ
            const tierCtx = document.getElementById('tierChart').getContext('2d');
            new Chart(tierCtx, {
                type: 'doughnut',
                data: {
                    labels: ['Member', 'Silver', 'Gold', 'Platinum'],
                    datasets: [{
                        data: [${memberCount}, ${silverCount}, ${goldCount}, ${platinumCount}],
                        backgroundColor: [
                            '#94a3b8', // slate-400
                            '#cbd5e1', // slate-300
                            '#00687a', // secondary
                            '#131b2e'  // primary-container
                        ],
                        borderWidth: 0,
                        hoverOffset: 6
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    cutout: '75%',
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            backgroundColor: '#131b2e',
                            titleFont: { family: 'JetBrains Mono', size: 11 },
                            bodyFont: { family: 'Hanken Grotesk', size: 13 },
                            padding: 10,
                            cornerRadius: 8,
                            callbacks: {
                                label: function(context) {
                                    return ' ' + context.label + ': ' + context.raw + ' TV';
                                }
                            }
                        }
                    }
                }
            });
        });
    </script>
</body>
</html>
