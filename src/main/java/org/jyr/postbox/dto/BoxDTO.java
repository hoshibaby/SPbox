package org.jyr.postbox.dto;

import lombok.*;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoxDTO {

    private Long id;            // 박스 PK
    private String title;       // 제목: "열시님의 비밀상자"
    private String urlKey;      // URL 연결 키

    private String ownerName;   // 박스 주인 (User.nickname)
    private String profileImageUrl; // Firestore 프로필 이미지 URL (없다면 null)

    // 메시지 관련 정보
    private long totalMessageCount;   // 전체 메세지 수
    private long unreadMessageCount;  // 안 읽은 메세지 수 (옵션)

    // 답장 관련 정보 (옵션)
    private long replyCount;          // 내가 작성한 답변 수




//    서비스 , 컨트롤러에서 리턴예정
//    BoxDTO dto = BoxDTO.builder()
//            .id(box.getId())
//            .title(box.getTitle())
//            .urlKey(box.getUrlKey())
//            .build();

}
