package study.moyak.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatListDTO { // 홈화면에서 보여줄 목록: 전체 이미지, 제목, 약 이름, 생성 날짜

    private String title;
    private String pillName;
    private String createDate;

}
