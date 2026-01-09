# -*- coding: utf-8 -*-
"""
06_comments.sql 파일 수정
- 테이블명: comment -> comments
- 컬럼명: parent_id -> parent_comment_id
- 필드 추가: nickname, is_deleted, like_count
"""

# user_id -> nickname 매핑
user_mapping = {
    1: 'admin',
    2: '코딩마스터',
    3: '개발왕초보',
    4: 'JavaLover',
    5: 'DB전문가',
    6: '프론트엔드러',
    7: '백엔드개발자',
    8: '풀스택지망생',
    9: '알고리즘킬러',
    10: '데이터분석가',
    11: 'AI연구원',
    12: '클라우드엔지니어',
    13: 'DevOps전문가',
    14: '보안전문가',
    15: '모바일개발자',
    16: 'iOS전문가',
    17: 'Android마스터',
    18: 'React개발자',
    19: 'Vue마스터',
    20: 'Angular전문가',
    21: 'Node.js개발자',
    22: 'Python마스터',
    23: 'Django전문가',
    24: 'Flask개발자',
    25: 'Go언어러버',
    26: 'Rust전도사',
    27: 'C++마스터',
    28: 'Kotlin개발자',
    29: 'Swift전문가',
    30: 'TypeScript러버',
    31: 'GraphQL전문가',
    32: 'REST API마스터',
    33: 'MSA전문가',
    34: 'Docker마스터',
    35: 'K8s전문가',
    36: 'AWS전문가',
    37: 'Azure마스터',
    38: 'GCP전문가',
    39: 'CI/CD마스터',
    40: 'Git전문가',
    41: '코드리뷰어',
    42: '테스트마스터',
    43: 'QA전문가',
    44: 'PM지망생',
    45: 'UX디자이너',
    46: 'UI디자이너',
    47: '기획자',
    48: '마케터',
    49: 'SEO전문가',
    50: '데이터사이언티스트',
}

def split_values(line):
    """VALUES 라인 파싱"""
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
    원본: (board_id, user_id, content, parent_id, created_at, updated_at),
    결과: (board_id, user_id, nickname, content, parent_comment_id, is_deleted, like_count, created_at, updated_at),
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

    # 괄호 제거
    content = line[1:] if line.startswith('(') else line

    # 값들 분리
    parts = split_values(content)

    if len(parts) < 6:
        return line + end_char

    # 파싱: board_id, user_id, content, parent_id, created_at, updated_at
    board_id = parts[0].strip()
    user_id_str = parts[1].strip()
    content_text = parts[2].strip()
    parent_id = parts[3].strip()
    created_at = parts[4].strip()
    updated_at = parts[5].strip()

    try:
        user_id = int(user_id_str)
    except:
        return line + end_char

    # nickname 조회
    if user_id not in user_mapping:
        print(f"Warning: user_id {user_id} not in mapping")
        nickname = f'닉네임{user_id}'
    else:
        nickname = user_mapping[user_id]

    # 새 라인 생성
    # (board_id, user_id, nickname, content, parent_comment_id, is_deleted, like_count, created_at, updated_at)
    new_parts = [
        board_id,
        user_id_str,
        f"'{nickname}'",
        content_text,
        parent_id,  # parent_comment_id
        "false",    # is_deleted
        "0",        # like_count
        created_at,
        updated_at
    ]

    new_line = '(' + ', '.join(new_parts) + ')' + end_char
    return new_line

# 파일 읽기
input_file = r'C:\Users\slow0\OneDrive\바탕 화면\251121\251210 게시판\mock-data\06_comments.sql'
output_file = r'C:\Users\slow0\OneDrive\바탕 화면\251121\251210 게시판\mock-data\06_comments_fixed.sql'

with open(input_file, 'r', encoding='utf-8') as f:
    content = f.read()

# 테이블명 변경
content = content.replace('INSERT INTO comment (', 'INSERT INTO comments (')

# 컬럼명 변경: parent_id -> parent_comment_id
content = content.replace('(board_id, user_id, content, parent_id, created_at, updated_at)',
                         '(board_id, user_id, nickname, content, parent_comment_id, is_deleted, like_count, created_at, updated_at)')

# 라인별 처리
lines = content.split('\n')
new_lines = []

for line in lines:
    stripped = line.strip()

    # VALUES 라인 처리
    if stripped.startswith('(') and any(c in stripped for c in [',', ')']):
        new_line = process_value_line(line)
        new_lines.append(new_line)
    else:
        new_lines.append(line)

# 파일 쓰기
with open(output_file, 'w', encoding='utf-8') as f:
    f.write('\n'.join(new_lines))

print("[OK] 변환 완료: " + output_file)
print("  총 " + str(len(new_lines)) + "줄 처리")
