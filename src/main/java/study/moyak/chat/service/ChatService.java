package study.moyak.chat.service;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import study.moyak.chat.dto.EachPillDTO;
import study.moyak.chat.dto.request.CreateChatRequestDTO;
import study.moyak.chat.dto.response.ChatListDTO;
import study.moyak.chat.dto.response.ChatMessageDTO;
import study.moyak.chat.dto.response.ChatResponseDTO;
import study.moyak.chat.entity.Chat;
import study.moyak.chat.entity.EachPill;
import study.moyak.chat.repository.ChatRepository;
import study.moyak.user.entity.User;
import study.moyak.user.repository.UserRepository;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    // private String S3_DIR = "allImage";

    @Transactional
    public ResponseEntity<List<ChatListDTO>> chatList(Long userId) {
        // userId로 Chat 목록 조회
        List<Chat> chats = chatRepository.findByUserId(userId);

        // List<Chat> -> List<ChatListDTO> 변환
        List<ChatListDTO> chatListDTOs = chats.stream()
                .map(this::convertToChatListDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(chatListDTOs);
    }

    private ChatListDTO convertToChatListDTO(Chat chat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println("시간 : "+ formatter.format(chat.getCreatedAt()));

        // Chat -> ChatListDTO 변환 (chat_id 추가)
        return ChatListDTO.builder()
                .chat_id(chat.getId())
                .allImage(chat.getAllImage())
                .title(chat.getTitle())
                .pillName(extractPillNames(chat))
                .createdAt(chat.getCreatedAt().format(formatter))
                .build();
    }

    private List<String> extractPillNames(Chat chat) {
        // 각 Chat의 EachPill에서 pillName을 추출
        return chat.getEachPills().stream()
                .map(EachPill::getPillName)
                .collect(Collectors.toList());
    }

    // createDate와 chatId를 보내주세요
    @Transactional
    public ResponseEntity<Long> createChat(CreateChatRequestDTO createChatRequestDTO) throws IOException {
        Chat chat = new Chat();

        // 로그인한 사용자 조회
        User user = userRepository.findById(createChatRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        chat.setTitle(createChatRequestDTO.getTimeStamp()); // 처음 채팅방 생성됐을 때는 생성된 날짜로
        chat.setAllImage(createChatRequestDTO.getAll_image_url()); // 이미지 경로 저장
        chat.setUser(user);

        chatRepository.save(chat);

        return ResponseEntity.ok(chat.getId());
    }

    // 채팅 내역 불러올 때, chat_id에 해당하는 eachpill에 있는 것들 + 채팅내역 + 채팅방 제목 필요
    @Transactional
    public ResponseEntity<?> getChat(Long chat_id) throws IOException {
        Chat chat = chatRepository.findById(chat_id)
                .orElseThrow(() -> new FileNotFoundException("채팅방을 찾을 수 없습니다."));

        // tilte 가져오기
        String title = chat.getTitle();

        // chat_id에 해당하는 eachpill 불러오기
        List<EachPillDTO> eachPills = chat.getEachPills().stream()
                .map(pill -> new EachPillDTO(pill.getImage(), pill.getPillName(), pill.getPillIngredient()))
                .collect(Collectors.toList());

        List<ChatMessageDTO> chatMessages = chat.getChatMessages().stream()
                .map(message -> new ChatMessageDTO(message.getRole(), message.getContent()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ChatResponseDTO(eachPills, chatMessages, title));
    }

    @Transactional
    public ResponseEntity<String> deleteChat(Long chat_id) {
        if (chat_id == null) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다. 채팅방 ID가 필요합니다."); // 400 Bad Request
        }

        try {
            Chat chat = chatRepository.findById(chat_id).orElseThrow(
                    () -> new EntityNotFoundException("채팅방을 찾을 수 없습니다.")
            );
            chatRepository.delete(chat);
            return ResponseEntity.ok().body("채팅방 삭제 완료"); // 200 OK
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 채팅방을 찾을 수 없습니다."); // 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류가 발생했습니다."); // 500 Internal Server Error
        }
    }

    @Transactional
    public ResponseEntity<?> updateTitle(Long chat_id, String title) throws IOException {
        // 채팅방 조회, 없으면 예외 처리
        Chat chat = chatRepository.findById(chat_id).orElseThrow(
                () -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));

        // 채팅방 제목 수정
        chat.setTitle(title);

        // 변경 사항 저장
        chatRepository.save(chat);

        return ResponseEntity.ok().body("채팅방 제목 수정 완료");
    }
}
