package com.board.exception;

import com.board.util.ApiResponse;
import com.board.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 핸들러
 *
 * 애플리케이션 전체에서 발생하는 예외를 중앙에서 처리합니다.
 * Phase 1에서는 안전망으로만 작동하며, 점진적으로 확장할 예정입니다.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * RuntimeException 처리
     *
     * 비즈니스 로직에서 발생하는 일반적인 RuntimeException을 처리합니다.
     * 현재는 대부분의 비즈니스 예외가 RuntimeException으로 던져지고 있습니다.
     *
     * @param e 런타임 예외
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException e) {
        return ResponseBuilder.error(e.getMessage());
    }

    /**
     * AccessDeniedException 처리
     *
     * Spring Security에서 발생하는 접근 권한 예외를 처리합니다.
     *
     * @param e 접근 거부 예외
     * @return 403 Forbidden 응답
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseBuilder.forbidden("접근 권한이 없습니다.");
    }

    /**
     * MethodArgumentNotValidException 처리
     *
     * @Valid 검증 실패 시 발생하는 예외를 처리합니다.
     * Phase 3에서 활성화될 예정입니다.
     *
     * @param e 검증 예외
     * @return 400 Bad Request 응답 (필드별 에러 포함)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseBuilder.error("입력값이 올바르지 않습니다", errors);
    }

    /**
     * IllegalArgumentException 처리
     *
     * 잘못된 인자가 전달되었을 때 발생하는 예외를 처리합니다.
     *
     * @param e 잘못된 인자 예외
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseBuilder.error(e.getMessage());
    }

    /**
     * NullPointerException 처리
     *
     * Null 참조로 인한 예외를 처리합니다.
     * 프로덕션에서는 상세한 에러 메시지 대신 일반적인 메시지를 반환합니다.
     *
     * @param e Null 포인터 예외
     * @return 500 Internal Server Error 응답
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse> handleNullPointerException(NullPointerException e) {
        // 개발 환경에서는 상세 메시지, 프로덕션에서는 일반 메시지
        String message = e.getMessage() != null ? e.getMessage() : "요청을 처리하는 중 오류가 발생했습니다.";
        return ResponseBuilder.serverError(message);
    }

    /**
     * Exception 처리 (최상위 캐치)
     *
     * 위의 핸들러들에서 처리되지 않은 모든 예외를 처리합니다.
     * 예상하지 못한 오류가 발생했을 때의 안전망 역할을 합니다.
     *
     * @param e 예외
     * @return 500 Internal Server Error 응답
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ApiResponse> handleGeneralException(Exception e) {
        // 프로덕션에서는 내부 에러 상세를 숨기고 일반적인 메시지만 반환
        return ResponseBuilder.serverError("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
}
