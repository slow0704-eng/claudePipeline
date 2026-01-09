package com.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 성공 응답을 위한 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse {

    /**
     * 성공 메시지
     */
    private String message;

    /**
     * 추가 데이터 (선택적)
     */
    private Object data;

    /**
     * 응답 시간
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * 메시지만 포함하는 성공 응답 생성
     */
    public static SuccessResponse of(String message) {
        return SuccessResponse.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 메시지와 데이터를 포함하는 성공 응답 생성
     */
    public static SuccessResponse of(String message, Object data) {
        return SuccessResponse.builder()
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
