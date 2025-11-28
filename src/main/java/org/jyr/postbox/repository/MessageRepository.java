package org.jyr.postbox.repository;

import org.jyr.postbox.domain.Box;
import org.jyr.postbox.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 특정 박스의 메시지 목록 (최신순)
    List<Message> findByBoxOrderByCreatedAtDesc(Box box);

    // 숨김 되지 않은 메시지 목록만 보고 싶을 때 (필요하면 사용)
    List<Message> findByBoxAndHiddenFalseOrderByCreatedAtDesc(Box box);
}