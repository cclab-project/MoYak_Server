package study.moyak.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatResponseDTO { // 채팅이 생성되면 client로 chat_id 와 title 전달해줘야 함

    private Long chat_id;
    private String title;
}
