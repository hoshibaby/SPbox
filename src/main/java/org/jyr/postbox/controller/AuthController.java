package org.jyr.postbox.controller;

import lombok.RequiredArgsConstructor;
import org.jyr.postbox.dto.user.UserSignupDTO;
import org.jyr.postbox.dto.user.LoginRequestDTO;
import org.jyr.postbox.dto.user.LoginResponseDTO;
import org.jyr.postbox.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final UserService userService;

    // 회원가입 /api/auth/signuo
    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@RequestBody @Valid UserSignupDTO dto) {

        Long userId = userService.signup(dto);

        // 201 + 생성된 userId 반환 (프론트에서 필요 없으면 body 빼도 됨)
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userId);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        LoginResponseDTO response = userService.login(dto);
        // 여기서 response 안에 accessToken / refreshToken / user 정보 등을 담아두면 됨
        return ResponseEntity.ok(response);
    }


}