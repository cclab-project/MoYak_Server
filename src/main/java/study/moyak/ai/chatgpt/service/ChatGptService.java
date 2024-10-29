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
import study.moyak.chat.entity.ChatMessage;
import study.moyak.chat.entity.EachPill;
import study.moyak.chat.repository.ChatMessageRepository;
import study.moyak.chat.repository.ChatRepository;
import study.moyak.chat.repository.EachPillRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChatGptService {

    @Value("${gpt.api.model}")
    private String model;

    @Value("${gpt.api.url}")
    private String apiUrl;

    @Value("${gpt.api-key}")
    private String apiKey;

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRepository chatRepository;
    private final EachPillRepository eachPillRepository;

    // 정확한 답변을 위한 프롬프트 추가: 여기서 반환한 것으로 gpt에게 질문할 예정
    // 사용자의 질문 db에 저장 (사용자에게는 프롬프트하기 전(=사용자가 입력한) 질문을 보여줘야 함)
    public String getPrompt(Long chatId, String question) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("user");
        chatMessage.setContent(question);
        chatMessage.setChatroom(chatRepository.findById(chatId).get());

        // 사용자의 질문 저장
        chatMessageRepository.save(chatMessage);

        // chatId에 해당하는 약정보 조회
        List<EachPill> pills = eachPillRepository.findByChatroomId(chatId);

        // 약 정보들을 "약 이름 : 성분" 형식으로 바꾸기
        String pillInfo = pills.stream()
                .map(pill -> pill.getPillName() + " : " + pill.getPillIngredient())
                .collect(Collectors.joining("\n"));

        // 프롬프트
        String promptQuestion = "현재 복용하려는 약 정보는 다음과 같습니다:\n\n"
                + pillInfo + "\n\n" // 약의 이름 및 성분에 대한 정보 포함
                + "사용자 질문: \"" + question + "\"\n\n" // 사용자가 입력한 질문 포함
                + "위 약들의 성분을 고려하여 사용자 질문에 대한 간결한 답변을 제공해 주세요. "
                + "의사와 상담하라는 내용은 제외하고, 존댓말로 부드럽게 답변해 주세요.";

        System.out.println(promptQuestion);

        return promptQuestion;
    }


    // gpt에게 요청 보내기
    public Message gptRequest(Long chat_id, String promptQuestion) {

        List<Message> prompts = new ArrayList<>();
        prompts.add(new Message("user", promptQuestion));
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

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole(getMessage.getRole());
        chatMessage.setContent(getMessage.getContent());
        chatMessage.setChatroom(chatRepository.findById(chat_id).get());

        chatMessageRepository.save(chatMessage);

        if(getMessage != null) {
            return getMessage;
        }else {
            throw new RuntimeException("응답을 받을 수 없습니다");
        }
    }
}
