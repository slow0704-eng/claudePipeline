package com.board.exception;

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 기본 리소스 Not Found 예외 생성
     */
    public static ResourceNotFoundException of(String resourceName, Long id) {
        return new ResourceNotFoundException(
                ErrorCode.INTERNAL_SERVER_ERROR,
                String.format("%s를 찾을 수 없습니다. (ID: %d)", resourceName, id)
        );
    }

    /**
     * 기본 리소스 Not Found 예외 생성 (문자열 ID)
     */
    public static ResourceNotFoundException of(String resourceName, String id) {
        return new ResourceNotFoundException(
                ErrorCode.INTERNAL_SERVER_ERROR,
                String.format("%s를 찾을 수 없습니다. (ID: %s)", resourceName, id)
        );
    }
}
