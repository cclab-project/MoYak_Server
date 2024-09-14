package study.moyak.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.moyak.chat.entity.EachPill;

@Repository
public interface EachPillRepository extends JpaRepository<EachPill, Long> {

}
