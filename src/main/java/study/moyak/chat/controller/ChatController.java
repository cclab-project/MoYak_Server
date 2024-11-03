package study.moyak.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import study.moyak.ai.chatgpt.dto.Message;
import study.moyak.ai.chatgpt.service.ChatGptService;
import study.moyak.chat.dto.request.CreateChatDTO;
import study.moyak.chat.dto.request.NewChatDTO;
import study.moyak.chat.dto.request.UpdateTitleDTO;
import study.moyak.chat.entity.EachPill;
import study.moyak.chat.repository.EachPillRepository;
import study.moyak.chat.service.ChatService;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatGptService chatGptService;

    // 홈화면 요청 -> 알약 전체 사진, 알약 이름, 생성된 날짜 꺼내와 주세요
    @GetMapping("/chat/list?userId={userId}")
    public ResponseEntity<?> chatList(@PathVariable("userId") Long userId) throws IOException {


        return chatService.chatList(userId);
    }

    // 채팅방 생성 -> createDate와 chatId를 보내주세요
    @PostMapping("/chat/create")
    public ResponseEntity<?> createChat(@RequestBody CreateChatDTO createChatDTO) throws IOException {
        System.out.println(createChatDTO.getAll_image_url());
        System.out.println(createChatDTO.getTimeStamp());

        return chatService.createChat(createChatDTO);
    }

    // chat_id번째 채팅방 불러오기 -> 이때 제목도 보내줘야함
    @GetMapping("/chat/{chat_id}")
    public ResponseEntity<?> getChat(@PathVariable("chat_id") Long chat_id) throws IOException {

        System.out.println("chat_id = " + chat_id);

        return chatService.getChat(chat_id);
    }

    // chat_id번째 채팅방에 질문 추가 -> Message 테이블에 대화내용 추가
    @PostMapping("/chat/{chat_id}")
    public ResponseEntity<Message> updateChat(
            @PathVariable("chat_id") Long chat_id,
            @RequestBody NewChatDTO content
    ){

        String question = content.getContent();

        System.out.println("질문 추가할 채팅방 번호 = " + chat_id);
        System.out.println("사용자의 질문 = " + question);

        String promptQuestion = chatGptService.getPrompt(chat_id, question);

        return ResponseEntity.ok(chatGptService.gptRequest(chat_id, promptQuestion));
    }

    // chat_id번째 채팅방 삭제
    @DeleteMapping("/chat/{chat_id}")
    public ResponseEntity<?> deleteChat(@PathVariable("chat_id") Long chat_id) throws IOException {
        return chatService.deleteChat(chat_id);
    }

    // chat_id번째 채팅방 제목 수정
    @PatchMapping("/chat/{chat_id}")
    public ResponseEntity<?> updateTitle(
            @PathVariable("chat_id") Long chat_id, @RequestBody UpdateTitleDTO title) throws IOException {

        System.out.println("수정할 제목 = " + title.getTitle());

        String newTitle = title.getTitle();

        return chatService.updateTitle(chat_id, newTitle);
    }
}