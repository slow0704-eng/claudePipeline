# -*- coding: utf-8 -*-
"""
04_likes.sql 파일 수정 v2
- 모든 (숫자, 숫자, NOW...) 패턴을 ('POST', 숫자, 숫자, NOW...)로 변환
"""
import re

input_file = r'C:\Users\slow0\OneDrive\바탕 화면\251121\251210 게시판\mock-data\04_likes.sql'
output_file = r'C:\Users\slow0\OneDrive\바탕 화면\251121\251210 게시판\mock-data\04_likes_v2.sql'

with open(input_file, 'r', encoding='utf-8') as f:
    content = f.read()

# 패턴: (숫자, 숫자, NOW() - INTERVAL ...)
# 변환: ('POST', 숫자, 숫자, NOW() - INTERVAL ...)

def replace_func(match):
    """(1, 2, NOW()...) -> ('POST', 1, 2, NOW()...)"""
    full = match.group(0)
    # ( 제거
    inner = full[1:-1]  # 괄호 제거

    # 첫 번째 쉼표로 분리
    parts = inner.split(',', 1)
    if len(parts) == 2:
        first_num = parts[0].strip()
        rest = parts[1].strip()

        return f"('POST', {first_num}, {rest})"

    return full

# 정규식: (숫자, 공백포함한 나머지)
# \(\d+\s*,\s*.*?\)(?=[,;])
pattern = r'\(\d+\s*,\s*[^)]+\)'

content_new = re.sub(pattern, replace_func, content)

with open(output_file, 'w', encoding='utf-8') as f:
    f.write(content_new)

print("[OK] 변환 완료")
print("  입력: " + input_file)
print("  출력: " + output_file)

# 샘플 확인
lines = content_new.split('\n')
print("\n변환 샘플 (6-10줄):")
for i, line in enumerate(lines[5:10], start=6):
    print(f"  {i}: {line}")
