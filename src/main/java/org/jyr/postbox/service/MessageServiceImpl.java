package org.jyr.postbox.service;

import lombok.RequiredArgsConstructor;
import org.jyr.postbox.domain.*;
import org.jyr.postbox.dto.box.BoxHeaderDTO;
import org.jyr.postbox.dto.box.MyBoxResponseDTO;
import org.jyr.postbox.dto.message.MessageCreateDTO;
import org.jyr.postbox.dto.message.MessageDetailDTO;
import org.jyr.postbox.dto.message.MessagePageDTO;
import org.jyr.postbox.dto.message.MessageSummaryDTO;
import org.jyr.postbox.repository.BlackListRepository;
import org.jyr.postbox.repository.BoxRepository;
import org.jyr.postbox.repository.MessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private final BoxRepository boxRepository;
    private final MessageRepository messageRepository;
    private final BlackListRepository blackListRepository;

    // =============== 메시지 작성 ===============
    @Override
    public Long createMessage(MessageCreateDTO dto, User loginUserOrNull) {

        // 1) 박스 찾기
        Box box = boxRepository.findByUrlKey(dto.getBoxUrlKey())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 박스입니다."));

        // 🔥 블랙리스트 체크 (로그인 유저인 경우에만)
        if (loginUserOrNull != null &&
                blackListRepository.existsByBoxAndBlockedUser(box, loginUserOrNull)) {
            throw new IllegalStateException("이 박스에서 차단된 사용자입니다.");
        }

        // 2) 작성자 타입 결정
        AuthorType authorType;
        User authorUser = null;

        // 로그인한 유저이고, 그 유저가 박스 주인이면 → OWNER 글
        if (loginUserOrNull != null &&
                loginUserOrNull.getId().equals(box.getOwner().getId())) {

            authorType = AuthorType.OWNER;
            authorUser = loginUserOrNull;    // 주인 정보

        } else {
            // 익명 방문자 or 로그인했지만 남의 박스에 쓰는 경우
            authorType = AuthorType.ANONYMOUS;

            // ⭐ 로그인한 방문자라면, 내부적으로는 누군지 기억해 두기
            if (loginUserOrNull != null) {
                authorUser = loginUserOrNull;   // ← 이게 포인트!
            }
        }

        // 3) Message 엔티티 생성 (nickname 없이)
        Message message = Message.builder()
                .box(box)
                .content(dto.getContent())
                .authorType(authorType)
                .authorUser(authorUser)   // 방문자/주인 모두 여기 저장
                .build();

        Message saved = messageRepository.save(message);
        return saved.getId();
    }

    // =============== MyBox 메시지 리스트(페이지) ===============
    @Override
    @Transactional(readOnly = true)
    public MessagePageDTO getMessagesForOwner(User owner, int page, int size) {

        Box box = boxRepository.findByOwner(owner)
                .orElseThrow(() -> new IllegalStateException("해당 유저의 박스가 없습니다."));

        PageRequest pageable = PageRequest.of(page, size);
        Page<Message> result = messageRepository
                .findByBoxOrderByCreatedAtDesc(box, pageable);

        return MessagePageDTO.builder()
                .page(result.getNumber())
                .size(result.getSize())
                .totalPages(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .content(
                        result.getContent().stream()
                                .map(this::toSummaryDTO)
                                .collect(Collectors.toList())
                )
                .build();
    }



    // =============== 공개 메시지 리스트(페이지) ===============
    @Override
    @Transactional(readOnly = true)
    public MessagePageDTO getPublicMessages(String boxUrlKey, int page, int size) {

        Box box = boxRepository.findByUrlKey(boxUrlKey)
                .orElseThrow(() -> new IllegalArgumentException("박스를 찾을 수 없습니다."));

        PageRequest pageable = PageRequest.of(page, size);
        Page<Message> result = messageRepository
                .findByBoxAndHiddenFalseOrderByCreatedAtDesc(box, pageable);

        return MessagePageDTO.builder()
                .page(result.getNumber())
                .size(result.getSize())
                .totalPages(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .content(
                        result.getContent().stream()
                                .map(this::toSummaryDTO)
                                .collect(Collectors.toList())
                )
                .build();
    }


    // =============== 답장 / 숨김 / 블랙리스트 ===============
    @Override
    public void replyToMessage(Long messageId, String replyContent, User owner) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        if (!message.getBox().getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("이 메시지에 답변할 권한이 없습니다.");
        }

        message.writeReply(replyContent);
    }

    @Transactional
    public void clearReply(Long messageId, User owner) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));

        // 이 메시지가 진짜 이 사람(post box 주인)의 것인지 확인
        if (!message.getBox().getOwner().getId().equals(owner.getId())) {
              throw new IllegalStateException("내 박스의 메시지가 아닙니다.");
        }

        // 답장 내용/시간 비우기
        message.setReplyContent(null);
        message.setReplyCreatedAt(null);

        messageRepository.save(message);
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

    @Transactional
    @Override
    public void blacklistUserByMessage(Long messageId, User owner) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        Box box = message.getBox();

        if (!box.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("이 박스의 주인이 아닙니다.");
        }

        User blockedUser = message.getAuthorUser();

        // 익명인 경우 - 유저 차단 불가 → 메시지만 숨기기
        if (blockedUser == null) {
            message.hide();
            messageRepository.save(message);
            return;
        }

        // 블랙리스트 저장
        if (!blackListRepository.existsByBoxAndBlockedUser(box, blockedUser)) {
            blackListRepository.save(
                    BlackList.builder()
                            .box(box)
                            .blockedUser(blockedUser)
                            .build()
            );
        }

        // 메시지 숨김
        message.hide();
        messageRepository.save(message);
    }

    // =========================
    // 내부 변환 메서드들
    // =========================

    private String calculateAuthorLabel(Message m) {
        switch (m.getAuthorType()) {

            case ANONYMOUS:
                return "익명";

            case OWNER:
            default:
                return m.getAuthorUser() != null
                        ? m.getAuthorUser().getNickname()
                        : "계정주";
        }
    }



    // =============== 내부 변환 메서드 ===============
    private MessageSummaryDTO toSummaryDTO(Message m) {

        boolean fromOwner = (m.getAuthorType() == AuthorType.OWNER);

        return MessageSummaryDTO.builder()
                .id(m.getId())
                .shortContent(shorten(m.getContent(), 20))
                .fromOwner(fromOwner)
                .hasReply(m.getReplyContent() != null)
                .hidden(m.isHidden())

                .createdAt(m.getCreatedAt())
                .authorType(m.getAuthorType().name())
                .authorLabel(calculateAuthorLabel(m))

                .build();
    }

    private String shorten(String content, int max) {
        if (content == null) return "";
        if (content.length() <= max) return content;
        return content.substring(0, max) + "...";
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDetailDTO getMessageDetailForOwner(Long messageId, User owner) {

        // 1) 메시지 조회
        Message m = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        // 2) 권한 체크 - 이 메시지가 owner 의 박스에 달린 건지 확인
        if (!m.getBox().getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("이 메시지에 접근할 권한이 없습니다.");
        }

        // 3) 작성자가 박스 주인인지 여부
        boolean fromOwner = (m.getAuthorType() == AuthorType.OWNER);

        // 4) DTO 로 변환해서 리턴
        return MessageDetailDTO.builder()
                .id(m.getId())
                .content(m.getContent())
                .fromOwner(fromOwner)
                .hidden(m.isHidden())
                .createdAt(m.getCreatedAt())
                .replyContent(m.getReplyContent())
                .replyCreatedAt(m.getReplyCreatedAt())
                .authorUserId(m.getAuthorUser() != null ? m.getAuthorUser().getId() : null)
                .authorType(m.getAuthorType().name())
                .boxOwnerId(m.getBox().getOwner().getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MyBoxResponseDTO getMyBox(User owner) {

        // 1) 박스 찾기
        Box box = boxRepository.findByOwner(owner)
                .orElseThrow(() -> new IllegalStateException("박스가 없습니다."));

        // 2) 박스 헤더 정보용 카운트 값 계산
        long totalMessageCount  = messageRepository.countByBox(box);
        long unreadMessageCount = messageRepository.countByBoxAndHiddenFalse(box);
        long replyCount         = messageRepository.countByBoxAndReplyContentIsNotNull(box);

        // 2-1) 박스 헤더 DTO 생성
        BoxHeaderDTO boxHeaderDTO = BoxHeaderDTO.builder()
                .boxId(box.getId())
                .boxTitle(box.getTitle())
                .urlKey(box.getUrlKey())
                .ownerName(owner.getNickname())
                .profileImageUrl(owner.getProfileImageUrl())
                .totalMessageCount(totalMessageCount)
                .unreadMessageCount(unreadMessageCount)
                .replyCount(replyCount)
                .build();

        // 3) 메시지 요약 리스트
        List<MessageSummaryDTO> summaryList = messageRepository
                .findByBoxOrderByCreatedAtDesc(box)
                .stream()
                .map(this::toSummaryDTO)
                .toList();

        // 4) 조합해서 리턴
        return MyBoxResponseDTO.builder()
                .box(boxHeaderDTO)
                .messages(summaryList)
                .build();
    }

    @Override
    public void updateMessage(Long messageId, String newContent, User loginUser) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        // 1) 작성자가 로그인 유저여야 하고
        // 2) 그 박스의 주인도 로그인 유저여야 한다 = 내 박스에 내가 쓴 글만 수정 가능
        if (message.getAuthorUser() == null ||
                !message.getAuthorUser().getId().equals(loginUser.getId()) ||
                !message.getBox().getOwner().getId().equals(loginUser.getId())) {
            throw new IllegalStateException("내 박스에 내가 쓴 메시지만 수정할 수 있습니다.");
        }

        message.setContent(newContent);
    }

    @Override
    public void deleteMessage(Long messageId, User loginUser) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        if (message.getAuthorUser() == null ||
                !message.getAuthorUser().getId().equals(loginUser.getId()) ||
                !message.getBox().getOwner().getId().equals(loginUser.getId())) {
            throw new IllegalStateException("내 박스에 내가 쓴 메시지만 삭제할 수 있습니다.");
        }

        messageRepository.delete(message);
    }


}