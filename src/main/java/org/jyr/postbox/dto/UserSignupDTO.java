package org.jyr.postbox.dto;

import lombok.Data;

@Data
public class UserSignupDTO {
    private String email;
    private String password;
    private String passwordCheck;
    private String nickname;
}
