package study.moyak.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import study.moyak.chat.dto.EachPillDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponseDTO { // 채팅내역 반환하는 DTO

    private List<EachPillDTO> eachPills;
    private List<ChatMessageDTO> chatMessages;
}
