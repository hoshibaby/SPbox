package org.jyr.postbox.controller;

import lombok.RequiredArgsConstructor;
import org.jyr.postbox.domain.User;
import org.jyr.postbox.dto.BoxDTO;
import org.jyr.postbox.service.BoxService;
import org.jyr.postbox.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/box")
public class BoxController {

    private final UserService userService;
    private final BoxService boxService;

    // 본인 박스 조회
    @GetMapping("/me")
    public ResponseEntity<?> getMyBox(@RequestParam("email") String email) {

        // 임시 로그인 방식: email만 보내면 그 사람으로 판단
        User user = userService.findByEmail(email);

        BoxDTO dto = boxService.getBoxForUser(user);

        return ResponseEntity.ok(dto);
    }

    // URL Key로 박스 조회
    @GetMapping("/{urlKey}")
    public ResponseEntity<?> getBox(@PathVariable String urlKey) {

        return ResponseEntity.ok(boxService.getBoxByUrlKey(urlKey));
    }
}