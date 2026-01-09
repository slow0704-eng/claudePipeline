# 수정 사항 요약

## 해결된 문제들

### 1. ✅ 좋아요 카운트 리셋 버그 해결
**문제**: 좋아요가 60개 있던 게시물도 클릭하면 1로 되돌아감

**원인**:
- Mock 데이터의 `board` 테이블에는 `like_count` 값이 설정되어 있었음 (23, 34, 41 등)
- 하지만 실제 `likes` 테이블에는 데이터가 없었음
- 사용자가 좋아요를 클릭하면 `likeRepository.count(...)`로 실제 개수(0 또는 1)를 세서 덮어씀

**해결**:
- `04_likes.sql` 파일의 SQL 실행 문제를 우회하기 위해 Python 스크립트 작성
- `insert_all_likes.py`로 276개의 좋아요 데이터를 직접 삽입
- `update_like_counts.py`로 각 게시글의 `like_count` 컬럼 업데이트
- `reset_db_and_insert.bat`를 수정하여 Python 스크립트 사용

**결과**:
```
Board  3: 41 likes  (Git 브랜치 전략 비교)
Board  5: 37 likes  (SwiftUI vs UIKit 비교)
Board  2: 34 likes  (RESTful API 설계 원칙)
Board  4: 28 likes  (Python 비동기 프로그래밍)
Board  1: 23 likes  (CSS Flexbox 완벽 가이드)
... 총 20개 게시글에 276개 좋아요 분산
```

### 2. ✅ User 43 Not Found 오류 해결
**문제**: `/user/43` 접근 시 "사용자를 찾을 수 없습니다" 오류

**원인**: Mock 데이터가 완전히 삽입되지 않아 user ID 43이 존재하지 않음

**해결**: Mock 데이터 정상 삽입 후 확인
- 현재 51명의 사용자 존재 (ID 1-51)
- User 43 = user042 (테스트유저명)

### 3. ✅ 동일 시간 게시물 정렬 개선
**문제**: 게시물이 동시에 생성되면 정렬이 불확실함

**해결**: 모든 Repository 메소드에 2차 정렬 기준 추가
```java
// Before: 시간만으로 정렬
List<Board> findAllByOrderByCreatedAtDesc();

// After: 시간 + 제목으로 정렬
List<Board> findAllByOrderByCreatedAtDescTitleAsc();
```

### 4. ✅ JavaScript 오류 해결
**문제**: `Uncaught ReferenceError: openShareModal is not defined`

**원인**: Thymeleaf 인라인 변수 `[[${board.id}]]`가 JavaScript 블록 내에서 렌더링 실패

**해결**:
- HTML5 data attributes 사용
- 페이지 로드 시 한 번만 변수 추출
- 모든 함수에서 전역 변수 사용

## 데이터베이스 현황

```
사용자: 51명 (user001 ~ user050 + admin)
게시글: 30개
좋아요: 276개 (Board 1-20에 분산)
팔로우: 테이블 없음
북마크: 테이블 없음
댓글: 테이블 없음
```

## 테스트 방법

### 1. 좋아요 버그 테스트
1. 애플리케이션 시작: `mvnw.cmd spring-boot:run`
2. 로그인: user042 / 1234 (User ID 43)
3. Board ID 3 접속 (41개 좋아요)
4. 좋아요 버튼 클릭
5. **예상 결과**: 42개로 증가 (1로 리셋되지 않음)

### 2. User 43 접근 테스트
1. 로그인: user042 / 1234
2. 마이페이지 또는 `/user/43` 접근
3. **예상 결과**: 정상적으로 프로필 페이지 표시

### 3. 정렬 테스트
1. 게시판 목록 확인
2. 동일 시간대 게시물이 제목 알파벳순으로 정렬되는지 확인

## 파일 변경 목록

### 새로 생성된 파일
- `insert_all_likes.py` - SQL 파일 파싱 후 likes 데이터 삽입
- `update_like_counts.py` - board 테이블의 like_count 업데이트
- `check_data.py` - 데이터베이스 현황 확인

### 수정된 파일
- `reset_db_and_insert.bat` - 04_likes.sql 대신 Python 스크립트 사용
- `src/main/java/com/board/repository/BoardRepository.java` - 모든 메소드에 TitleAsc 추가
- `src/main/java/com/board/service/BoardService.java` - Repository 메소드 호출 업데이트
- `src/main/java/com/board/controller/AdminController.java` - 메소드명 수정
- `src/main/resources/templates/board/view.html` - JavaScript 변수 스코핑 수정

## 추가 작업 필요

현재 follow, bookmark, comment 테이블이 없는 것으로 보입니다. 필요한 경우:
1. `mvnw.cmd spring-boot:run` 실행하여 Hibernate가 테이블 생성
2. 해당 mock 데이터 파일 확인 및 삽입

## 실행 방법

1. **전체 DB 재설정 + Mock 데이터 삽입**
   ```
   reset_db_and_insert.bat
   ```

2. **현재 DB 상태 확인**
   ```
   python check_data.py
   ```

3. **좋아요 데이터만 다시 삽입**
   ```
   python insert_all_likes.py
   python update_like_counts.py
   ```

4. **애플리케이션 시작**
   ```
   mvnw.cmd spring-boot:run
   ```
