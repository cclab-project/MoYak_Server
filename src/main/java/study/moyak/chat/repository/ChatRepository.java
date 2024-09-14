package study.moyak.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.moyak.chat.entity.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

}
