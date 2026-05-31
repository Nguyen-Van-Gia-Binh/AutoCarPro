<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Trang không tìm thấy</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;700&family=JetBrains+Mono:wght@500&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
</head>
<body class="bg-[#f8f9ff] text-[#0b1c30] min-h-screen flex items-center justify-center font-[Hanken_Grotesk]">
    <div class="text-center px-4">
        <span class="material-symbols-outlined text-7xl text-[#76777d] mb-4" style="font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 48;">search_off</span>
        <h1 class="text-6xl font-bold text-[#00687a] mb-2">404</h1>
        <p class="text-xl text-[#45464d] mb-6">Trang bạn tìm kiếm không tồn tại.</p>
        <a href="${pageContext.request.contextPath}/login"
           class="inline-flex items-center gap-2 px-6 py-3 bg-[#00687a] text-white rounded-xl font-[JetBrains_Mono] text-sm uppercase hover:bg-[#006172] transition-colors">
            <span class="material-symbols-outlined text-lg">home</span> Về trang chủ
        </a>
    </div>
</body>
</html>
