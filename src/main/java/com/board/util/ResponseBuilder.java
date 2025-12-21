package com.board.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * API 응답 빌더 유틸리티
 *
 * 일관된 응답 구조를 제공하는 헬퍼 메서드들을 제공합니다.
 * 기존 60+ 곳의 중복된 응답 처리 코드를 통합합니다.
 */
public class ResponseBuilder {

    /**
     * 성공 응답 (메시지 없음, 데이터 없음)
     *
     * @return 200 OK 응답
     */
    public static ResponseEntity<ApiResponse> success() {
        ApiResponse response = new ApiResponse(true, "성공");
        return ResponseEntity.ok(response);
    }

    /**
     * 성공 응답 (데이터 포함)
     *
     * @param data 응답 데이터
     * @return 200 OK 응답
     */
    public static ResponseEntity<ApiResponse> success(Object data) {
        ApiResponse response = new ApiResponse(true, "성공", data);
        return ResponseEntity.ok(response);
    }

    /**
     * 성공 응답 (메시지 및 데이터 포함)
     *
     * @param message 성공 메시지
     * @param data 응답 데이터
     * @return 200 OK 응답
     */
    public static ResponseEntity<ApiResponse> success(String message, Object data) {
        ApiResponse response = new ApiResponse(true, message, data);
        return ResponseEntity.ok(response);
    }

    /**
     * 성공 응답 (메시지만 포함)
     *
     * @param message 성공 메시지
     * @return 200 OK 응답
     */
    public static ResponseEntity<ApiResponse> success(String message) {
        ApiResponse response = new ApiResponse(true, message);
        return ResponseEntity.ok(response);
    }

    /**
     * 에러 응답 (메시지 포함)
     *
     * @param message 에러 메시지
     * @return 400 Bad Request 응답
     */
    public static ResponseEntity<ApiResponse> error(String message) {
        ApiResponse response = new ApiResponse(false, message);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 에러 응답 (예외로부터 메시지 추출)
     *
     * @param e 예외 객체
     * @return 400 Bad Request 응답
     */
    public static ResponseEntity<ApiResponse> error(Exception e) {
        ApiResponse response = new ApiResponse(false, e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 에러 응답 (메시지 및 데이터 포함)
     *
     * @param message 에러 메시지
     * @param data 추가 에러 정보
     * @return 400 Bad Request 응답
     */
    public static ResponseEntity<ApiResponse> error(String message, Object data) {
        ApiResponse response = new ApiResponse(false, message, data);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 인증 실패 응답
     *
     * @param message 인증 실패 메시지
     * @return 401 Unauthorized 응답
     */
    public static ResponseEntity<ApiResponse> unauthorized(String message) {
        ApiResponse response = new ApiResponse(false, message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 권한 부족 응답
     *
     * @param message 권한 부족 메시지
     * @return 403 Forbidden 응답
     */
    public static ResponseEntity<ApiResponse> forbidden(String message) {
        ApiResponse response = new ApiResponse(false, message);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * 리소스 없음 응답
     *
     * @param message 리소스 없음 메시지
     * @return 404 Not Found 응답
     */
    public static ResponseEntity<ApiResponse> notFound(String message) {
        ApiResponse response = new ApiResponse(false, message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 서버 에러 응답
     *
     * @param message 서버 에러 메시지
     * @return 500 Internal Server Error 응답
     */
    public static ResponseEntity<ApiResponse> serverError(String message) {
        ApiResponse response = new ApiResponse(false, message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 커스텀 상태 코드 응답
     *
     * @param status HTTP 상태 코드
     * @param success 성공 여부
     * @param message 응답 메시지
     * @return 지정된 상태 코드의 응답
     */
    public static ResponseEntity<ApiResponse> custom(HttpStatus status, boolean success, String message) {
        ApiResponse response = new ApiResponse(success, message);
        return ResponseEntity.status(status).body(response);
    }

    /**
     * 커스텀 상태 코드 응답 (데이터 포함)
     *
     * @param status HTTP 상태 코드
     * @param success 성공 여부
     * @param message 응답 메시지
     * @param data 응답 데이터
     * @return 지정된 상태 코드의 응답
     */
    public static ResponseEntity<ApiResponse> custom(HttpStatus status, boolean success, String message, Object data) {
        ApiResponse response = new ApiResponse(success, message, data);
        return ResponseEntity.status(status).body(response);
    }
}
