package org.jyr.postbox.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoxSummaryDTO {

    private Long boxId;
    private String boxTitle;        // ~님의 SecretBox
    private String boxUrlKey;       // /box/{urlKey}
    private String ownerNickname;   // pm10:10
    private long todayMessageCount;
    private long totalMessageCount;

}
