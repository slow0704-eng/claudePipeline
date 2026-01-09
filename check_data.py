# -*- coding: utf-8 -*-
"""
데이터베이스 현황 확인
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

print("=" * 70)
print("데이터베이스 현황")
print("=" * 70)

# 사용자 확인
cursor.execute("SELECT COUNT(*) FROM users")
user_count = cursor.fetchone()[0]
print(f"\n사용자: {user_count}명")

cursor.execute("SELECT MIN(id), MAX(id) FROM users")
min_id, max_id = cursor.fetchone()
print(f"  User ID 범위: {min_id} ~ {max_id}")

# User 43 확인
cursor.execute("SELECT id, username, nickname FROM users WHERE id = 43")
user_43 = cursor.fetchone()
if user_43:
    print(f"  User 43: {user_43[1]} ({user_43[2]})")
else:
    print(f"  User 43: ❌ 존재하지 않음")

# 게시글 확인
cursor.execute("SELECT COUNT(*) FROM board")
board_count = cursor.fetchone()[0]
print(f"\n게시글: {board_count}개")

# 좋아요 확인
cursor.execute("SELECT COUNT(*) FROM likes")
like_count = cursor.fetchone()[0]
print(f"\n좋아요: {like_count}개")

# 좋아요가 많은 게시글
cursor.execute("SELECT id, title, like_count FROM board WHERE like_count > 0 ORDER BY like_count DESC LIMIT 5")
top_boards = cursor.fetchall()
print(f"\n좋아요 TOP 5:")
for board_id, title, likes in top_boards:
    title_truncated = title[:40] if title else "제목 없음"
    print(f"  [{board_id:3d}] {title_truncated:40s} - {likes:3d} likes")

# 팔로우 확인
try:
    cursor.execute("SELECT COUNT(*) FROM follow")
    follow_count = cursor.fetchone()[0]
    print(f"\n팔로우: {follow_count}개")
except:
    print(f"\n팔로우: 테이블 없음 또는 데이터 없음")

# 북마크 확인
try:
    cursor.execute("SELECT COUNT(*) FROM bookmark")
    bookmark_count = cursor.fetchone()[0]
    print(f"\n북마크: {bookmark_count}개")
except:
    print(f"\n북마크: 테이블 없음 또는 데이터 없음")

# 댓글 확인
try:
    cursor.execute("SELECT COUNT(*) FROM comment")
    comment_count = cursor.fetchone()[0]
    print(f"\n댓글: {comment_count}개")
except:
    print(f"\n댓글: 테이블 없음 또는 데이터 없음")

print("\n" + "=" * 70)

cursor.close()
conn.close()
