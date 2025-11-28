package org.jyr.postbox.dto;

import lombok.Data;

@Data
public class MessageCreateDTO {

    // 어떤 박스에 쓰는지 (예: abc123ef)
    private String boxUrlKey;

    // 작성자가 입력하는 가명 닉네임
    private String nickname;

    // 본문 내용
    private String content;
}