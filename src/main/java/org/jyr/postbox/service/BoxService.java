package org.jyr.postbox.service;

import org.jyr.postbox.domain.Box;
import org.jyr.postbox.domain.User;
import org.jyr.postbox.dto.BoxDTO;

public interface BoxService {

    // 회원가입 시 유저에게 박스 하나 자동 생성
    Box createBoxForUser(User user);

    // 로그인한 사용자의 박스 정보
    BoxDTO getBoxForUser(User user);

    // urlKey로 박스 찾기 (익명 접근용)
    Box getBoxByUrlKey(String urlKey);






}