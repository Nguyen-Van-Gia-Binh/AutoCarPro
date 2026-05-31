<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - AutoWash Pro</title>
    <!-- Tailwind CSS via CDN (theo yêu cầu tài liệu PRJ301) -->
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <!-- Google Fonts: Hanken Grotesk (heading/body) + JetBrains Mono (label) -->
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;600;700&family=JetBrains+Mono:wght@500&display=swap" rel="stylesheet">
    <!-- Material Symbols (Icons) -->
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <%-- Tailwind config: Design tokens trích xuất từ StitchMCP --%>
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        "primary": "#000000",
                        "on-primary": "#ffffff",
                        "primary-container": "#131b2e",
                        "secondary": "#00687a",
                        "on-secondary": "#ffffff",
                        "secondary-container": "#57dffe",
                        "on-secondary-container": "#006172",
                        "surface": "#f8f9ff",
                        "on-surface": "#0b1c30",
                        "on-surface-variant": "#45464d",
                        "surface-container-lowest": "#ffffff",
                        "surface-container-low": "#eff4ff",
                        "surface-variant": "#d3e4fe",
                        "outline": "#76777d",
                        "outline-variant": "#c6c6cd",
                        "background": "#f8f9ff",
                        "on-background": "#0b1c30",
                        "error": "#ba1a1a",
                        "error-container": "#ffdad6",
                    },
                    fontFamily: {
                        "sans": ["Hanken Grotesk", "sans-serif"],
                        "mono": ["JetBrains Mono", "monospace"],
                    },
                }
            }
        }
    </script>
    <style>
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
        }
    </style>
</head>
<body class="flex flex-col md:flex-row h-screen w-full overflow-hidden text-on-surface bg-background font-sans antialiased">

    <!-- Left Column: Branding (Hidden on mobile, 50% on desktop) -->
    <div class="hidden md:flex flex-col w-1/2 bg-[#0F172A] relative overflow-hidden justify-center items-center text-white p-8">
        <!-- Abstract gradient mesh -->
        <div class="absolute inset-0 bg-[radial-gradient(circle_at_bottom_right,rgba(0,104,122,0.4)_0%,rgba(15,23,42,0)_50%)] pointer-events-none"></div>
        <div class="z-10 text-center max-w-md mx-auto">
            <h1 class="text-5xl font-bold tracking-tight text-white mb-4">AutoWash Pro</h1>
            <p class="text-lg text-slate-300 font-light leading-relaxed">
                Hệ thống quản lý dịch vụ rửa xe tự động và thông minh. Trải nghiệm nhanh chóng, tích lũy điểm thưởng và nâng hạng thẻ dễ dàng.
            </p>
        </div>
        <!-- Minimalist decorative circles -->
        <div class="absolute top-1/4 left-1/4 w-32 h-32 border border-slate-500/20 rounded-full opacity-50"></div>
        <div class="absolute bottom-1/4 right-1/4 w-64 h-64 border border-secondary/30 rounded-full opacity-30"></div>
    </div>

    <!-- Right Column: Authentication Form -->
    <div class="w-full md:w-1/2 bg-surface flex items-center justify-center p-4 md:p-12 overflow-y-auto">
        <div class="w-full max-w-[440px] bg-white border border-outline-variant/30 shadow-sm rounded-2xl p-6 md:p-8 flex flex-col gap-6">
            <!-- Header -->
            <div class="text-center md:text-left">
                <h2 class="text-3xl font-bold text-on-surface mb-2">Đăng Nhập</h2>
                <p class="text-sm text-on-surface-variant">Nhập thông tin đăng nhập của bạn để tiếp tục.</p>
            </div>

            <%-- Thông báo đăng ký thành công --%>
            <c:if test="${param.registered == 'true'}">
                <div class="w-full p-3 bg-green-50 border border-green-200 rounded-xl text-green-800 text-sm text-center">
                    <span class="material-symbols-outlined text-sm align-middle">check_circle</span>
                    Đăng ký thành công! Hãy đăng nhập.
                </div>
            </c:if>

            <%-- Thông báo lỗi --%>
            <c:if test="${not empty error}">
                <div class="w-full p-3 bg-error-container border border-error/20 rounded-xl text-error text-sm text-center">
                    <span class="material-symbols-outlined text-sm align-middle">error</span>
                    ${error}
                </div>
            </c:if>

            <!-- Login Form -->
            <form action="${pageContext.request.contextPath}/login" method="POST" class="flex flex-col gap-4">
                <!-- Phone Number -->
                <div class="flex flex-col gap-1.5">
                    <label class="font-mono text-xs text-on-surface-variant uppercase tracking-wider ml-1" for="phone">
                        SỐ ĐIỆN THOẠI
                    </label>
                    <div class="relative flex items-center">
                        <span class="material-symbols-outlined absolute left-3.5 text-outline pointer-events-none text-lg">call</span>
                        <input type="tel" id="phone" name="phone"
                               value="${phone}"
                               placeholder="0912345678"
                               required
                               class="w-full pl-11 pr-4 py-3 bg-slate-50 border-transparent focus:border-secondary focus:bg-white focus:ring-1 focus:ring-secondary rounded-xl text-base text-on-surface placeholder:text-outline/40 transition-colors outline-none">
                    </div>
                </div>

                <!-- Password -->
                <div class="flex flex-col gap-1.5">
                    <div class="flex justify-between items-center ml-1">
                        <label class="font-mono text-xs text-on-surface-variant uppercase tracking-wider" for="password">
                            MẬT KHẨU
                        </label>
                    </div>
                    <div class="relative flex items-center">
                        <span class="material-symbols-outlined absolute left-3.5 text-outline pointer-events-none text-lg">lock</span>
                        <input type="password" id="password" name="password"
                               placeholder="••••••••"
                               required
                               class="w-full pl-11 pr-10 py-3 bg-slate-50 border-transparent focus:border-secondary focus:bg-white focus:ring-1 focus:ring-secondary rounded-xl text-base text-on-surface placeholder:text-outline/40 transition-colors outline-none">
                        <button type="button" aria-label="Hiện/ẩn mật khẩu" id="togglePassword"
                                class="absolute right-3.5 text-outline hover:text-on-surface transition-colors">
                            <span class="material-symbols-outlined text-lg" id="visibility-icon">visibility</span>
                        </button>
                    </div>
                </div>

                <!-- Submit Button -->
                <div class="pt-2">
                    <button type="submit"
                            class="w-full bg-secondary text-on-secondary font-mono text-sm uppercase tracking-wider py-4 rounded-xl hover:bg-on-secondary-container active:scale-[0.98] transition-all duration-200 flex items-center justify-center gap-2 shadow-sm">
                        ĐĂNG NHẬP
                        <span class="material-symbols-outlined text-lg" style="font-variation-settings: 'FILL' 1;">login</span>
                    </button>
                </div>
            </form>

            <!-- Register Link -->
            <div class="text-center pt-4 border-t border-slate-100">
                <p class="text-sm text-on-surface-variant">
                    Chưa có tài khoản?
                    <a href="${pageContext.request.contextPath}/register"
                       class="text-secondary font-semibold hover:text-on-secondary-container transition-colors ml-1">Đăng ký ngay</a>
                </p>
            </div>
        </div>
    </div>

    <!-- Password Toggle Script -->
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            const toggleBtn = document.getElementById('togglePassword');
            const passwordInput = document.getElementById('password');
            const icon = document.getElementById('visibility-icon');

            if (toggleBtn && passwordInput && icon) {
                toggleBtn.addEventListener('click', () => {
                    const type = passwordInput.type === 'password' ? 'text' : 'password';
                    passwordInput.type = type;
                    icon.textContent = type === 'password' ? 'visibility' : 'visibility_off';
                });
            }
        });
    </script>
</body>

</html>
