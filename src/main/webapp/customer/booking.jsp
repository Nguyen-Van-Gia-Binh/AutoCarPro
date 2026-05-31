<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt lịch rửa xe - AutoWash Pro</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;600;700&family=JetBrains+Mono:wght@500&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <script>
        tailwind.config = {
            theme: { extend: {
                colors: {
                    "primary": "#000000", "on-primary": "#ffffff",
                    "secondary": "#00687a", "on-secondary": "#ffffff",
                    "secondary-container": "#57dffe", "on-secondary-container": "#006172",
                    "surface": "#f8f9ff", "on-surface": "#0b1c30",
                    "on-surface-variant": "#45464d",
                    "surface-container-lowest": "#ffffff",
                    "surface-container-low": "#eff4ff",
                    "surface-variant": "#d3e4fe",
                    "outline": "#76777d", "outline-variant": "#c6c6cd",
                    "background": "#f8f9ff", "error": "#ba1a1a", "error-container": "#ffdad6",
                },
                fontFamily: { "sans": ["Hanken Grotesk"], "mono": ["JetBrains Mono"] },
            }}
        }
    </script>
    <style>
        .material-symbols-outlined { font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24; }
        .wizard-step { display: none; }
        .wizard-step.active { display: block; animation: fadeIn 0.3s ease; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }
        .slot-btn.selected { background-color: #00687a; color: white; border-color: #00687a; }
        .slot-btn.disabled { opacity: 0.4; cursor: not-allowed; }
        .car-card.selected { border-color: #00687a; background-color: #eff4ff; }
        .date-btn.selected { background-color: #00687a; color: white; }
    </style>
</head>
<body class="bg-background text-on-background min-h-screen flex flex-col font-sans antialiased pb-24 md:pb-0">

    <c:set var="user" value="${sessionScope.user}" />

    <!-- Desktop Customer Sidebar (Ẩn trên mobile) -->
    <jsp:include page="/components/customer-sidebar.jsp">
        <jsp:param name="activeMenu" value="booking" />
    </jsp:include>

    <!-- Main Outer Wrapper: md:pl-[280px] để nhường chỗ cho sidebar trên desktop -->
    <div class="flex-1 flex flex-col min-h-screen md:pl-[280px]">
        
        <!-- Header Component (Breadcrumb trên desktop, logo trên mobile) -->
        <jsp:include page="/components/header.jsp">
            <jsp:param name="pageTitle" value="Đặt lịch" />
        </jsp:include>

        <!-- Main Canvas: mt-16 nhường chỗ cho header fixed, pb-24 nhường chỗ cho bottom-nav mobile -->
        <main class="flex-1 mt-16 p-4 md:p-8 w-full max-w-container-max-width mx-auto">

            <%-- Error message --%>
            <c:if test="${not empty error}">
                <div class="mb-4 p-4 bg-error-container border border-error/20 rounded-xl text-error text-sm text-center shadow-sm">${error}</div>
            </c:if>

            <div class="mb-6">
                <h2 class="text-3xl font-bold text-on-surface">Đăng ký lịch hẹn</h2>
                <p class="text-sm text-on-surface-variant mt-1">Hoàn thành các bước dưới đây để đặt lịch chăm sóc xe của bạn.</p>
            </div>

            <!-- Centralized Booking Card -->
            <div class="bg-white rounded-2xl shadow-sm border border-outline-variant/30 p-6 md:p-8 flex flex-col min-h-[450px]">
                
                <!-- Wizard Progress Bar (Responsive style from StitchMCP) -->
                <div class="mb-10 w-full max-w-3xl mx-auto">
                    <div class="flex items-center justify-between relative">
                        <div class="absolute left-0 top-1/2 -translate-y-1/2 w-full h-1 bg-surface-variant -z-10 rounded-full"></div>
                        
                        <!-- Step 1 -->
                        <div class="flex flex-col items-center gap-2">
                            <div id="stepDot1" class="w-10 h-10 rounded-full flex items-center justify-center font-mono text-sm font-bold shadow-md ring-4 ring-white transition-all duration-300 bg-secondary text-white">
                                1
                            </div>
                            <span class="font-mono text-[10px] md:text-xs text-secondary uppercase tracking-wider font-semibold">Chọn Xe</span>
                        </div>
                        
                        <!-- Step 2 -->
                        <div class="flex flex-col items-center gap-2">
                            <div id="stepDot2" class="w-10 h-10 rounded-full flex items-center justify-center font-mono text-sm ring-4 ring-white transition-all duration-300 bg-surface-variant text-on-surface-variant">
                                2
                            </div>
                            <span class="font-mono text-[10px] md:text-xs text-on-surface-variant uppercase tracking-wider">Chọn Ngày</span>
                        </div>
                        
                        <!-- Step 3 -->
                        <div class="flex flex-col items-center gap-2">
                            <div id="stepDot3" class="w-10 h-10 rounded-full flex items-center justify-center font-mono text-sm ring-4 ring-white transition-all duration-300 bg-surface-variant text-on-surface-variant">
                                3
                            </div>
                            <span class="font-mono text-[10px] md:text-xs text-on-surface-variant uppercase tracking-wider">Chọn Giờ</span>
                        </div>
                        
                        <!-- Step 4 -->
                        <div class="flex flex-col items-center gap-2">
                            <div id="stepDot4" class="w-10 h-10 rounded-full flex items-center justify-center font-mono text-sm ring-4 ring-white transition-all duration-300 bg-surface-variant text-on-surface-variant">
                                4
                            </div>
                            <span class="font-mono text-[10px] md:text-xs text-on-surface-variant uppercase tracking-wider">Xác Nhận</span>
                        </div>
                    </div>
                </div>

                <!-- Hidden form for final submission -->
                <form id="bookingForm" action="${pageContext.request.contextPath}/customer/booking" method="POST">
                    <input type="hidden" name="plateId" id="selectedPlateId">
                    <input type="hidden" name="bookingDate" id="selectedDate">
                    <input type="hidden" name="timeSlot" id="selectedSlot">
                    <input type="hidden" name="notes" id="selectedNotes">
                </form>

                <!-- ======================== -->
                <!-- STEP 1: Chọn Xe         -->
                <!-- ======================== -->
                <div id="step1" class="wizard-step active flex-grow flex flex-col">
                    <div class="mb-4 flex justify-between items-center border-b border-outline-variant/30 pb-3">
                        <h3 class="text-lg font-bold text-on-surface">Bước 1: Chọn Xe</h3>
                    </div>
                    
                    <c:choose>
                        <c:when test="${not empty plates}">
                            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 flex-grow">
                                <c:forEach var="plate" items="${plates}">
                                    <div class="car-card bg-white border border-outline-variant/40 rounded-xl p-5 cursor-pointer hover:border-secondary hover:shadow-md transition-all active:scale-[0.98] flex flex-col justify-between relative overflow-hidden group"
                                         data-plate-id="${plate.plateId}" data-plate-number="${plate.plateNumber}" data-vehicle-model="${plate.vehicleModel}"
                                         onclick="selectCar(this)">
                                        <div class="flex items-center gap-4">
                                            <div class="w-12 h-12 rounded-lg bg-slate-50 flex items-center justify-center text-on-surface-variant">
                                                <span class="material-symbols-outlined text-2xl text-secondary">directions_car</span>
                                            </div>
                                            <div>
                                                <h4 class="text-base font-bold text-on-surface">${plate.vehicleModel != null ? plate.vehicleModel : 'Chưa đặt tên'}</h4>
                                                <p class="font-mono text-sm text-secondary font-bold">${plate.plateNumber}</p>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                                <!-- Nút Đăng ký xe mới -->
                                <a href="${pageContext.request.contextPath}/customer/my-cars" 
                                   class="border-2 border-dashed border-outline-variant hover:border-secondary rounded-xl p-5 flex flex-col items-center justify-center text-center hover:bg-slate-50/50 transition-colors group cursor-pointer">
                                    <div class="w-10 h-10 rounded-full bg-slate-100 flex items-center justify-center text-on-surface-variant group-hover:bg-secondary/10 group-hover:text-secondary transition-colors mb-2">
                                        <span class="material-symbols-outlined text-xl">add</span>
                                    </div>
                                    <span class="font-semibold text-sm text-on-surface group-hover:text-secondary transition-colors">Đăng ký thêm xe</span>
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-12 flex-grow flex flex-col justify-center items-center">
                                <span class="material-symbols-outlined text-5xl text-outline mb-2">no_crash</span>
                                <p class="text-on-surface-variant text-sm">Bạn chưa có xe nào. Hãy thêm xe trước khi đặt lịch.</p>
                                <a href="${pageContext.request.contextPath}/customer/my-cars" class="inline-block mt-4 px-6 py-3 bg-secondary text-white rounded-xl font-mono text-xs uppercase shadow-sm">Thêm xe ngay</a>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <div class="mt-8 pt-4 border-t border-outline-variant/30 flex justify-end">
                        <button type="button" id="btnNext1" onclick="goToStep(2)" disabled
                                class="px-8 py-3 bg-secondary text-on-secondary rounded-xl font-mono text-xs uppercase tracking-wider flex items-center gap-2 disabled:opacity-40 disabled:cursor-not-allowed transition-all hover:bg-on-secondary-container active:scale-[0.98] shadow-sm">
                            Tiếp theo <span class="material-symbols-outlined text-sm">arrow_forward</span>
                        </button>
                    </div>
                </div>

                <!-- ======================== -->
                <!-- STEP 2: Chọn Ngày       -->
                <!-- ======================== -->
                <div id="step2" class="wizard-step flex-grow flex flex-col">
                    <div class="mb-4 flex justify-between items-center border-b border-outline-variant/30 pb-3">
                        <h3 class="text-lg font-bold text-on-surface">Bước 2: Chọn Ngày</h3>
                        <p class="text-xs text-on-surface-variant">Hạng <span class="font-bold text-secondary">${currentTier.tierName}</span> — Đặt trước tối đa <span class="font-bold">${bookingDays}</span> ngày</p>
                    </div>

                    <div class="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-7 gap-3 mt-4 flex-grow">
                        <c:forEach var="d" items="${availableDates}">
                            <button type="button" class="date-btn py-4 px-3 rounded-xl border border-outline-variant/40 text-center hover:border-secondary hover:bg-slate-50 transition-all flex flex-col items-center justify-center cursor-pointer"
                                    data-date="<fmt:formatDate value='${d}' pattern='yyyy-MM-dd' />"
                                    onclick="selectDate(this)">
                                <span class="font-bold text-lg text-on-surface"><fmt:formatDate value="${d}" pattern="dd" /></span>
                                <span class="text-xs text-on-surface-variant uppercase mt-1"><fmt:formatDate value="${d}" pattern="EEE" /></span>
                                <span class="text-[10px] text-outline mt-0.5"><fmt:formatDate value="${d}" pattern="MM/yyyy" /></span>
                            </button>
                        </c:forEach>
                    </div>

                    <div class="mt-8 pt-4 border-t border-outline-variant/30 flex justify-end gap-3">
                        <button type="button" onclick="goToStep(1)" class="px-6 py-3 border border-outline-variant rounded-xl font-mono text-xs uppercase text-on-surface-variant hover:bg-slate-50 transition-colors">Quay lại</button>
                        <button type="button" id="btnNext2" onclick="goToStep(3)" disabled class="px-8 py-3 bg-secondary text-on-secondary rounded-xl font-mono text-xs uppercase disabled:opacity-40 disabled:cursor-not-allowed transition-all shadow-sm">Tiếp theo</button>
                    </div>
                </div>

                <!-- ======================== -->
                <!-- STEP 3: Chọn Giờ        -->
                <!-- ======================== -->
                <div id="step3" class="wizard-step flex-grow flex flex-col">
                    <div class="mb-4 flex justify-between items-center border-b border-outline-variant/30 pb-3">
                        <h3 class="text-lg font-bold text-on-surface" id="slotDateLabel">Bước 3: Chọn Khung Giờ</h3>
                    </div>

                    <div id="slotsContainer" class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4 flex-grow mt-4">
                        <div class="col-span-full text-center py-12 text-on-surface-variant">
                            <span class="material-symbols-outlined text-3xl animate-spin text-secondary">progress_activity</span>
                            <p class="mt-2 text-sm">Đang tải khung giờ trống...</p>
                        </div>
                    </div>

                    <div class="mt-8 pt-4 border-t border-outline-variant/30 flex justify-end gap-3">
                        <button type="button" onclick="goToStep(2)" class="px-6 py-3 border border-outline-variant rounded-xl font-mono text-xs uppercase text-on-surface-variant hover:bg-slate-50 transition-colors">Quay lại</button>
                        <button type="button" id="btnNext3" onclick="goToStep(4)" disabled class="px-8 py-3 bg-secondary text-on-secondary rounded-xl font-mono text-xs uppercase disabled:opacity-40 disabled:cursor-not-allowed transition-all shadow-sm">Tiếp theo</button>
                    </div>
                </div>

                <!-- ======================== -->
                <!-- STEP 4: Xác nhận        -->
                <!-- ======================== -->
                <div id="step4" class="wizard-step flex-grow flex flex-col">
                    <div class="mb-4 flex justify-between items-center border-b border-outline-variant/30 pb-3">
                        <h3 class="text-lg font-bold text-on-surface">Bước 4: Xác Nhận Cuộc Hẹn</h3>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 flex-grow mt-4">
                        <!-- Summary info -->
                        <div class="bg-slate-50 border border-outline-variant/20 rounded-2xl p-6 space-y-4">
                            <div class="flex items-center gap-3">
                                <span class="material-symbols-outlined text-secondary text-2xl">directions_car</span>
                                <div>
                                    <div class="font-mono text-[10px] text-on-surface-variant uppercase tracking-wider">Thông Tin Xe</div>
                                    <div class="text-on-surface font-bold text-base" id="confirmPlate">—</div>
                                </div>
                            </div>
                            <div class="flex items-center gap-3">
                                <span class="material-symbols-outlined text-secondary text-2xl">calendar_today</span>
                                <div>
                                    <div class="font-mono text-[10px] text-on-surface-variant uppercase tracking-wider">Ngày Hẹn</div>
                                    <div class="text-on-surface font-bold text-base" id="confirmDate">—</div>
                                </div>
                            </div>
                            <div class="flex items-center gap-3">
                                <span class="material-symbols-outlined text-secondary text-2xl">schedule</span>
                                <div>
                                    <div class="font-mono text-[10px] text-on-surface-variant uppercase tracking-wider">Khung Giờ</div>
                                    <div class="text-on-surface font-bold text-base text-secondary" id="confirmSlot">—</div>
                                </div>
                            </div>
                        </div>

                        <!-- Notes text input -->
                        <div class="flex flex-col gap-2">
                            <label class="font-mono text-xs text-on-surface-variant uppercase tracking-wider ml-1" for="notesInput">
                                Ghi chú thêm (tùy chọn)
                            </label>
                            <textarea id="notesInput" rows="4" placeholder="VD: Hãy rửa kỹ gầm xe, hút bụi khoang nội thất..."
                                      class="w-full flex-grow px-4 py-3 bg-white border border-outline-variant/40 rounded-xl text-on-surface focus:border-secondary focus:ring-1 focus:ring-secondary transition-colors outline-none text-sm"></textarea>
                        </div>
                    </div>

                    <div class="mt-8 pt-4 border-t border-outline-variant/30 flex justify-end gap-3">
                        <button type="button" onclick="goToStep(3)" class="px-6 py-3 border border-outline-variant rounded-xl font-mono text-xs uppercase text-on-surface-variant hover:bg-slate-50 transition-colors">Quay lại</button>
                        <button type="button" onclick="submitBooking()" class="px-8 py-4 bg-secondary text-on-secondary rounded-xl font-mono text-xs uppercase flex items-center justify-center gap-2 hover:bg-on-secondary-container transition-all active:scale-[0.98] shadow-md font-bold">
                            <span class="material-symbols-outlined text-base">check</span> Xác Nhận Đặt Lịch
                        </button>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <%-- Bottom Navigation (Chỉ hiện trên mobile) --%>
    <jsp:include page="/components/bottom-nav.jsp">
        <jsp:param name="activeTab" value="booking" />
    </jsp:include>

    <!-- Booking Wizard JavaScript -->
    <script>
        // === STATE ===
        let currentStep = 1;
        const state = { plateId: null, plateNumber: null, vehicleModel: null, date: null, slot: null };
        const contextPath = '${pageContext.request.contextPath}';

        // === STEP NAVIGATION ===
        function goToStep(step) {
            if (step === 3 && state.date) { loadSlots(state.date); }
            if (step === 4) { populateConfirmation(); }

            document.querySelectorAll('.wizard-step').forEach(el => el.classList.remove('active'));
            document.getElementById('step' + step).classList.add('active');

            // Update progress dots
            for (let i = 1; i <= 4; i++) {
                const dot = document.getElementById('stepDot' + i);
                dot.className = 'w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold transition-colors duration-300 '
                    + (i <= step ? 'bg-secondary text-white' : 'bg-surface-variant text-on-surface-variant');
                if (i < 4) {
                    document.getElementById('stepLine' + i).style.width = (i < step ? '100%' : '0%');
                }
            }
            currentStep = step;
        }

        // === STEP 1: Chọn xe ===
        function selectCar(el) {
            document.querySelectorAll('.car-card').forEach(c => c.classList.remove('selected'));
            el.classList.add('selected');
            state.plateId = el.dataset.plateId;
            state.plateNumber = el.dataset.plateNumber;
            state.vehicleModel = el.dataset.vehicleModel;
            document.getElementById('btnNext1').disabled = false;
        }

        // === STEP 2: Chọn ngày ===
        function selectDate(el) {
            document.querySelectorAll('.date-btn').forEach(b => b.classList.remove('selected'));
            el.classList.add('selected');
            state.date = el.dataset.date;
            document.getElementById('btnNext2').disabled = false;
        }

        // === STEP 3: Load slots via AJAX ===
        function loadSlots(dateStr) {
            document.getElementById('slotDateLabel').textContent = 'Ngày: ' + formatDate(dateStr);
            const container = document.getElementById('slotsContainer');
            container.innerHTML = '<div class="col-span-2 text-center py-8 text-on-surface-variant"><span class="material-symbols-outlined text-3xl animate-spin text-secondary">progress_activity</span><p class="mt-2">Đang tải...</p></div>';

            fetch(contextPath + '/customer/booking?action=slots&date=' + dateStr)
                .then(r => r.json())
                .then(slots => {
                    container.innerHTML = '';
                    for (const [time, info] of Object.entries(slots)) {
                        const btn = document.createElement('button');
                        btn.type = 'button';
                        btn.className = 'slot-btn flex flex-col items-center justify-center p-4 rounded-xl border border-outline-variant/40 transition-all '
                            + (info.available ? 'hover:border-secondary cursor-pointer active:scale-95' : 'disabled opacity-40 cursor-not-allowed');
                        btn.innerHTML = '<span class="font-bold text-base">' + time + '</span>'
                            + '<span class="font-mono text-xs mt-1 ' + (info.available ? 'text-secondary' : 'text-error') + '">'
                            + (info.available ? 'Còn ' + info.remainingSpots + ' chỗ' : 'Hết chỗ') + '</span>';
                        if (info.available) {
                            btn.onclick = () => selectSlot(btn, time);
                        } else {
                            btn.disabled = true;
                        }
                        container.appendChild(btn);
                    }
                })
                .catch(() => {
                    container.innerHTML = '<div class="col-span-2 text-center py-4 text-error">Lỗi tải dữ liệu. Vui lòng thử lại.</div>';
                });
        }

        function selectSlot(el, slot) {
            document.querySelectorAll('.slot-btn').forEach(b => b.classList.remove('selected'));
            el.classList.add('selected');
            state.slot = slot;
            document.getElementById('btnNext3').disabled = false;
        }

        // === STEP 4: Xác nhận ===
        function populateConfirmation() {
            document.getElementById('confirmPlate').textContent = state.plateNumber + (state.vehicleModel ? ' — ' + state.vehicleModel : '');
            document.getElementById('confirmDate').textContent = formatDate(state.date);
            document.getElementById('confirmSlot').textContent = state.slot;
        }

        function submitBooking() {
            document.getElementById('selectedPlateId').value = state.plateId;
            document.getElementById('selectedDate').value = state.date;
            document.getElementById('selectedSlot').value = state.slot;
            document.getElementById('selectedNotes').value = document.getElementById('notesInput').value;
            document.getElementById('bookingForm').submit();
        }

        // === HELPERS ===
        function formatDate(str) {
            const d = new Date(str + 'T00:00:00');
            return d.toLocaleDateString('vi-VN', { weekday: 'long', day: '2-digit', month: '2-digit', year: 'numeric' });
        }
    </script>
</body>
</html>
