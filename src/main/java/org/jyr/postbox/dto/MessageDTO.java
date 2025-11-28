package org.jyr.postbox.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageDTO {

    private Long id;

    // 작성자 표시용
    private String authorName;
    // ANONYMOUS이면 익명 닉네임, OWNER이면 유저 닉네임

    private String content;
    private boolean hidden;

    private String replyContent;
    private LocalDateTime createdAt;
    private LocalDateTime replyCreatedAt;

    //authorType == OWNER → authorName = authorUser.getNickname()
    //authorType == ANONYMOUS → authorName = nickname
}
