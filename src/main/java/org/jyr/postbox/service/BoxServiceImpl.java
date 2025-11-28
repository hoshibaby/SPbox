package org.jyr.postbox.service;

import lombok.RequiredArgsConstructor;
import org.jyr.postbox.domain.Box;
import org.jyr.postbox.domain.User;
import org.jyr.postbox.dto.BoxDTO;
import org.jyr.postbox.repository.BoxRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoxServiceImpl implements BoxService {

    private final BoxRepository boxRepository;

    @Override
    public Box createBoxForUser(User user) {

        // urlKey 랜덤 생성 (10자리 정도)
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
                .orElseThrow(() -> new IllegalStateException("해당 유저의 박스가 없습니다."));

        return BoxDTO.builder()
                .id(box.getId())
                .title(box.getTitle())
                .urlKey(box.getUrlKey())
                .build();
    }

    @Override
    public Box getBoxByUrlKey(String urlKey) {
        return boxRepository.findByUrlKey(urlKey)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 박스입니다."));
    }
}