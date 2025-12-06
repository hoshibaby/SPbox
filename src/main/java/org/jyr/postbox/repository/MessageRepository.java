package org.jyr.postbox.repository;

import org.jyr.postbox.domain.Box;
import org.jyr.postbox.domain.Message;
import org.jyr.postbox.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // ========= 목록(리스트) 조회 =========
    // 특정 박스의 메시지 목록 (최신순)
    List<Message> findByBoxOrderByCreatedAtDesc(Box box);

    // ========= 카운트 =========

    // 총 메시지 수
    long countByBox(Box box);

    // 숨김되지 않은 메시지 수 = 읽지 않은 메시지 역할
    long countByBoxAndHiddenFalse(Box box);

    //답장이 존재하는 메시지 수

    long countByBoxAndReplyContentIsNotNull(Box box);

    // ================== 페이지네이션용 ==================

    // 박스 주인이 보는 "내 박스 메시지" 목록 (숨김 포함, 최신순)
    Page<Message> findByBoxOrderByCreatedAtDesc(Box box, Pageable pageable);

    // 공개 모드에서 보는 메시지 목록 (숨김 제외, 최신순)
    Page<Message> findByBoxAndHiddenFalseOrderByCreatedAtDesc(Box box, Pageable pageable);

}