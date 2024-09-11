package study.moyak.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import study.moyak.ai.chatgpt.dto.Message;
import study.moyak.ai.chatgpt.service.ChatGptService;
import study.moyak.chat.service.ChatService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatGptService chatGptService;

    // 채팅방 생성
    @PostMapping("/chat/create")
    public ResponseEntity<?> createChat(@RequestParam MultipartFile all_image) throws IOException {
        // 이미지 파일 정보 출력
        System.out.println("Original Filename: " + all_image.getOriginalFilename());

        return chatService.createChat(all_image);
    }

    // chat_id번째 채팅방 불러오기
    @GetMapping("/chat/{chat_id}")
    public ResponseEntity<?> getChat(@PathVariable("chat_id") Long chat_id) throws IOException {

        System.out.println("chat_id = " + chat_id);

        return chatService.getChat(chat_id);
    }

    // chat_id번째 채팅방에 질문 추가
    @PostMapping("/chat/{chat_id}")
    public ResponseEntity<Message> updateChat(
            @PathVariable("chat_id") Long chat_id,
            @RequestBody String question
    ){

        System.out.println("질문 추가할 채팅방 번호 = " + chat_id);
        System.out.println("사용자의 질문 = " + question);

        String prompt_question = chatGptService.getPrompt(question);

        return ResponseEntity.ok(chatGptService.gptRequest(prompt_question));
    }

    // chat_id번째 채팅방 삭제
    @DeleteMapping("/chat/{chat_id}")
    public ResponseEntity<?> deleteChat(@PathVariable("chat_id") Long chat_id) throws IOException {
        return chatService.deleteChat(chat_id);
    }

    // chat_id번째 채팅방 제목 수정
    @PatchMapping("/chat/{chat_id}")
    public ResponseEntity<?> updateTitle(@PathVariable("chat_id") Long chat_id, @RequestParam String title) throws IOException {
            return chatService.updateTitle(chat_id, title);
    }
}