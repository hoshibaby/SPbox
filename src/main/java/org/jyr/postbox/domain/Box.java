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
public class Box {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 박스 주인
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    // 링크에 사용할 고유 키
    @Column(nullable = false, unique = true, length = 50)
    private String urlKey;

    // 박스 제목
    @Column(nullable = false, length = 100)
    private String title;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.title == null || this.title.isBlank()) {
            this.title = "익명 메시지함";
        }
    }
}