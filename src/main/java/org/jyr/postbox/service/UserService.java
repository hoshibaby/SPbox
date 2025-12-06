package org.jyr.postbox.service;

import org.jyr.postbox.domain.User;
import org.jyr.postbox.dto.user.LoginRequestDTO;
import org.jyr.postbox.dto.user.LoginResponseDTO;
import org.jyr.postbox.dto.user.UserSignupDTO;

public interface UserService {

    // 회원가입 후 생성된 유저 id 반환
    Long signup(UserSignupDTO dto);

    // 이메일로 유저 검색 (로그인/인증에서 사용)
    User findByEmail(String email);

    User findById(Long id);

    User findByUserId(String userId);

    LoginResponseDTO login(LoginRequestDTO dto);

}