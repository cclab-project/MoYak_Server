package study.moyak.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import study.moyak.user.entity.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // User의 기본 키를 외래 키로 사용
    private User user;

    @Column
    private String title;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT", name = "all_image")  // 명시적으로 TEXT 타입으로 지정
    private String allImage;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EachPill> eachPills;

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> chatMessages;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
