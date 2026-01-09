package com.board.dto.request;

import com.board.entity.User;
import com.board.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 회원가입 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 3, max = 50, message = "아이디는 3자 이상 50자 이하여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(max = 50, message = "닉네임은 50자 이하여야 합니다.")
    private String nickname;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
    private String email;

    @Size(max = 50, message = "이름은 50자 이하여야 합니다.")
    private String name;

    /**
     * Request DTO -> Entity 변환 (비밀번호는 암호화된 상태로 전달받음)
     */
    public User toEntity(String encodedPassword) {
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(encodedPassword);
        user.setNickname(this.nickname);
        user.setEmail(this.email);
        user.setName(this.name);
        user.setRole(UserRole.MEMBER);
        user.setEnabled(true);
        return user;
    }
}
