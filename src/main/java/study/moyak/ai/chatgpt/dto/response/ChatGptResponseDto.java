package study.moyak.ai.chatgpt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGptResponseDto {

    private String id;
    private String object;
    private String model;
    private List<Choice> choices;
}
