package study.moyak.chat.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import study.moyak.chat.dto.EachPillDTO;
import study.moyak.chat.dto.response.ChatMessageDTO;
import study.moyak.chat.dto.response.ChatResponseDTO;
import study.moyak.chat.entity.Chat;
import study.moyak.chat.repository.ChatMessageRepository;
import study.moyak.chat.repository.ChatRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ResponseEntity<?> createChat(MultipartFile allImage) throws IOException {
        Chat chat = new Chat();
        chat.setAllImage(allImage.getOriginalFilename());
        chat.setTitle(LocalDateTime.now().toString()); // 처음 채팅방 생성됐을 때는 생성된 날짜로

        if(allImage.isEmpty()){
            return ResponseEntity.status(404).body("no Image");
        }else{
            //이미지 Base64 인코딩
            String base64Data = Base64.getEncoder().encodeToString(allImage.getBytes());
            chat.setAllImage(base64Data);

            chatRepository.save(chat);

            return ResponseEntity.ok(chat.getId());
        }

    }

    // 채팅 내역 불러올 때, chat_id에 해당하는 eachpill에 있는 것들 + 채팅내역 필요
    @Transactional
    public ResponseEntity<?> getChat(Long chat_id) throws IOException {
        Chat chat = chatRepository.findById(chat_id)
                .orElseThrow(() -> new FileNotFoundException("채팅방을 찾을 수 없습니다."));

        // chat_id에 해당하는 eachpill 불러오기
        List<EachPillDTO> eachPills = chat.getEachPills().stream()
                .map(pill -> new EachPillDTO(pill.getImage(), pill.getPillName(), pill.getPillIngredient()))
                .collect(Collectors.toList());

        List<ChatMessageDTO> chatMessages = chat.getChatMessages().stream()
                .map(message -> new ChatMessageDTO(message.getRole(), message.getContent()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ChatResponseDTO(eachPills, chatMessages));
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
