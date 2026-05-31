<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thông báo - AutoWash Pro</title>
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
        <jsp:param name="activeMenu" value="notifications" />
    </jsp:include>

    <!-- Main Outer Wrapper: md:pl-[280px] để nhường chỗ cho sidebar trên desktop -->
    <div class="flex-1 flex flex-col min-h-screen md:pl-[280px]">
        
        <!-- Header Component (Breadcrumb trên desktop, logo trên mobile) -->
        <jsp:include page="/components/header.jsp">
            <jsp:param name="pageTitle" value="Thông báo" />
        </jsp:include>

        <!-- Main Canvas: mt-16 nhường chỗ cho header fixed, pb-24 nhường chỗ cho bottom-nav mobile -->
        <main class="flex-1 mt-16 p-4 md:p-8 w-full max-w-container-max-width mx-auto">
            <div class="mb-6">
                <h2 class="text-3xl font-bold text-on-surface">Thông báo</h2>
                <p class="text-sm text-on-surface-variant mt-1">
                    Bạn có <span class="font-bold text-secondary">${unreadCount > 0 ? unreadCount : '0'}</span> thông báo chưa đọc.
                </p>
            </div>

            <c:choose>
                <c:when test="${not empty notifications}">
                    <div class="grid grid-cols-1 gap-4 max-w-3xl">
                        <c:forEach var="notif" items="${notifications}">
                            <div class="notif-item bg-white border ${notif.read ? 'border-outline-variant/30' : 'border-secondary/30 bg-secondary/[0.02]'} rounded-2xl p-5 transition-all duration-300 cursor-pointer hover:shadow-sm flex items-start gap-4"
                                 data-id="${notif.notificationId}" data-read="${notif.read}"
                                 onclick="markAsRead(this, ${notif.notificationId})">

                                <%-- Icon trạng thái thông báo --%>
                                <div class="w-12 h-12 rounded-xl flex-shrink-0 flex items-center justify-center ${notif.read ? 'bg-slate-100 text-outline' : 'bg-secondary/10 text-secondary'}">
                                    <span class="material-symbols-outlined text-xl">
                                        ${notif.read ? 'mark_email_read' : 'notifications_active'}
                                    </span>
                                </div>

                                <div class="flex-1 min-w-0">
                                    <div class="flex justify-between items-start gap-4">
                                        <h3 class="text-base font-bold ${notif.read ? 'text-on-surface-variant' : 'text-on-surface'}">${notif.title}</h3>
                                        <c:if test="${!notif.read}">
                                            <span class="w-2.5 h-2.5 rounded-full bg-secondary flex-shrink-0 mt-1.5 animate-pulse" id="dot-${notif.notificationId}"></span>
                                        </c:if>
                                    </div>
                                    <p class="text-sm text-on-surface-variant mt-1.5 leading-relaxed">${notif.content}</p>
                                    <div class="flex items-center gap-2 mt-3 text-xs text-outline font-mono">
                                        <span class="material-symbols-outlined text-sm">schedule</span>
                                        <fmt:formatDate value="${notif.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="text-center py-12 bg-white rounded-2xl border border-outline-variant/30 shadow-sm max-w-3xl">
                        <span class="material-symbols-outlined text-5xl text-outline mb-2">notifications_off</span>
                        <p class="text-on-surface-variant text-sm">Bạn chưa nhận được thông báo nào.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>
    </div>

    <%-- Bottom Navigation (Chỉ hiện trên mobile) --%>
    <jsp:include page="/components/bottom-nav.jsp">
        <jsp:param name="activeTab" value="alerts" />
    </jsp:include>

    <!-- AJAX Mark as Read -->
    <script>
        const contextPath = '${pageContext.request.contextPath}';

        function markAsRead(el, notifId) {
            if (el.dataset.read === 'true') return; // Đã đọc rồi → bỏ qua

            fetch(contextPath + '/customer/notifications', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: 'notificationId=' + notifId
            })
            .then(r => r.json())
            .then(data => {
                if (data.success) {
                    el.dataset.read = 'true';
                    el.classList.remove('border-secondary/30', 'bg-secondary/[0.02]');
                    el.classList.add('border-outline-variant/30');

                    const icon = el.querySelector('.material-symbols-outlined');
                    if (icon) { 
                        icon.textContent = 'mark_email_read'; 
                        const iconWrapper = icon.parentElement;
                        iconWrapper.classList.remove('bg-secondary/10', 'text-secondary');
                        iconWrapper.classList.add('bg-slate-100', 'text-outline');
                    }

                    const dot = document.getElementById('dot-' + notifId);
                    if (dot) dot.remove();

                    // Cập nhật badge trên header
                    const badge = document.getElementById('notifBadge');
                    if (badge && data.unreadCount <= 0) { badge.remove(); }
                    else if (badge) { badge.textContent = data.unreadCount > 9 ? '9+' : data.unreadCount; }
                }
            })
            .catch(err => console.error('Mark as read failed:', err));
        }
    </script>
</body>

</html>
