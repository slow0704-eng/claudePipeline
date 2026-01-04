package com.board.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 현재 로그인한 사용자를 컨트롤러 파라미터에 자동으로 주입하는 어노테이션
 *
 * 사용 예시:
 * <pre>
 * {@code
 * @PostMapping("/board")
 * public String createBoard(@CurrentUser User currentUser, @ModelAttribute Board board) {
 *     // currentUser가 자동으로 주입됨
 * }
 * }
 * </pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
}
