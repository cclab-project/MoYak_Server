package study.moyak.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.moyak.chat.entity.EachPill;
import study.moyak.user.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface EachPillRepository extends JpaRepository<EachPill, Long> {
    List<EachPill> findByChatroomId(Long chatroom);
}
