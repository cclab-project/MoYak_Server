package study.moyak.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import study.moyak.ai.chatgpt.dto.Message;
import study.moyak.ai.chatgpt.service.ChatGptService;
import study.moyak.chat.dto.ChatDTO;
import study.moyak.chat.dto.request.NewChatDTO;
import study.moyak.chat.service.ChatService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatGptService chatGptService;

    // 채팅방 생성
    @PostMapping("/chat/create")
    public ResponseEntity<?> createChat(@RequestParam MultipartFile allImage) throws IOException {
        // 이미지 파일 정보 출력
        System.out.println("Original Filename: " + allImage.getOriginalFilename());

        return chatService.createChat(allImage);
    }

    // chat_id번째 채팅방 불러오기
    @GetMapping("/chat/{chat_id}")
    public ResponseEntity<?> getChat(@PathVariable("chat_id") Long chat_id) throws IOException {

        System.out.println("chat_id = " + chat_id);

        return chatService.getChat(chat_id);
    }

    // chat_id번째 채팅방에 질문 추가 -> Message 테이블에 대화내용 추가해야함 + UpdateChatDto 추가
    @PostMapping("/chat/{chat_id}")
    public ResponseEntity<Message> updateChat(
            @PathVariable("chat_id") Long chat_id,
            @RequestBody NewChatDTO content
    ){

        String question = content.getContent();

        System.out.println("질문 추가할 채팅방 번호 = " + chat_id);
        System.out.println("사용자의 질문 = " + question);

        String promptQuestion = chatGptService.getPrompt(question);

        return ResponseEntity.ok(chatGptService.gptRequest(promptQuestion));
    }

    // chat_id번째 채팅방 삭제
    @DeleteMapping("/chat/{chat_id}")
    public ResponseEntity<?> deleteChat(@PathVariable("chat_id") Long chat_id) throws IOException {
        return chatService.deleteChat(chat_id);
    }

    // chat_id번째 채팅방 제목 수정
    @PatchMapping("/chat/{chat_id}")
    public ResponseEntity<?> updateTitle(
            @PathVariable("chat_id") Long chat_id, @RequestBody ChatDTO title) throws IOException {

        System.out.println("수정할 제목 = " + title.getTitle());

        String newTitle = title.getTitle();

        return chatService.updateTitle(chat_id, newTitle);
    }

    // 채팅방 내역 요청
    @GetMapping("/chat/list/{chat_id}")
    public ResponseEntity<?> chatList(@PathVariable("chat_id") Long chat_id) throws IOException {

        return null;
    }
}