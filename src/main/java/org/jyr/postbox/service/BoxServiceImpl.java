package org.jyr.postbox.service;

import lombok.RequiredArgsConstructor;
import org.jyr.postbox.domain.Box;
import org.jyr.postbox.domain.User;
import org.jyr.postbox.dto.BoxDTO;
import org.jyr.postbox.repository.BoxRepository;
import org.jyr.postbox.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoxServiceImpl implements BoxService {

    private final BoxRepository boxRepository;
    private final MessageRepository messageRepository;

    @Override
    public Box createBoxForUser(User user) {

        String urlKey = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10);

        Box box = Box.builder()
                .owner(user)
                .urlKey(urlKey)
                .title(user.getNickname() + "님의 SecretBox")
                .build();

        return boxRepository.save(box);
    }

    @Override
    public BoxDTO getBoxForUser(User user) {

        Box box = boxRepository.findByOwner(user)
                .orElseThrow(() -> new IllegalStateException("박스가 없습니다."));

        long total = messageRepository.countByBox(box); // 전체 메시지 수
        long unread = messageRepository.countByBoxAndHiddenFalse(box); // 숨김 아님 = 노출 메시지 수
        long replyCount = messageRepository.countByBoxAndReplyContentIsNotNull(box); // 답장 달린 개수

        return BoxDTO.builder()
                .id(box.getId())
                .title(box.getTitle())
                .urlKey(box.getUrlKey())
                .ownerName(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl()) // Firestore 붙으면 여기 채우면 됨
                .totalMessageCount(total)
                .unreadMessageCount(unread)
                .replyCount(replyCount)
                .build();
    }

    @Override
    public Box getBoxByUrlKey(String urlKey) {
        return boxRepository.findByUrlKey(urlKey)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 박스입니다."));
    }
}
