package org.jyr.postbox.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoxDTO {

    private Long id;
    private String title;
    private String urlKey;



//    서비스 , 컨트롤러에서 리턴예정
//    BoxDTO dto = BoxDTO.builder()
//            .id(box.getId())
//            .title(box.getTitle())
//            .urlKey(box.getUrlKey())
//            .build();

}
