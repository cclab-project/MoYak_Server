package study.moyak.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity (name = "chatroom")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;

    @Column(name = "chat_room")
    private String title;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT", name = "all_image")  // 명시적으로 TEXT 타입으로 지정
    private String allImage;

    @CreationTimestamp
    private Timestamp createDate;

    // 마지막 대화시간 추가해야됨

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EachPill> eachPills;

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> chatMessages;
}
