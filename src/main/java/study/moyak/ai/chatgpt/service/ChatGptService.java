package study.moyak.ai.chatgpt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import study.moyak.ai.chatgpt.dto.Message;
import study.moyak.ai.chatgpt.dto.request.ChatGptRequestDto;
import study.moyak.ai.chatgpt.dto.response.ChatGptResponseDto;
import study.moyak.ai.chatgpt.dto.response.Choice;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ChatGptService {

    @Value("${gpt.api.model}")
    private String model;

    @Value("${gpt.api.url}")
    private String apiUrl;

    @Value("${gpt.api-key}")
    private String apiKey;

    // 정확한 답변을 위한 프롬프트 추가: 여기서 반환한 것으로 gpt에게 질문할 예정
    public String getPrompt(String question) {

        String prompt_question = "내가 지금 약을 먹으려고 해. 다음은 약들의 성분이야"
                // + 약의 이름 및 성분에 대한 정보 (EachPill에서 꺼내오기)
                + question // 사용자 질문 ex) 이 약 술과 함께 먹어도 돼? or 이 약을 한 번에 먹어도 돼?
                + "약들의 성분을 고려해서 간결하게 대답해줘. 의사와 상담하라는 내용은 제외하고, 존댓말로 대답해줘.";

        return prompt_question;
    }


    // gpt에게 요청 보내기
    public Message gptRequest(String message) {

        List<Message> prompts = new ArrayList<>();
        prompts.add(new Message("user", message));
        ChatGptRequestDto requestDto = new ChatGptRequestDto(model, prompts);

        RestTemplate rt = new RestTemplate();

        //header 정보
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + apiKey);

        HttpEntity<ChatGptRequestDto> gptRequest = new HttpEntity<>(requestDto, headers);

        ChatGptResponseDto gptResponse = rt.exchange(
                apiUrl, HttpMethod.POST, gptRequest, ChatGptResponseDto.class).getBody();

        Message getMessage = gptResponse.getChoices().get(0).getMessage();

        if(getMessage != null) {
            return getMessage;
        }else {
            throw new RuntimeException("응답을 받을 수 없습니다");
        }
    }
}
