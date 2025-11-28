package org.jyr.postbox.controller;

import lombok.RequiredArgsConstructor;
import org.jyr.postbox.domain.User;
import org.jyr.postbox.dto.UserSignupDTO;
import org.jyr.postbox.dto.LoginRequestDTO;
import org.jyr.postbox.dto.LoginResponseDTO;
import org.jyr.postbox.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupDTO dto) {
        Long userId = userService.signup(dto);
        return ResponseEntity.ok("회원가입 성공! userId = " + userId);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {

        User user = userService.findByEmail(dto.getEmail());

        // 비밀번호 체크는 LoginResponseDTO 안에서 할 예정
        LoginResponseDTO response = new LoginResponseDTO(user);
        return ResponseEntity.ok(response);
    }
}