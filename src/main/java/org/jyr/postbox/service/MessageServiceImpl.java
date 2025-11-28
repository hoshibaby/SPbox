package org.jyr.postbox.service;

import lombok.RequiredArgsConstructor;
import org.jyr.postbox.domain.*;
import org.jyr.postbox.dto.MessageCreateDTO;
import org.jyr.postbox.dto.MessageDTO;
import org.jyr.postbox.repository.BoxRepository;
import org.jyr.postbox.repository.MessageRepository;
import org.jyr.postbox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private final BoxRepository boxRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;


    @Override
    public Long createMessage(MessageCreateDTO dto, User loginUserOrNull) {

        // 1) 박스 찾기
        Box box = boxRepository.findByUrlKey(dto.getBoxUrlKey())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 박스입니다."));

        // 2) 작성자 타입 결정
        AuthorType authorType;
        User authorUser = null;
        String nickname = dto.getNickname();

        // 로그인한 유저이고, 그 유저가 박스 주인이면 → OWNER 글
        if (loginUserOrNull != null &&
                loginUserOrNull.getId().equals(box.getOwner().getId())) {
            authorType = AuthorType.OWNER;
            authorUser = loginUserOrNull;
            nickname = null; // 주인 글은 별도 닉네임 사용 안 함
        } else {
            authorType = AuthorType.ANONYMOUS;
        }

        // 3) Message 엔티티 생성
        Message message = Message.builder()
                .box(box)
                .content(dto.getContent())
                .nickname(nickname)
                .authorType(authorType)
                .authorUser(authorUser)
                .build();

        Message saved = messageRepository.save(message);
        return saved.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDTO> getMessagesForOwner(User owner) {

        Box box = boxRepository.findByOwner(owner)
                .orElseThrow(() -> new IllegalStateException("해당 유저의 박스가 없습니다."));

        List<Message> list = messageRepository
                .findByBoxOrderByCreatedAtDesc(box);

        return list.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDTO> getPublicMessages(String boxUrlKey) {

        Box box = boxRepository.findByUrlKey(boxUrlKey)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 박스입니다."));

        List<Message> list = messageRepository
                .findByBoxAndHiddenFalseOrderByCreatedAtDesc(box);

        return list.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void replyToMessage(Long messageId, String replyContent, User owner) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        // 박스 주인만 답변 가능
        if (!message.getBox().getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("이 메시지에 답변할 권한이 없습니다.");
        }

        message.writeReply(replyContent);
    }

    @Override
    public void hideMessage(Long messageId, User owner) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        if (!message.getBox().getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("이 메시지를 숨길 권한이 없습니다.");
        }

        message.hide();
    }

    // ----------------- 내부 헬퍼 메서드 -----------------

    private MessageDTO toDTO(Message m) {

        String authorName;
        if (m.getAuthorType() == AuthorType.OWNER && m.getAuthorUser() != null) {
            authorName = m.getAuthorUser().getNickname(); // 계정주 이름
        } else {
            authorName = m.getNickname(); // 익명 닉네임
        }

        return MessageDTO.builder()
                .id(m.getId())
                .authorName(authorName)
                .content(m.getContent())
                .hidden(m.isHidden())
                .replyContent(m.getReplyContent())
                .createdAt(m.getCreatedAt())
                .replyCreatedAt(m.getReplyCreatedAt())
                .build();
    }
}