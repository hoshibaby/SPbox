package org.jyr.postbox.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MyBoxResponseDTO {


    private BoxDTO box;                       // 박스 헤더 정보
    private List<MessageSummaryDTO> messages; // 메시지 요약 리스트
}
