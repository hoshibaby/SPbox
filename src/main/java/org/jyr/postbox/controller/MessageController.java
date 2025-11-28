package org.jyr.postbox.controller;

import lombok.RequiredArgsConstructor;
import org.jyr.postbox.domain.User;
import org.jyr.postbox.dto.MessageCreateDTO;
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

    // 주인 본인 박스 메시지 보기
    @GetMapping("/me")
    public ResponseEntity<?> myMessages(@RequestParam("email") String email) {

        User owner = userService.findByEmail(email);
        return ResponseEntity.ok(messageService.getMessagesForOwner(owner));
    }

    // 공개 메시지 조회
    @GetMapping("/public/{urlKey}")
    public ResponseEntity<?> publicMessages(@PathVariable String urlKey) {
        return ResponseEntity.ok(messageService.getPublicMessages(urlKey));
    }

   //  답변 달기
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
}