package study.moyak.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.moyak.chat.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
