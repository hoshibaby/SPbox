package org.jyr.postbox.dto.user;

import lombok.Data;
import org.jyr.postbox.domain.User;

@Data
public class LoginResponseDTO {

    private Long id;
    private String userId;
    private String email;
    private String nickname;
    private String token;

    public LoginResponseDTO(User user, String token) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.token = token;
    }
}