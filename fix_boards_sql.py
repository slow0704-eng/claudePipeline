# -*- coding: utf-8 -*-
"""
02_boards.sql 파일을 엄밀하게 수정
- author, nickname, status, comment_count, is_pinned, is_important 필드 추가
"""
import re

# user_id -> (name, nickname) 매핑
user_mapping = {
    2: ('김철수', '코딩마스터'),
    3: ('이영희', '개발왕초보'),
    4: ('박민수', 'JavaLover'),
    5: ('정다은', 'DB전문가'),
    6: ('강호준', '프론트엔드러'),
    7: ('윤서연', '백엔드개발자'),
    8: ('임태희', '풀스택지망생'),
    9: ('오하늘', '알고리즘킬러'),
    10: ('서준호', '데이터분석가'),
    11: ('한지우', 'AI연구원'),
    12: ('권수아', '클라우드엔지니어'),
    13: ('송민재', 'DevOps전문가'),
    14: ('조예린', '보안전문가'),
    15: ('배현우', '모바일개발자'),
    16: ('신채원', 'iOS전문가'),
    17: ('홍시현', 'Android마스터'),
    18: ('류준서', 'React개발자'),
    19: ('문서윤', 'Vue마스터'),
    20: ('진도현', 'Angular전문가'),
    21: ('남주아', 'Node.js개발자'),
    22: ('표지훈', 'Python마스터'),
    23: ('탁은서', 'Django전문가'),
    24: ('변건우', 'Flask개발자'),
    25: ('석예원', 'Go언어러버'),
    26: ('길민준', 'Rust전도사'),
    27: ('노서아', 'C++마스터'),
    28: ('도연우', 'Kotlin개발자'),
    29: ('하시우', 'Swift전문가'),
    30: ('목지안', 'TypeScript러버'),
    31: ('유다온', 'GraphQL전문가'),
    32: ('차하준', 'REST API마스터'),
    33: ('추소율', 'MSA전문가'),
    34: ('피우진', 'Docker마스터'),
    35: ('함수빈', 'K8s전문가'),
    36: ('곽하율', 'AWS전문가'),
    37: ('성시온', 'Azure마스터'),
    38: ('양윤서', 'GCP전문가'),
    39: ('민재원', 'CI/CD마스터'),
    40: ('복하윤', 'Git전문가'),
    41: ('설지호', '코드리뷰어'),
    42: ('옥서준', '테스트마스터'),
    43: ('엄다은', 'QA전문가'),
    44: ('예민성', 'PM지망생'),
    45: ('남궁준', 'UX디자이너'),
    46: ('선우아', 'UI디자이너'),
    47: ('황보율', '기획자'),
    48: ('독고서', '마케터'),
    49: ('사공윤', 'SEO전문가'),
    50: ('제갈호', '데이터사이언티스트'),
}

def split_values(line):
    """
    VALUES 라인을 파싱 (문자열 안의 쉼표 무시)
    """
    parts = []
    current = ''
    in_string = False
    paren_level = 0

    for ch in line:
        if ch == "'" and (not current or current[-1] != '\\'):
            in_string = not in_string
            current += ch
        elif ch == '(' and not in_string:
            paren_level += 1
            current += ch
        elif ch == ')' and not in_string:
            paren_level -= 1
            current += ch
        elif ch == ',' and not in_string and paren_level == 0:
            parts.append(current.strip())
            current = ''
        else:
            current += ch

    if current.strip():
        parts.append(current.strip())

    return parts

def process_value_line(line):
    """
    원본: (user_id, title, content, view_count, like_count, is_draft, created_at, updated_at),
    결과: (user_id, author, nickname, title, content, status, view_count, like_count, comment_count, is_draft, is_pinned, is_important, created_at, updated_at),
    """
    line = line.strip()
    if not line.startswith('('):
        return line

    # 끝부분 처리
    end_char = ''
    if line.endswith(');'):
        line = line[:-2]
        end_char = ');'
    elif line.endswith(','):
        line = line[:-1]
        end_char = ','
    elif line.endswith(';'):
        line = line[:-1]
        end_char = ';'
    elif line.endswith(')'):
        line = line[:-1]
        end_char = ')'

    # 여는 괄호와 닫는 괄호 모두 제거
    content = line[1:-1] if line.startswith('(') and line.endswith(')') else line

    # 값들 분리
    parts = split_values(content)

    if len(parts) < 8:
        return line + end_char

    # 파싱: user_id, title, content, view_count, like_count, is_draft, created_at, updated_at
    user_id_str = parts[0].strip()
    title = parts[1].strip()
    content_text = parts[2].strip()
    view_count = parts[3].strip()
    like_count = parts[4].strip()
    is_draft = parts[5].strip()
    created_at = parts[6].strip()
    updated_at = parts[7].strip()

    try:
        user_id = int(user_id_str)
    except:
        return line + end_char

    # user_mapping에서 조회
    if user_id not in user_mapping:
        print(f"Warning: user_id {user_id} not in mapping, using default")
        author, nickname = f'사용자{user_id}', f'닉네임{user_id}'
    else:
        author, nickname = user_mapping[user_id]

    # 새 라인 생성
    new_parts = [
        user_id_str,
        f"'{author}'",
        f"'{nickname}'",
        title,
        content_text,
        "'PUBLIC'",  # status
        view_count,
        like_count,
        "0",  # comment_count
        is_draft,
        "false",  # is_pinned
        "false",  # is_important
        created_at,
        updated_at
    ]

    new_line = '(' + ', '.join(new_parts) + ')' + end_char
    return new_line

# 파일 읽기
input_file = r'C:\Users\slow0\OneDrive\바탕 화면\251121\251210 게시판\mock-data\02_boards.sql'
output_file = r'C:\Users\slow0\OneDrive\바탕 화면\251121\251210 게시판\mock-data\02_boards_fixed.sql'

with open(input_file, 'r', encoding='utf-8') as f:
    lines = f.readlines()

new_lines = []
for line in lines:
    stripped = line.strip()

    # INSERT 문 수정
    if stripped.startswith('INSERT INTO board'):
        new_line = 'INSERT INTO board (user_id, author, nickname, title, content, status, view_count, like_count, comment_count, is_draft, is_pinned, is_important, created_at, updated_at) VALUES\n'
        new_lines.append(new_line)
    elif stripped.startswith('(') and any(c in stripped for c in [',', ')']):
        # VALUES 라인
        new_line = process_value_line(line)
        new_lines.append(new_line + '\n')
    else:
        new_lines.append(line)

# 파일 쓰기
with open(output_file, 'w', encoding='utf-8') as f:
    f.writelines(new_lines)

print("[OK] 변환 완료: " + output_file)
print("  총 " + str(len(new_lines)) + "줄 처리")
