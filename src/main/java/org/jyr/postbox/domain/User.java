package org.jyr.postbox.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이메일(로그인 ID)
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 30)
    private String userId;

    // 암호화된 비밀번호
    @Column(nullable = false, length = 200)
    private String password;

    // 화면에 보여줄 이름 (닉네임)
    @Column(nullable = false, length = 50)
    private String nickname;

    // 프로필 이미지 URL (Firestore 연동용, nullable 허용)
    @Column(length = 300)
    private String profileImageUrl;

    // USER, ADMIN ...
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.role == null) {
            this.role = UserRole.USER;
        }
    }
}

