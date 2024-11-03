package study.moyak.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatListDTO { // 홈화면에서 보여줄 목록: 전체 이미지, 제목, 약 이름, 생성 날짜

    private String allImage;
    private String title;
    private List<String> pillName;
    private String createDate;

}
