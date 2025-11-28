package org.jyr.postbox.service;

import org.jyr.postbox.domain.User;
import org.jyr.postbox.dto.MessageCreateDTO;
import org.jyr.postbox.dto.MessageDTO;

import java.util.List;

public interface MessageService {

    // 익명/주인 모두 메시지 작성
    Long createMessage(MessageCreateDTO dto, User loginUserOrNull);

    // 박스 주인용: 내 박스의 전체 메시지(숨김 포함)
    List<MessageDTO> getMessagesForOwner(User owner);

    // 공개용: 숨김 되지 않은 메시지(답변 탭 등에서 사용)
    List<MessageDTO> getPublicMessages(String boxUrlKey);

    // 박스 주인이 메시지에 답변 달기
    void replyToMessage(Long messageId, String replyContent, User owner);

    // 박스 주인이 메시지 숨김 처리
    void hideMessage(Long messageId, User owner);
}