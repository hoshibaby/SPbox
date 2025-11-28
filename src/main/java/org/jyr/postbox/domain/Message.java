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
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어느 박스에 달린 메시지인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "box_id", nullable = false)
    private Box box;

    // 익명 닉네임 (익명 방문자 전용)
    @Column(length = 30)
    private String nickname;

    // 본문
    @Column(nullable = false, length = 1000)
    private String content;

    // 작성 시간
    private LocalDateTime createdAt;

    // 숨김 여부 (박스 주인이 가릴 때)
    @Column(nullable = false)
    private boolean hidden;

    // 박스 주인의 답변 (있을 수도, 없을 수도)
    @Column(length = 1000)
    private String replyContent;

    private LocalDateTime replyCreatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthorType authorType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="author_user_id")
    private User authorUser;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.hidden = false;
        // 기본값: 익명 작성자
        if (this.authorType == null) {
            this.authorType = AuthorType.ANONYMOUS;
        }
    }

    // 답변 달 때 사용할 헬퍼 메서드
    public void writeReply(String replyContent) {
        this.replyContent = replyContent;
        this.replyCreatedAt = LocalDateTime.now();
    }

    // 숨김 처리
    public void hide() {
        this.hidden = true;
    }



}