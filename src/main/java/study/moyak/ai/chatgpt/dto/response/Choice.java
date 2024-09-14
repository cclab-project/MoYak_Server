package study.moyak.ai.chatgpt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import study.moyak.ai.chatgpt.dto.Message;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Choice {

    private int index;
    private Message message;
}
