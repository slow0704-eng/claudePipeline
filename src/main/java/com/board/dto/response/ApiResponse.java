package com.board.dto.response;

/**
 * 표준화된 API 응답 DTO
 *
 * 모든 REST API 응답에 일관된 구조를 제공합니다.
 * 기존 Map 기반 응답과 완전히 호환되는 JSON 구조를 생성합니다.
 */
public class ApiResponse<T> {

    /**
     * 요청 성공 여부
     */
    private boolean success;

    /**
     * 응답 메시지 (성공/실패 사유 등)
     */
    private String message;

    /**
     * 응답 데이터 (선택적)
     */
    private T data;

    /**
     * 기본 생성자
     */
    public ApiResponse() {
    }

    /**
     * 성공 여부와 메시지를 포함한 응답 생성
     *
     * @param success 성공 여부
     * @param message 응답 메시지
     */
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * 성공 여부, 메시지, 데이터를 포함한 응답 생성
     *
     * @param success 성공 여부
     * @param message 응답 메시지
     * @param data 응답 데이터
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // Static helper methods

    /**
     * 성공 응답 생성 (데이터 없음)
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message);
    }

    /**
     * 성공 응답 생성 (데이터 포함)
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * 실패 응답 생성
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message);
    }

    /**
     * 실패 응답 생성 (에러 코드 포함)
     */
    public static <T> ApiResponse<T> error(int errorCode, String message) {
        return new ApiResponse<>(false, message);
    }

    /**
     * 실패 응답 생성 (데이터 포함)
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }
}
