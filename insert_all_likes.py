# -*- coding: utf-8 -*-
"""
04_likes.sql 파일을 파싱하여 PostgreSQL에 직접 삽입
"""
import psycopg2
import re
from datetime import datetime, timedelta

# 데이터베이스 연결
conn = psycopg2.connect(
    host="localhost",
    port=5432,
    database="boarddb",
    user="postgres",
    password="1"
)

cursor = conn.cursor()

print("=" * 50)
print("좋아요 데이터 삽입 시작")
print("=" * 50)

# 기존 likes 데이터 삭제
cursor.execute("DELETE FROM likes")
print("기존 데이터 삭제 완료\n")

# SQL 파일 읽기
sql_file = r'C:\Users\slow0\OneDrive\바탕 화면\251121\251210 게시판\mock-data\04_likes.sql'
with open(sql_file, 'r', encoding='utf-8') as f:
    content = f.read()

# VALUES 패턴 찾기: ('POST', board_id, user_id, NOW() - INTERVAL '...')
pattern = r"\('POST',\s*(\d+),\s*(\d+),\s*NOW\(\)\s*-\s*INTERVAL\s+'([^']+)'\)"

matches = re.findall(pattern, content)

print(f"총 {len(matches)}개의 좋아요 항목을 찾았습니다.\n")

# INTERVAL 문자열을 timedelta로 변환
def parse_interval(interval_str):
    """
    '29 days' -> timedelta(days=29)
    '2 hours' -> timedelta(hours=2)
    '30 minutes' -> timedelta(minutes=30)
    """
    parts = interval_str.strip().split()
    if len(parts) != 2:
        return timedelta(0)

    value = float(parts[0])
    unit = parts[1].lower()

    if 'day' in unit:
        return timedelta(days=value)
    elif 'hour' in unit:
        return timedelta(hours=value)
    elif 'minute' in unit:
        return timedelta(minutes=value)
    elif 'second' in unit:
        return timedelta(seconds=value)
    else:
        return timedelta(0)

# 데이터 삽입
inserted_count = 0
board_counts = {}

for board_id_str, user_id_str, interval_str in matches:
    board_id = int(board_id_str)
    user_id = int(user_id_str)

    # INTERVAL 파싱
    time_delta = parse_interval(interval_str)
    created_at = datetime.now() - time_delta

    try:
        cursor.execute(
            "INSERT INTO likes (target_type, target_id, user_id, created_at) VALUES (%s, %s, %s, %s)",
            ('POST', board_id, user_id, created_at)
        )
        inserted_count += 1

        # 게시글별 카운트
        if board_id not in board_counts:
            board_counts[board_id] = 0
        board_counts[board_id] += 1

    except Exception as e:
        print(f"오류 발생 (Board {board_id}, User {user_id}): {e}")
        continue

# 커밋
conn.commit()

print("=" * 50)
print(f"총 {inserted_count}개의 좋아요 데이터 삽입 완료")
print("=" * 50)
print("\n게시글별 좋아요 수:")
for board_id in sorted(board_counts.keys()):
    count = board_counts[board_id]
    print(f"  Board {board_id:3d}: {count:3d} likes")

# 데이터베이스에서 확인
cursor.execute("SELECT COUNT(*) FROM likes")
db_count = cursor.fetchone()[0]
print(f"\n데이터베이스 확인: {db_count}개")

# 게시글의 like_count 업데이트
print("\n게시글의 like_count 업데이트 중...")
cursor.execute("""
    UPDATE boards b
    SET like_count = (
        SELECT COUNT(*)
        FROM likes l
        WHERE l.target_type = 'POST' AND l.target_id = b.id
    )
""")
conn.commit()

cursor.execute("SELECT id, title, like_count FROM boards WHERE like_count > 0 ORDER BY like_count DESC LIMIT 10")
top_boards = cursor.fetchall()

print("\n좋아요가 많은 게시글 TOP 10:")
for board_id, title, like_count in top_boards:
    print(f"  [{board_id:3d}] {title[:40]:40s} - {like_count} likes")

cursor.close()
conn.close()

print("\n완료!")
