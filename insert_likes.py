# -*- coding: utf-8 -*-
"""
Likes 데이터를 직접 PostgreSQL에 삽입
"""
import psycopg2
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

print("좋아요 데이터 삽입 시작...")

# 테이블 구조 확인
cursor.execute("SELECT column_name, data_type FROM information_schema.columns WHERE table_name = 'likes' ORDER BY ordinal_position")
columns = cursor.fetchall()
print("\n테이블 구조:")
for col in columns:
    print(f"  {col[0]}: {col[1]}")

# 기존 likes 데이터 삭제
cursor.execute("DELETE FROM likes")
print(f"\n기존 데이터 삭제 완료")

# Board 1 - 23 likes
board1_likes = [
    (1, 2, 29), (1, 3, 28), (1, 4, 27), (1, 5, 26), (1, 6, 25),
    (1, 7, 24), (1, 8, 23), (1, 9, 22), (1, 10, 21), (1, 11, 20),
    (1, 12, 19), (1, 13, 18), (1, 14, 17), (1, 15, 16), (1, 16, 15),
    (1, 17, 14), (1, 18, 13), (1, 19, 12), (1, 20, 11), (1, 21, 10),
    (1, 22, 9), (1, 23, 8), (1, 24, 7)
]

inserted = 0
for board_id, user_id, days_ago in board1_likes:
    created_at = datetime.now() - timedelta(days=days_ago)
    cursor.execute(
        "INSERT INTO likes (target_type, target_id, user_id, created_at) VALUES (%s, %s, %s, %s)",
        ('POST', board_id, user_id, created_at)
    )
    inserted += 1

print(f"Board 1: {len(board1_likes)}개 삽입 완료")

# Board 2 - 34 likes
board2_likes = [
    (2, 2, 27), (2, 3, 26), (2, 4, 25), (2, 5, 24), (2, 6, 23),
    (2, 7, 22), (2, 8, 21), (2, 9, 20), (2, 10, 19), (2, 11, 18),
    (2, 12, 17), (2, 13, 16), (2, 14, 15), (2, 15, 14), (2, 16, 13),
    (2, 17, 12), (2, 18, 11), (2, 19, 10), (2, 20, 9), (2, 21, 8),
    (2, 22, 7), (2, 23, 6), (2, 24, 5), (2, 25, 4), (2, 26, 3),
    (2, 27, 2), (2, 28, 1), (2, 29, 0.5), (2, 30, 0.4), (2, 31, 0.3),
    (2, 32, 0.2), (2, 33, 0.15), (2, 34, 0.1), (2, 35, 0.08)
]

for board_id, user_id, days_ago in board2_likes:
    created_at = datetime.now() - timedelta(days=days_ago)
    cursor.execute(
        "INSERT INTO likes (target_type, target_id, user_id, created_at) VALUES (%s, %s, %s, %s)",
        ('POST', board_id, user_id, created_at)
    )
    inserted += 1

print(f"Board 2: {len(board2_likes)}개 삽입 완료")

# 커밋
conn.commit()

# 확인
cursor.execute("SELECT COUNT(*) FROM likes")
count = cursor.fetchone()[0]
print(f"\n총 {count}개의 좋아요 데이터 삽입 완료")

cursor.close()
conn.close()
print("완료!")
