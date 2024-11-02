package study.moyak.chat.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import study.moyak.user.repository.UserRepository;
import study.moyak.chat.service.S3Service;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final S3Service s3Service;

    private String S3_DIR = "allImage";

    @Transactional
    public ResponseEntity<?> createChat(MultipartFile allImage, String timeStamp) throws IOException {
        Chat chat = new Chat();
        chat.setTitle(timeStamp); // 처음 채팅방 생성됐을 때는 생성된 날짜로
        chatRepository.save(chat); // s3 경로를 위해 미리 저장

        File uploadFile = s3Service.convert(allImage)
                .orElseThrow(() -> new IllegalArgumentException("파일 변환에 실패하였습니다."));

        if(allImage.isEmpty()){
            return ResponseEntity.status(404).body("no Image");
        }else{
            String originalFilename = uploadFile.getName(); //원본 파일 명
            String fileName = S3_DIR + "/" + chat.getId() + "/" + UUID.randomUUID() + originalFilename;   // S3에 저장된 파일 이름
            String uploadUrl = s3Service.uploadS3(uploadFile, fileName);


            chat.setAllImage(uploadUrl);
            chatRepository.save(chat); // image 경로 설정 후, 저장

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
