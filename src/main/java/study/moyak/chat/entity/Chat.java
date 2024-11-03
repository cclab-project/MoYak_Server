package study.moyak.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import study.moyak.user.entity.User;

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

    // private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // User의 기본 키를 외래 키로 사용
    private User user;

    @Column
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
