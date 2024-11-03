package study.moyak.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.moyak.user.entity.User;
import study.moyak.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User signup(User user) {
        if (findUser(user.getEmail()) != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
        return userRepository.save(user);
    }

    @Transactional
    // 가입자 혹은 비가입자 체크 처리
    public User findUser(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

}
