<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xe của tôi - AutoWash Pro</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;600;700&family=JetBrains+Mono:wght@500&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <script>
        tailwind.config = {
            theme: { extend: {
                colors: {
                    "primary": "#000000", "on-primary": "#ffffff", "secondary": "#00687a", "on-secondary": "#ffffff",
                    "secondary-container": "#57dffe", "on-secondary-container": "#006172",
                    "surface": "#f8f9ff", "on-surface": "#0b1c30", "on-surface-variant": "#45464d",
                    "surface-container-lowest": "#ffffff", "surface-variant": "#d3e4fe",
                    "outline": "#76777d", "outline-variant": "#c6c6cd", "background": "#f8f9ff",
                    "error": "#ba1a1a", "error-container": "#ffdad6",
                },
                fontFamily: { "sans": ["Hanken Grotesk"], "mono": ["JetBrains Mono"] },
            }}
        }
    </script>
    <style>.material-symbols-outlined { font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24; }</style>
</head>
<body class="bg-background text-on-background min-h-screen flex flex-col font-sans antialiased pb-24 md:pb-0">

    <!-- Desktop Customer Sidebar (Ẩn trên mobile) -->
    <jsp:include page="/components/customer-sidebar.jsp">
        <jsp:param name="activeMenu" value="cars" />
    </jsp:include>

    <!-- Main Outer Wrapper: md:pl-[280px] để nhường chỗ cho sidebar trên desktop -->
    <div class="flex-1 flex flex-col min-h-screen md:pl-[280px]">
        
        <!-- Header Component (Breadcrumb trên desktop, logo trên mobile) -->
        <jsp:include page="/components/header.jsp">
            <jsp:param name="pageTitle" value="Xe của tôi" />
        </jsp:include>

        <!-- Main Canvas: mt-16 nhường chỗ cho header fixed, pb-24 nhường chỗ cho bottom-nav mobile -->
        <main class="flex-1 mt-16 p-4 md:p-8 w-full max-w-container-max-width mx-auto">
            <div class="mb-6">
                <h2 class="text-3xl font-bold text-on-surface">Xe của tôi</h2>
                <p class="text-sm text-on-surface-variant mt-1">Quản lý biển số xe đã đăng ký phục vụ đặt lịch hẹn.</p>
            </div>

            <%-- Thông báo --%>
            <c:if test="${param.added == 'true'}">
                <div class="mb-6 p-4 bg-green-50 border border-green-200 rounded-xl text-green-800 text-sm text-center flex items-center justify-center gap-2 shadow-sm">
                    <span class="material-symbols-outlined text-lg">check_circle</span>
                    <span>Thêm xe mới thành công!</span>
                </div>
            </c:if>
            <c:if test="${param.deleted == 'true'}">
                <div class="mb-6 p-4 bg-green-50 border border-green-200 rounded-xl text-green-800 text-sm text-center flex items-center justify-center gap-2 shadow-sm">
                    <span class="material-symbols-outlined text-lg">check_circle</span>
                    <span>Đã gỡ biển số xe thành công!</span>
                </div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="mb-6 p-4 bg-error-container border border-error/20 rounded-xl text-error text-sm text-center shadow-sm">${error}</div>
            </c:if>

            <!-- Grid 2 Cột trên Desktop (Form bên trái, Danh sách bên phải) -->
            <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
                
                <!-- Cột trái: Form Thêm Xe Mới (lg:col-span-5) -->
                <div class="lg:col-span-5">
                    <form action="${pageContext.request.contextPath}/customer/my-cars" method="POST"
                          class="bg-white border border-outline-variant/30 rounded-2xl p-6 shadow-sm">
                        <input type="hidden" name="action" value="add">
                        <h3 class="font-mono text-xs font-bold text-on-surface-variant uppercase tracking-wider mb-4 border-b border-outline-variant/20 pb-2">Đăng Ký Xe Mới</h3>
                        
                        <div class="space-y-4">
                            <div class="flex flex-col gap-1.5">
                                <label class="font-mono text-xs text-on-surface-variant uppercase tracking-wider ml-1" for="plateNumber">Biển Số Xe</label>
                                <input type="text" id="plateNumber" name="plateNumber" placeholder="30A1-1234" required
                                       pattern="^\d{2}[A-Za-z]\d{1}-\d{4,5}$"
                                       class="w-full px-4 py-3 bg-slate-50 border-transparent focus:border-secondary focus:bg-white focus:ring-1 focus:ring-secondary rounded-xl text-sm text-on-surface placeholder:text-outline/40 transition-colors outline-none uppercase font-bold">
                                <span class="text-[10px] text-outline ml-1">Định dạng VN: 30A1-1234 hoặc 30A1-12345</span>
                            </div>
                            <div class="flex flex-col gap-1.5">
                                <label class="font-mono text-xs text-on-surface-variant uppercase tracking-wider ml-1" for="vehicleModel">Hãng xe / Tên xe (tùy chọn)</label>
                                <input type="text" id="vehicleModel" name="vehicleModel" placeholder="Toyota Camry 2024"
                                       class="w-full px-4 py-3 bg-slate-50 border-transparent focus:border-secondary focus:bg-white focus:ring-1 focus:ring-secondary rounded-xl text-sm text-on-surface placeholder:text-outline/40 transition-colors outline-none">
                            </div>
                            <button type="submit"
                                    class="w-full py-3.5 bg-secondary text-on-secondary rounded-xl font-mono text-sm uppercase flex items-center justify-center gap-2 hover:bg-on-secondary-container transition-colors active:scale-[0.98] shadow-sm">
                                <span class="material-symbols-outlined text-lg">add</span> Đăng Ký Xe
                            </button>
                        </div>
                    </form>
                </div>

                <!-- Cột phải: Danh sách Xe Đã Đăng Ký (lg:col-span-7) -->
                <div class="lg:col-span-7 space-y-4">
                    <h3 class="font-mono text-xs font-bold text-on-surface-variant uppercase tracking-wider pb-2 border-b border-outline-variant/20 ml-1">Danh Sách Xe Đã Đăng Ký (${plates.size()})</h3>
                    
                    <c:choose>
                        <c:when test="${not empty plates}">
                            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                <c:forEach var="plate" items="${plates}">
                                    <div class="bg-white border border-outline-variant/20 rounded-2xl p-5 flex items-center justify-between shadow-sm hover:shadow-md transition-all relative overflow-hidden group">
                                        <div class="flex items-center gap-4 z-10">
                                            <div class="w-12 h-12 rounded-xl bg-slate-50 flex items-center justify-center text-on-surface-variant shrink-0">
                                                <span class="material-symbols-outlined text-2xl text-secondary">directions_car</span>
                                            </div>
                                            <div>
                                                <h4 class="text-base font-bold text-on-surface truncate max-w-[150px]">${plate.vehicleModel != null ? plate.vehicleModel : 'Chưa đặt tên'}</h4>
                                                <p class="font-mono text-sm text-secondary font-bold">${plate.plateNumber}</p>
                                            </div>
                                        </div>
                                        <form action="${pageContext.request.contextPath}/customer/my-cars" method="POST" class="z-10"
                                              onsubmit="return confirm('Bạn có chắc muốn gỡ bỏ xe ${plate.plateNumber}?')">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="plateId" value="${plate.plateId}">
                                            <button type="submit" class="p-2 rounded-xl text-error hover:bg-red-50 transition-colors cursor-pointer" title="Xóa xe">
                                                <span class="material-symbols-outlined">delete</span>
                                            </button>
                                        </form>
                                        <!-- background shape decor -->
                                        <div class="absolute -right-6 -bottom-6 w-20 h-20 bg-slate-50 opacity-50 rounded-full group-hover:scale-110 transition-transform"></div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-12 bg-white rounded-2xl border border-outline-variant/30 shadow-sm">
                                <span class="material-symbols-outlined text-5xl text-outline mb-2">no_crash</span>
                                <p class="text-on-surface-variant text-sm">Bạn chưa đăng ký xe nào.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </main>
    </div>

    <%-- Bottom Navigation (Chỉ hiện trên mobile) --%>
    <jsp:include page="/components/bottom-nav.jsp">
        <jsp:param name="activeTab" value="cars" />
    </jsp:include>
</body>

</html>
