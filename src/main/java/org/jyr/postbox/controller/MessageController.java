package org.jyr.postbox.controller;

import lombok.RequiredArgsConstructor;
import org.jyr.postbox.domain.User;
import org.jyr.postbox.dto.message.MessageCreateDTO;
import org.jyr.postbox.dto.message.MessageDetailDTO;
import org.jyr.postbox.dto.message.MessagePageDTO;
import org.jyr.postbox.dto.message.MessageUpdateRequestDTO;
import org.jyr.postbox.service.MessageService;
import org.jyr.postbox.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    // =========================
    // 1. 메시지 작성 (익명 / 로그인 / 박스 주인)
    // =========================
    @PostMapping("/message")
    public ResponseEntity<?> writeMessage(
            @RequestBody MessageCreateDTO dto,
            @RequestParam(value = "userId", required = false) String userIdOrNull
    ) {
        User loginUser = null;
        if (userIdOrNull != null) {
            loginUser = userService.findByUserId(userIdOrNull);
        }

        Long id = messageService.createMessage(dto, loginUser);
        return ResponseEntity.ok("메시지 등록 완료! id = " + id);
    }

    // =========================
    // 2. MyBox - 메시지 목록 조회
    // =========================
    @GetMapping("/me/messages")
    public ResponseEntity<MessagePageDTO> myMessages(
            @RequestParam("userId") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User owner = userService.findByUserId(userId);
        MessagePageDTO dto = messageService.getMessagesForOwner(owner, page, size);
        return ResponseEntity.ok(dto);
    }

    // =========================
    // 3. MyBox - 메시지 상세 조회
    // =========================
    @GetMapping("/me/messages/{id}")
    public ResponseEntity<MessageDetailDTO> getDetail(
            @PathVariable Long id,
            @RequestParam("userId") String userId
    ) {
        User owner = userService.findByUserId(userId);
        MessageDetailDTO dto = messageService.getMessageDetailForOwner(id, owner);
        return ResponseEntity.ok(dto);
    }

    // =========================
    // 4. 답장 달기 / 삭제하기
    //    - body 가 비어 있으면 "답장 삭제"
    // =========================
    @PatchMapping(
            value = "/me/messages/{id}/reply",
            consumes = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<?> reply(
            @PathVariable Long id,
            @RequestParam("userId") String userId,
            @RequestBody(required = false) String replyContent
    ) {
        User owner = userService.findByUserId(userId);

        String trimmed = (replyContent == null) ? "" : replyContent.trim();

        // 빈 문자열이면 답장 삭제
        if (trimmed.isEmpty()) {
            messageService.clearReply(id, owner);
            return ResponseEntity.noContent().build();
        }

        // 내용이 있으면 답장 저장/수정
        messageService.replyToMessage(id, trimmed, owner);
        return ResponseEntity.ok("답변 완료!");
    }

    // =========================
    // 5. 메시지 숨김 처리 (박스 주인 전용)
    // =========================
    @PatchMapping("/me/messages/{id}/hide")
    public ResponseEntity<?> hide(
            @PathVariable Long id,
            @RequestParam("userId") String userId
    ) {
        User owner = userService.findByUserId(userId);
        messageService.hideMessage(id, owner);
        return ResponseEntity.ok("숨김 처리 완료!");
    }

    // =========================
    // 6. 블랙리스트 + 숨김 (로그인 회원만 블랙리스트 가능)
    // =========================
    @PostMapping("/me/messages/{id}/blacklist")
    public ResponseEntity<?> blacklistByMessage(
            @PathVariable Long id,
            @RequestParam("userId") String userId
    ) {
        User owner = userService.findByUserId(userId);
        messageService.blacklistUserByMessage(id, owner);
        return ResponseEntity.ok("블랙리스트 설정 및 메시지 숨김 완료!");
    }

    // =========================
    // 7. 원본 메시지 수정
    //    - 내 박스에 내가 쓴 메시지만 수정 가능
    // =========================
    @PutMapping("/me/messages/{id}")
    public ResponseEntity<?> updateMessage(
            @PathVariable Long id,
            @RequestBody MessageUpdateRequestDTO dto
    ) {
        User loginUser = userService.findByUserId(dto.getUserId());
        messageService.updateMessage(id, dto.getContent(), loginUser);
        return ResponseEntity.ok("메시지 수정 완료");
    }

    // =========================
    // 8. 원본 메시지 삭제
    //    - 내 박스에 내가 쓴 메시지만 삭제 가능
    // =========================
    @DeleteMapping("/me/messages/{id}")
    public ResponseEntity<?> deleteMessage(
            @PathVariable Long id,
            @RequestParam("userId") String userId
    ) {
        User loginUser = userService.findByUserId(userId);
        messageService.deleteMessage(id, loginUser);
        return ResponseEntity.ok("메시지 삭제 완료");
    }

}
