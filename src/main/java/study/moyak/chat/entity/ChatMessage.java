package study.moyak.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chatroom;

    @Column(nullable = false)
    private String role; //  assistant, user

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime chatTime;

    @PrePersist
    public void prePersist() {
        this.chatTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

}
