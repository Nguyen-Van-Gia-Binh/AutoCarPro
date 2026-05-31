<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán nhanh - AutoWash Pro Admin</title>
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
                    "tertiary-fixed-dim": "#ffb95f", "on-tertiary-fixed": "#2a1700",
                    "surface": "#f8f9ff", "on-surface": "#0b1c30", "on-surface-variant": "#45464d",
                    "surface-container-lowest": "#ffffff", "surface-container-low": "#eff4ff",
                    "surface-variant": "#d3e4fe", "outline": "#76777d", "outline-variant": "#c6c6cd",
                    "background": "#f8f9ff", "error": "#ba1a1a", "error-container": "#ffdad6",
                },
                fontFamily: { "sans": ["Hanken Grotesk"], "mono": ["JetBrains Mono"] },
            }}
        }
    </script>
    <style>
        .material-symbols-outlined { font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24; }
        .modal-backdrop { background: rgba(0,0,0,0.5); backdrop-filter: blur(4px); }
        @keyframes modalIn { from { opacity: 0; transform: scale(0.95) translateY(10px); } to { opacity: 1; transform: scale(1) translateY(0); } }
        .modal-content { animation: modalIn 0.3s ease; }
    </style>
</head>
<body class="bg-background text-on-surface min-h-screen font-sans antialiased">

    <c:set var="user" value="${sessionScope.user}" />

    <jsp:include page="/components/sidebar.jsp">
        <jsp:param name="activeMenu" value="checkout" />
    </jsp:include>

    <!-- TopAppBar (Admin Top Bar) -->
    <header class="bg-white border-b border-outline-variant/30 shadow-sm fixed top-0 right-0 w-[calc(100%-250px)] h-16 flex justify-between items-center px-8 z-10">
        <div class="flex items-center gap-2 text-on-surface-variant font-mono text-sm">
            <span>Portal</span>
            <span class="material-symbols-outlined text-sm">chevron_right</span>
            <span class="text-secondary font-bold">Thanh Toán Nhanh</span>
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
            
            <!-- Header -->
            <div class="mb-8 pb-4 border-b border-outline-variant/20">
                <h1 class="text-3xl font-bold text-on-surface mb-1">Thanh Toán Nhanh</h1>
                <p class="text-sm text-on-surface-variant">Tiếp đón khách hàng, nhập hóa đơn và thực hiện tính điểm thưởng realtime.</p>
            </div>

            <!-- Today's Pending Bookings -->
            <div class="bg-white border border-outline-variant/30 rounded-2xl shadow-sm overflow-hidden">
                <div class="px-6 py-4 border-b border-outline-variant/20 flex justify-between items-center bg-slate-50/50">
                    <h2 class="text-lg font-bold text-on-surface flex items-center gap-2">
                        <span class="material-symbols-outlined text-secondary">pending_actions</span>
                        Lịch hẹn đang chờ — <fmt:formatDate value="${today}" pattern="dd/MM/yyyy" />
                    </h2>
                </div>

                <c:choose>
                    <c:when test="${not empty bookings}">
                        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 p-6">
                            <c:forEach var="booking" items="${bookings}">
                                <c:if test="${booking.status == 'PENDING'}">
                                    <div class="border border-outline-variant/30 bg-white rounded-2xl p-5 hover:border-secondary hover:shadow-md transition-all cursor-pointer flex flex-col justify-between group"
                                         onclick="openCheckoutModal(${booking.bookingId})">
                                        <div>
                                            <div class="flex justify-between items-start mb-4">
                                                <span class="inline-flex px-3 py-1 rounded-full text-[10px] font-bold font-mono bg-amber-100 text-amber-800 border border-amber-200">PENDING</span>
                                                <span class="font-mono text-sm font-bold text-secondary">${booking.timeSlot}</span>
                                            </div>
                                            <div class="space-y-3">
                                                <div class="flex items-center gap-3">
                                                    <div class="w-8 h-8 rounded-lg bg-slate-50 flex items-center justify-center text-outline-variant">
                                                        <span class="material-symbols-outlined text-lg">person</span>
                                                    </div>
                                                    <span class="text-sm text-on-surface font-semibold">${booking.customerName}</span>
                                                </div>
                                                <div class="flex items-center gap-3">
                                                    <div class="w-8 h-8 rounded-lg bg-slate-50 flex items-center justify-center text-outline-variant">
                                                        <span class="material-symbols-outlined text-lg">directions_car</span>
                                                    </div>
                                                    <div>
                                                        <span class="text-sm font-mono text-secondary font-bold">${booking.plateNumber}</span>
                                                        <span class="text-xs text-on-surface-variant block mt-0.5">${booking.vehicleModel != null ? booking.vehicleModel : 'Chưa đặt tên'}</span>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="mt-6 flex items-center justify-center gap-2 py-3 bg-secondary/5 rounded-xl text-secondary font-mono text-xs uppercase tracking-wider font-bold group-hover:bg-secondary group-hover:text-white transition-all shadow-sm">
                                            <span class="material-symbols-outlined text-lg">point_of_sale</span> Tiến hành Checkout
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-16 text-on-surface-variant">
                            <span class="material-symbols-outlined text-5xl text-outline mb-2">check_circle</span>
                            <p class="text-sm">Không có lịch hẹn nào cần xử lý thanh toán hôm nay.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>


    <!-- ================================ -->
    <!-- CHECKOUT MODAL (Realtime)        -->
    <!-- ================================ -->
    <div id="checkoutModal" class="fixed inset-0 z-50 hidden items-center justify-center modal-backdrop">
        <div class="modal-content bg-surface-container-lowest rounded-2xl shadow-xl w-full max-w-lg mx-4 max-h-[90vh] overflow-y-auto">
            <!-- Modal Header -->
            <div class="flex justify-between items-center p-6 border-b border-outline-variant/20">
                <h2 class="text-xl font-bold text-on-surface">Checkout</h2>
                <button onclick="closeModal()" class="p-2 rounded-lg hover:bg-surface-variant transition-colors">
                    <span class="material-symbols-outlined">close</span>
                </button>
            </div>

            <!-- Modal Body -->
            <div class="p-6 space-y-6">
                <!-- Customer Info -->
                <div class="bg-surface-container-low rounded-xl p-4 space-y-2" id="customerInfo">
                    <div class="text-center py-4 text-on-surface-variant">Đang tải...</div>
                </div>

                <!-- Bill Amount Input -->
                <div>
                    <label class="block font-mono text-xs text-on-surface-variant mb-2 uppercase tracking-wider">TỔNG HÓA ĐƠN (VNĐ)</label>
                    <input type="number" id="billAmountInput" min="0" step="1000" placeholder="150000"
                           class="w-full px-4 py-3 bg-surface-container-lowest border border-outline-variant/40 rounded-xl text-lg font-bold text-on-surface focus:border-secondary focus:ring-1 focus:ring-secondary outline-none"
                           oninput="calculateRealtime()">
                </div>

                <!-- Redeem Points Slider -->
                <div>
                    <div class="flex justify-between items-center mb-2">
                        <label class="font-mono text-xs text-on-surface-variant uppercase tracking-wider">ĐIỂM ĐỔI GIẢM GIÁ</label>
                        <span id="redeemLabel" class="font-mono text-sm text-secondary font-bold">0 điểm</span>
                    </div>
                    <input type="range" id="redeemSlider" min="0" max="0" value="0" step="10"
                           class="w-full h-2 bg-surface-variant rounded-lg appearance-none cursor-pointer accent-secondary"
                           oninput="calculateRealtime()">
                    <div class="flex justify-between mt-1 font-mono text-xs text-outline">
                        <span>0</span>
                        <span id="maxRedeemLabel">0</span>
                    </div>
                </div>

                <!-- Realtime Calculation Preview -->
                <div class="bg-primary-container text-white rounded-xl p-5 space-y-3">
                    <h3 class="font-mono text-xs uppercase tracking-wider text-slate-400">TỔNG KẾT THANH TOÁN</h3>
                    <div class="flex justify-between items-center">
                        <span class="text-slate-300">Tổng hóa đơn (A)</span>
                        <span id="previewBill" class="font-mono text-lg text-white">0 đ</span>
                    </div>
                    <div class="flex justify-between items-center">
                        <span class="text-slate-300">Giảm trừ điểm (D)</span>
                        <span id="previewDiscount" class="font-mono text-lg text-red-400">- 0 đ</span>
                    </div>
                    <hr class="border-white/10">
                    <div class="flex justify-between items-center">
                        <span class="text-white font-bold text-lg">Thực thu (T)</span>
                        <span id="previewActual" class="font-mono text-2xl font-bold text-secondary-container">0 đ</span>
                    </div>
                    <hr class="border-white/10">
                    <div class="flex justify-between items-center">
                        <span class="text-slate-300">Điểm tích lũy mới (P)</span>
                        <span id="previewEarned" class="font-mono text-lg text-tertiary-fixed-dim">+0</span>
                    </div>
                </div>
            </div>

            <!-- Modal Footer -->
            <div class="p-6 border-t border-outline-variant/20 flex gap-3">
                <button onclick="closeModal()"
                        class="flex-1 py-3 border border-outline-variant rounded-xl font-mono text-sm uppercase text-on-surface-variant hover:bg-surface-variant transition-colors">
                    Hủy
                </button>
                <button onclick="submitCheckout()" id="btnCheckout"
                        class="flex-1 py-3 bg-secondary text-on-secondary rounded-xl font-mono text-sm uppercase flex items-center justify-center gap-2 hover:bg-on-secondary-container transition-colors active:scale-[0.98] disabled:opacity-40"
                        disabled>
                    <span class="material-symbols-outlined text-lg">check</span> Xác nhận
                </button>
            </div>
        </div>
    </div>

    <!-- ================================ -->
    <!-- SUCCESS MODAL (After Checkout)   -->
    <!-- ================================ -->
    <div id="successModal" class="fixed inset-0 z-50 hidden items-center justify-center modal-backdrop">
        <div class="modal-content bg-surface-container-lowest rounded-2xl shadow-xl w-full max-w-sm mx-4 p-8 text-center">
            <div class="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center mx-auto mb-4">
                <span class="material-symbols-outlined text-green-600 text-3xl" style="font-variation-settings: 'FILL' 1;">check_circle</span>
            </div>
            <h2 class="text-2xl font-bold text-on-surface mb-2">Thanh toán thành công!</h2>
            <div id="successDetails" class="text-on-surface-variant space-y-1 mb-6"></div>
            <div id="tierUpgradeBanner" class="hidden bg-tertiary-fixed-dim/20 text-on-tertiary-fixed rounded-xl p-4 mb-4">
                <span class="material-symbols-outlined text-2xl">star</span>
                <p id="tierUpgradeText" class="font-bold mt-1"></p>
            </div>
            <button onclick="location.reload()" class="w-full py-3 bg-secondary text-on-secondary rounded-xl font-mono text-sm uppercase hover:bg-on-secondary-container transition-colors">
                Đóng
            </button>
        </div>
    </div>

    <!-- Checkout Realtime JavaScript -->
    <script>
        const contextPath = '${pageContext.request.contextPath}';
        let currentBookingId = null;
        let customerData = null;

        // === MỞ MODAL: Load thông tin khách hàng ===
        function openCheckoutModal(bookingId) {
            currentBookingId = bookingId;
            const modal = document.getElementById('checkoutModal');
            modal.classList.remove('hidden');
            modal.classList.add('flex');

            // Reset
            document.getElementById('billAmountInput').value = '';
            document.getElementById('redeemSlider').value = 0;
            document.getElementById('btnCheckout').disabled = true;
            resetPreview();

            // AJAX: Lấy thông tin khách hàng
            fetch(contextPath + '/admin/checkout?action=customer&bookingId=' + bookingId)
                .then(r => r.json())
                .then(data => {
                    customerData = data;
                    // Render customer info
                    document.getElementById('customerInfo').innerHTML =
                        '<div class="flex items-center gap-3 mb-3">' +
                            '<div class="w-10 h-10 bg-surface-variant rounded-full flex items-center justify-center"><span class="material-symbols-outlined text-secondary">person</span></div>' +
                            '<div><div class="font-semibold text-on-surface">' + data.customerName + '</div>' +
                            '<div class="font-mono text-xs text-on-surface-variant">' + (data.tierName || '') + ' — x' + (data.multiplier || 1) + '</div></div>' +
                        '</div>' +
                        '<div class="grid grid-cols-2 gap-3">' +
                            '<div class="bg-surface-variant/50 rounded-lg p-3 text-center"><div class="text-2xl font-bold text-secondary">' + data.pointsBalance + '</div><div class="font-mono text-xs text-on-surface-variant">Điểm hiện có</div></div>' +
                            '<div class="bg-surface-variant/50 rounded-lg p-3 text-center"><div class="text-lg font-bold text-on-surface">' + data.plateNumber + '</div><div class="font-mono text-xs text-on-surface-variant">' + (data.vehicleModel || '') + '</div></div>' +
                        '</div>';

                    // Set max redeem slider
                    document.getElementById('redeemSlider').max = data.pointsBalance || 0;
                    document.getElementById('maxRedeemLabel').textContent = (data.pointsBalance || 0) + ' điểm';
                })
                .catch(err => {
                    document.getElementById('customerInfo').innerHTML = '<div class="text-center text-error py-4">Lỗi tải dữ liệu</div>';
                });
        }

        // === TÍNH TOÁN REALTIME ===
        function calculateRealtime() {
            const A = parseFloat(document.getElementById('billAmountInput').value) || 0;
            const P_redeem = parseInt(document.getElementById('redeemSlider').value) || 0;
            const multiplier = customerData ? (customerData.multiplier || 1) : 1;

            // D = P_redeem × 100
            const D = P_redeem * 100;

            // T = max(0, A - D)
            const T = Math.max(0, A - D);

            // P_earn = round(T / 1000 × Multiplier)
            const P_earn = Math.round(T / 1000 * multiplier);

            // Cập nhật giao diện
            document.getElementById('redeemLabel').textContent = P_redeem + ' điểm (= ' + formatVnd(D) + ')';
            document.getElementById('previewBill').textContent = formatVnd(A);
            document.getElementById('previewDiscount').textContent = '- ' + formatVnd(D);
            document.getElementById('previewActual').textContent = formatVnd(T);
            document.getElementById('previewEarned').textContent = '+' + P_earn + ' điểm';

            // Enable/Disable submit button
            document.getElementById('btnCheckout').disabled = (A <= 0);
        }

        // === GỬI CHECKOUT ===
        function submitCheckout() {
            const btn = document.getElementById('btnCheckout');
            btn.disabled = true;
            btn.innerHTML = '<span class="material-symbols-outlined text-lg animate-spin">progress_activity</span> Đang xử lý...';

            const billAmount = document.getElementById('billAmountInput').value;
            const redeemPoints = document.getElementById('redeemSlider').value;

            fetch(contextPath + '/admin/checkout', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: 'bookingId=' + currentBookingId + '&billAmount=' + billAmount + '&redeemPoints=' + redeemPoints
            })
            .then(r => r.json())
            .then(result => {
                closeModal();
                if (result.success) {
                    showSuccess(result);
                } else {
                    alert('Lỗi: ' + (result.error || 'Không xác định'));
                    btn.disabled = false;
                    btn.innerHTML = '<span class="material-symbols-outlined text-lg">check</span> Xác nhận';
                }
            })
            .catch(err => {
                alert('Lỗi kết nối: ' + err.message);
                btn.disabled = false;
                btn.innerHTML = '<span class="material-symbols-outlined text-lg">check</span> Xác nhận';
            });
        }

        // === HIỂN THỊ MODAL THÀNH CÔNG ===
        function showSuccess(result) {
            document.getElementById('successDetails').innerHTML =
                '<p>Thực thu: <strong>' + formatVnd(result.actualAmount) + '</strong></p>' +
                '<p>Điểm tích mới: <strong class="text-secondary">+' + result.earnedPoints + '</strong></p>' +
                '<p>Số dư ví: <strong>' + result.newPointsBalance + ' điểm</strong></p>';

            if (result.tierUpgraded) {
                document.getElementById('tierUpgradeBanner').classList.remove('hidden');
                document.getElementById('tierUpgradeText').textContent = 'Thăng hạng lên ' + result.newTierName + '!';
            }

            const modal = document.getElementById('successModal');
            modal.classList.remove('hidden');
            modal.classList.add('flex');
        }

        // === HELPERS ===
        function closeModal() {
            document.getElementById('checkoutModal').classList.add('hidden');
            document.getElementById('checkoutModal').classList.remove('flex');
        }

        function resetPreview() {
            document.getElementById('previewBill').textContent = '0 đ';
            document.getElementById('previewDiscount').textContent = '- 0 đ';
            document.getElementById('previewActual').textContent = '0 đ';
            document.getElementById('previewEarned').textContent = '+0';
            document.getElementById('redeemLabel').textContent = '0 điểm';
        }

        function formatVnd(num) {
            return new Intl.NumberFormat('vi-VN').format(Math.round(num)) + ' đ';
        }
    </script>
</body>
</html>
