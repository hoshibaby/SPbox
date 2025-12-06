package org.jyr.postbox.service;

import org.jyr.postbox.domain.Box;
import org.jyr.postbox.domain.User;
import org.jyr.postbox.dto.box.BoxHeaderDTO;

public interface BoxService {

    // 회원가입 시 유저에게 박스 하나 자동 생성
    Box createBoxForUser(User user);


    // urlKey로 박스 조회 후 헤더 DTO 반환
    BoxHeaderDTO getBoxHeaderByUrlKey(String urlKey);;






}