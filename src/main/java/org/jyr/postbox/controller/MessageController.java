package org.jyr.postbox.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jyr.postbox.domain.User;
import org.jyr.postbox.dto.MessageCreateDTO;
import org.jyr.postbox.dto.MessageDetailDTO;
import org.jyr.postbox.dto.MessagePageDTO;
import org.jyr.postbox.service.MessageService;
import org.jyr.postbox.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    // 메시지 작성 (익명 또는 주인)
    @PostMapping
    public ResponseEntity<?> writeMessage(
            @RequestBody MessageCreateDTO dto,
            @RequestParam(value = "email", required = false) String emailOrNull // 로그인 전 임시
    ) {
        User loginUser = null;
        if (emailOrNull != null) {
            loginUser = userService.findByEmail(emailOrNull);
        }

        Long id = messageService.createMessage(dto, loginUser);
        return ResponseEntity.ok("메시지 등록 완료! id = " + id);
    }
    // MyBox - 메시지 목록
    @GetMapping("/me")
    public ResponseEntity<MessagePageDTO> myMessages(
            @RequestParam("email") String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User owner = userService.findByEmail(email);
        MessagePageDTO dto = messageService.getMessagesForOwner(owner, page, size);
        return ResponseEntity.ok(dto);
    }

    // 공개 박스 메시지 목록
    @GetMapping("/public/{urlKey}")
    public ResponseEntity<MessagePageDTO> publicMessages(
            @PathVariable String urlKey,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        MessagePageDTO dto = messageService.getPublicMessages(urlKey, page, size);
        return ResponseEntity.ok(dto);
    }

    // MyBox에서 특정 메시지 클릭했을 때 상세보기
    @GetMapping("/{id}")
    public ResponseEntity<MessageDetailDTO> getDetail(
            @PathVariable Long id,
            @RequestParam("email") String ownerEmail
    ) {
        User owner = userService.findByEmail(ownerEmail);
        MessageDetailDTO dto = messageService.getMessageDetailForOwner(id, owner);
        return ResponseEntity.ok(dto);
    }


    // 답장 달기
    @PatchMapping("/{id}/reply")
    public ResponseEntity<?> reply(
            @PathVariable Long id,
            @RequestParam("email") String ownerEmail,
            @RequestBody String replyContent
    ) {
        User owner = userService.findByEmail(ownerEmail);
        messageService.replyToMessage(id, replyContent, owner);
        return ResponseEntity.ok("답변 완료!");
    }

    // 숨김 처리
    @PatchMapping("/{id}/hide")
    public ResponseEntity<?> hide(
            @PathVariable Long id,
            @RequestParam("email") String ownerEmail
    ) {
        User owner = userService.findByEmail(ownerEmail);
        messageService.hideMessage(id, owner);
        return ResponseEntity.ok("숨김 처리 완료!");
    }

    // 블랙리스트 + 숨김
    @PostMapping("/{id}/blacklist")
    public ResponseEntity<?> blacklistByMessage(
            @PathVariable Long id,
            @RequestParam("ownerEmail") String ownerEmail
    ) {
        User owner = userService.findByEmail(ownerEmail);
        messageService.blacklistUserByMessage(id, owner);
        return ResponseEntity.ok("블랙리스트 설정 및 메시지 숨김 완료!");
    }

//            리엑트에
//                    <button onClick={() => handleBlacklist(message.id)}>
//                    블랙리스트 + 숨김
//                    </button>


}


