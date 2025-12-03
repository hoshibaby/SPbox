package org.jyr.postbox.controller;

import lombok.RequiredArgsConstructor;
import org.jyr.postbox.domain.User;
import org.jyr.postbox.dto.BoxDTO;
import org.jyr.postbox.dto.MyBoxResponseDTO;
import org.jyr.postbox.service.BoxService;
import org.jyr.postbox.service.MessageService;
import org.jyr.postbox.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/box")
public class BoxController {

    private final UserService userService;
    private final BoxService boxService;
    private final MessageService messageService;


    // 내 박스 전체 정보 + 메시지 요약 리스트
    @GetMapping("/me")
    public ResponseEntity<MyBoxResponseDTO> getMyBox(@RequestParam("email") String email) {
        User owner = userService.findByEmail(email);
        MyBoxResponseDTO dto = messageService.getMyBox(owner);
        return ResponseEntity.ok(dto);
    }

    // URL Key로 박스 조회
    @GetMapping("/{urlKey}")
    public ResponseEntity<?> getBox(@PathVariable String urlKey) {
        return ResponseEntity.ok(boxService.getBoxByUrlKey(urlKey));
    }



}