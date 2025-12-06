package org.jyr.postbox.service;

import lombok.RequiredArgsConstructor;
import org.jyr.postbox.domain.User;
import org.jyr.postbox.domain.UserRole;
import org.jyr.postbox.dto.user.LoginRequestDTO;
import org.jyr.postbox.dto.user.LoginResponseDTO;
import org.jyr.postbox.dto.user.UserSignupDTO;
import org.jyr.postbox.repository.UserRepository;
import org.jyr.postbox.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final BoxService boxService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Long signup(UserSignupDTO dto) {

        // 1) 이메일 중복 체크
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 1-1) userId 중복 체크
        if (userRepository.existsByUserId(dto.getUserId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 2) 비밀번호 확인
        if (!dto.getPassword().equals(dto.getPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3) 비밀번호 암호화
        String encodedPw = passwordEncoder.encode(dto.getPassword());

        // 4) User 엔티티 생성
        User user = User.builder()
                .userId(dto.getUserId())
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

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }

    @Override
    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 userId 입니다."));
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {

        // 1) userId로 사용자 찾기
        User user = userRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        // 2) 비밀번호 검증
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3) JWT 토큰 생성 (subject = userId)
        String token = jwtTokenProvider.createToken(user.getUserId());

        // 4) LoginResponseDTO 만들어서 반환
        return new LoginResponseDTO(user, token);
    }


}