package org.jyr.postbox.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageSummaryDTO {

    private Long id;             // 메시지 PK
    private String shortContent; // 앞 20글자 요약 (중요!!)
    private boolean fromOwner;   // 주인 글인지
    private boolean hasReply;    // 답장 여부
    private boolean hidden;      // 숨김 여부
    private LocalDateTime createdAt; // 작성 시간
}
