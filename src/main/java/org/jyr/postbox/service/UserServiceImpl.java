package org.jyr.postbox.service;

import lombok.RequiredArgsConstructor;
import org.jyr.postbox.domain.User;
import org.jyr.postbox.domain.UserRole;
import org.jyr.postbox.dto.UserSignupDTO;
import org.jyr.postbox.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final BoxService boxService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Long signup(UserSignupDTO dto) {

        // 1) 이메일 중복 체크
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 2) 비밀번호 확인
        if (!dto.getPassword().equals(dto.getPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3) 비밀번호 암호화
        String encodedPw = passwordEncoder.encode(dto.getPassword());

        // 4) User 엔티티 생성
        User user = User.builder()
                .email(dto.getEmail())
                .password(encodedPw)
                .nickname(dto.getNickname())
                .role(UserRole.USER)
                .build();

        User saved = userRepository.save(user);

        // 5) 가입과 동시에 Box 자동 생성
        boxService.createBoxForUser(saved);

        return saved.getId();
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
    }
}