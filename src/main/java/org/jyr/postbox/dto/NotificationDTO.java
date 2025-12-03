package org.jyr.postbox.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {

    private Long id;
    private String text;          // "호랑이님의 답변을 확인해보세요."
    private String type;          // NEW_MESSAGE / REPLY_RECEIVED
    private LocalDateTime createdAt;
    private boolean read;

    // 클릭했을 때 이동할 정보
    private String boxUrlKey;
    private Long messageId;

}
