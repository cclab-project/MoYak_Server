package study.moyak.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EachPill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")  // 명시적으로 TEXT 타입으로 지정
    private String image;

    @Column(name = "pill_name")
    private String pillName;

    @Column(name = "pill_ingredient")
    private String pillIngredient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chatroom;
}
