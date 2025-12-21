package com.board.util;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ResponseBuilder 테스트
 */
public class ResponseBuilderTest {

    @Test
    void success_메시지없이_성공응답_생성() {
        // When
        ResponseEntity<ApiResponse> response = ResponseBuilder.success();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("성공");
    }

    @Test
    void success_데이터포함_성공응답_생성() {
        // Given
        Map<String, Object> data = new HashMap<>();
        data.put("id", 123L);
        data.put("name", "테스트");

        // When
        ResponseEntity<ApiResponse> response = ResponseBuilder.success(data);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("성공");
        assertThat(response.getBody().getData()).isEqualTo(data);
    }

    @Test
    void success_메시지와_데이터포함_성공응답_생성() {
        // Given
        String message = "게시글이 생성되었습니다";
        Map<String, Object> data = new HashMap<>();
        data.put("boardId", 456L);

        // When
        ResponseEntity<ApiResponse> response = ResponseBuilder.success(message, data);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo(message);
        assertThat(response.getBody().getData()).isEqualTo(data);
    }

    @Test
    void success_메시지만포함_성공응답_생성() {
        // Given
        String message = "작업이 완료되었습니다";

        // When
        ResponseEntity<ApiResponse> response = ResponseBuilder.success(message);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo(message);
    }

    @Test
    void error_메시지로_에러응답_생성() {
        // Given
        String errorMessage = "잘못된 요청입니다";

        // When
        ResponseEntity<ApiResponse> response = ResponseBuilder.error(errorMessage);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo(errorMessage);
    }

    @Test
    void error_예외로_에러응답_생성() {
        // Given
        Exception exception = new RuntimeException("데이터베이스 오류");

        // When
        ResponseEntity<ApiResponse> response = ResponseBuilder.error(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("데이터베이스 오류");
    }

    @Test
    void error_메시지와_데이터포함_에러응답_생성() {
        // Given
        String errorMessage = "입력값이 올바르지 않습니다";
        Map<String, String> errors = new HashMap<>();
        errors.put("username", "사용자명은 필수입니다");
        errors.put("email", "올바른 이메일 형식이 아닙니다");

        // When
        ResponseEntity<ApiResponse> response = ResponseBuilder.error(errorMessage, errors);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo(errorMessage);
        assertThat(response.getBody().getData()).isEqualTo(errors);
    }

    @Test
    void unauthorized_인증실패응답_생성() {
        // Given
        String message = "로그인이 필요합니다";

        // When
        ResponseEntity<ApiResponse> response = ResponseBuilder.unauthorized(message);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo(message);
    }

    @Test
    void forbidden_권한부족응답_생성() {
        // Given
        String message = "접근 권한이 없습니다";

        // When
        ResponseEntity<ApiResponse> response = ResponseBuilder.forbidden(message);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo(message);
    }

    @Test
    void notFound_리소스없음응답_생성() {
        // Given
        String message = "게시글을 찾을 수 없습니다";

        // When
        ResponseEntity<ApiResponse> response = ResponseBuilder.notFound(message);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo(message);
    }

    @Test
    void serverError_서버에러응답_생성() {
        // Given
        String message = "서버 오류가 발생했습니다";

        // When
        ResponseEntity<ApiResponse> response = ResponseBuilder.serverError(message);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo(message);
    }

    @Test
    void custom_커스텀상태코드_응답생성() {
        // Given
        HttpStatus status = HttpStatus.CREATED;
        String message = "리소스가 생성되었습니다";

        // When
        ResponseEntity<ApiResponse> response = ResponseBuilder.custom(status, true, message);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo(message);
    }

    @Test
    void custom_커스텀상태코드_데이터포함_응답생성() {
        // Given
        HttpStatus status = HttpStatus.CREATED;
        String message = "사용자가 생성되었습니다";
        Map<String, Object> data = new HashMap<>();
        data.put("userId", 789L);

        // When
        ResponseEntity<ApiResponse> response = ResponseBuilder.custom(status, true, message, data);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo(message);
        assertThat(response.getBody().getData()).isEqualTo(data);
    }
}
