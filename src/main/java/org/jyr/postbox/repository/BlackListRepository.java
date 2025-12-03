package org.jyr.postbox.repository;

import org.jyr.postbox.domain.BlackList;
import org.jyr.postbox.domain.Box;
import org.jyr.postbox.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListRepository extends JpaRepository<BlackList, Long> {

    // 이 박스에서 이 유저가 차단됐는지 확인
    boolean existsByBoxAndBlockedUser(Box box, User blockedUser);

    // 필요하면 해제할 때 사용
    void deleteByBoxAndBlockedUser(Box box, User blockedUser);

}
