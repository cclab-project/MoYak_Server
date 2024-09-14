package study.moyak.ai.chatgpt.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import study.moyak.ai.chatgpt.dto.Message;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatGptRequestDto {

    private String model; //어떠한 모델인지
    private List<Message> messages; // 질문
    // private float temperature; // 답변 다양성 -> 값이 클수록 출력이 무작위로 만들어짐
    // private int max_tokens; // 답변 최대 길이
}
