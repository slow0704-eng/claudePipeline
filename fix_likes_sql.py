# -*- coding: utf-8 -*-
"""
04_likes.sql 파일 수정
- 테이블명: board_like -> likes
- 컬럼: (board_id, user_id, created_at) -> (target_type, target_id, user_id, created_at)
- 모든 값에 'POST' 추가
"""

input_file = r'C:\Users\slow0\OneDrive\바탕 화면\251121\251210 게시판\mock-data\04_likes.sql'
output_file = r'C:\Users\slow0\OneDrive\바탕 화면\251121\251210 게시판\mock-data\04_likes_fixed.sql'

with open(input_file, 'r', encoding='utf-8') as f:
    content = f.read()

# (1, 2, NOW()) -> ('POST', 1, 2, NOW())
# (board_id, user_id, created_at) -> (target_type, target_id, user_id, created_at)

import re

def replace_values(match):
    """(숫자, 숫자, NOW...) -> ('POST', 숫자, 숫자, NOW...)"""
    full_match = match.group(0)

    # (1, 2, NOW() - INTERVAL '...')
    # 첫 번째 숫자 추출
    parts = full_match[1:-1].split(',', 2)  # ( 제거하고 분리

    if len(parts) >= 3:
        board_id = parts[0].strip()
        user_id = parts[1].strip()
        rest = parts[2].strip()

        # ('POST', board_id, user_id, rest)
        return f"('POST', {board_id}, {user_id}, {rest})"

    return full_match

# 정규식으로 (숫자, 숫자, NOW...) 패턴 찾기
# \(\d+,\s*\d+,\s*NOW\(\)[^)]*\)
pattern = r'\(\d+,\s*\d+,\s*NOW\(\)[^)]*\)'

content = re.sub(pattern, replace_values, content)

with open(output_file, 'w', encoding='utf-8') as f:
    f.write(content)

print("[OK] 변환 완료: " + output_file)
print("  target_type = 'POST' 추가됨")
