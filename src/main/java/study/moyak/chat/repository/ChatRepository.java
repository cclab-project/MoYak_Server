package study.moyak.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.moyak.chat.entity.Chat;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    // 특정 User의 Chat 목록을 조회하는 메소드
    List<Chat> findByUserId(Long userId);
}
