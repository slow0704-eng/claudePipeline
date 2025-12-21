package com.board.exception;

import com.board.util.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * GlobalExceptionHandler 테스트
 */
public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleRuntimeException_런타임예외_400응답() {
        // Given
        RuntimeException exception = new RuntimeException("데이터를 찾을 수 없습니다");

        // When
        ResponseEntity<ApiResponse> response = handler.handleRuntimeException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("데이터를 찾을 수 없습니다");
    }

    @Test
    void handleAccessDeniedException_접근거부예외_403응답() {
        // Given
        AccessDeniedException exception = new AccessDeniedException("권한 없음");

        // When
        ResponseEntity<ApiResponse> response = handler.handleAccessDeniedException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("접근 권한이 없습니다.");
    }

    @Test
    void handleValidationException_검증실패_400응답() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError error1 = new FieldError("user", "username", "사용자명은 필수입니다");
        FieldError error2 = new FieldError("user", "email", "올바른 이메일 형식이 아닙니다");
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(error1, error2));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                null, bindingResult
        );

        // When
        ResponseEntity<ApiResponse> response = handler.handleValidationException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("입력값이 올바르지 않습니다");
        assertThat(response.getBody().getData()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().getData();
        assertThat(errors).hasSize(2);
        assertThat(errors.get("username")).isEqualTo("사용자명은 필수입니다");
        assertThat(errors.get("email")).isEqualTo("올바른 이메일 형식이 아닙니다");
    }

    @Test
    void handleIllegalArgumentException_잘못된인자예외_400응답() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("유효하지 않은 파라미터입니다");

        // When
        ResponseEntity<ApiResponse> response = handler.handleIllegalArgumentException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("유효하지 않은 파라미터입니다");
    }

    @Test
    void handleNullPointerException_널포인터예외_500응답() {
        // Given
        NullPointerException exception = new NullPointerException("객체가 null입니다");

        // When
        ResponseEntity<ApiResponse> response = handler.handleNullPointerException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("객체가 null입니다");
    }

    @Test
    void handleNullPointerException_메시지없는_널포인터예외_기본메시지() {
        // Given
        NullPointerException exception = new NullPointerException();

        // When
        ResponseEntity<ApiResponse> response = handler.handleNullPointerException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("요청을 처리하는 중 오류가 발생했습니다.");
    }

    @Test
    void handleGeneralException_일반예외_500응답() {
        // Given
        Exception exception = new Exception("예상하지 못한 오류");

        // When
        ResponseEntity<ApiResponse> response = handler.handleGeneralException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
}
