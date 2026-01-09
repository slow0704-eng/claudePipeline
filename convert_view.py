#!/usr/bin/env python3
# Script to convert view.html from fragment approach to Layout Dialect approach

import re

def convert_view_html(input_file, output_file):
    with open(input_file, 'r', encoding='utf-8') as f:
        content = f.read()

    # Find the CSS section (lines 6-936)
    css_match = re.search(r'<head th:replace.*?>\s*<title.*?</title>\s*(<style>.*?</style>)', content, re.DOTALL)
    css_content = css_match.group(1) if css_match else ''

    # Find the main content section (from line 950 to 2831)
    # Starting from <main class="main-content"> to the closing tags
    main_match = re.search(r'<main class="main-content">(.*?)</main>', content, re.DOTALL)
    main_content = main_match.group(1) if main_match else ''

    # Extract just the container content (without the container/page-wrapper divs)
    container_match = re.search(r'<div class="container.*?>\s*<div class="page-wrapper">(.*?)</div>\s*<!--.*?\.page-wrapper.*?-->\s*</div>\s*<!--.*?\.container.*?-->', main_content, re.DOTALL)
    inner_content = container_match.group(1) if container_match else main_content

    # Find scripts that are NOT in the fragments section (page-specific scripts after footer-scripts)
    # We need everything from line 2837 onwards (but there are no page-specific scripts in view.html based on grep results)
    # Let's check if there are any script tags after the footer scripts replacement
   scripts_pattern = r'<div th:replace="~\{fragments/footer :: footer-scripts\}"></div>\s*(.*?)</body>'
    scripts_match = re.search(scripts_pattern, content, re.DOTALL)
    page_scripts = scripts_match.group(1).strip() if scripts_match else ''

    # Clean up page_scripts - remove any remaining th:replace calls
    page_scripts = re.sub(r'<div th:replace="~\{fragments/.*?\}"></div>', '', page_scripts)

    # Build the converted template
    output = f'''<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{{layouts/default}}">
<head>
    <title th:text="${{board.title}}">게시글 보기</title>

    <!-- 페이지별 CSS -->
    <th:block layout:fragment="extra-css">
{css_content}

    <!-- QR Code Library -->
    <script src="https://cdn.jsdelivr.net/npm/qrcodejs@1.0.0/qrcode.min.js"></script>

    <!-- Kakao SDK -->
    <script src="https://t1.kakaocdn.net/kakao_js_sdk/2.7.0/kakao.min.js"
            integrity="sha384-l+xbElFSnPZ2rOaPrU//2FF5B4LB8FiX5q4fXYTlfcG4PGpMkE1vcL7kNXI6Cci0"
            crossorigin="anonymous"></script>
    </th:block>
</head>
<body>
    <!-- 컨텐츠만 작성 (header/footer/wrapper 불필요!) -->
    <div layout:fragment="content">
{inner_content}
    </div>

    <!-- 페이지별 스크립트 -->
    <th:block layout:fragment="extra-scripts">
{page_scripts}
    </th:block>
</body>
</html>
'''

    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(output)

    print(f"Conversion complete! Output written to {output_file}")
    print(f"CSS length: {len(css_content)} characters")
    print(f"Content length: {len(inner_content)} characters")
    print(f"Scripts length: {len(page_scripts)} characters")

if __name__ == '__main__':
    input_file = r'C:\Users\slow0\OneDrive\바탕 화면\251121\251210 게시판\src\main\resources\templates\board\view.html'
    output_file = r'C:\Users\slow0\OneDrive\바탕 화면\251121\251210 게시판\src\main\resources\templates\board\view_new.html'
    convert_view_html(input_file, output_file)
