package com.board.exception;

/**
 * 유효성 검증 실패 시 발생하는 예외
 */
public class ValidationException extends BusinessException {

    public ValidationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 기본 Validation 예외 생성
     */
    public static ValidationException of(String fieldName, String message) {
        return new ValidationException(
                ErrorCode.INVALID_INPUT_VALUE,
                String.format("%s: %s", fieldName, message)
        );
    }
}
