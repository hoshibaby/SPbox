package org.jyr.postbox.dto.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageUpdateRequestDTO {

    private String userId;
    private String content;
}
