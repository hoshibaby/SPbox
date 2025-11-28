package org.jyr.postbox.dto;

import lombok.Data;
import org.jyr.postbox.domain.User;

@Data
public class LoginResponseDTO {

    private Long id;
    private String email;
    private String nickname;

    public LoginResponseDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
    }
}