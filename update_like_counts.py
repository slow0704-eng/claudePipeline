# -*- coding: utf-8 -*-
"""
게시글의 like_count 업데이트
"""
import psycopg2

conn = psycopg2.connect(
    host="localhost",
    port=5432,
    database="boarddb",
    user="postgres",
    password="1"
)

cursor = conn.cursor()

print("게시글의 like_count 업데이트 중...")

# board 테이블의 like_count 업데이트
cursor.execute("""
    UPDATE board b
    SET like_count = (
        SELECT COUNT(*)
        FROM likes l
        WHERE l.target_type = 'POST' AND l.target_id = b.id
    )
""")

conn.commit()

# 확인
cursor.execute("SELECT id, title, like_count FROM board WHERE like_count > 0 ORDER BY like_count DESC LIMIT 20")
top_boards = cursor.fetchall()

print("\n좋아요가 많은 게시글 TOP 20:")
print("=" * 70)
for board_id, title, like_count in top_boards:
    title_truncated = title[:50] if title else "제목 없음"
    print(f"[{board_id:3d}] {title_truncated:50s} - {like_count:3d} likes")

cursor.execute("SELECT SUM(like_count) FROM board")
total = cursor.fetchone()[0] or 0
print("=" * 70)
print(f"총 좋아요 수: {total}")

cursor.close()
conn.close()

print("\n완료!")
