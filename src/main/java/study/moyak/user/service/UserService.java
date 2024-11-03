package study.moyak.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.moyak.user.dto.UserDTO;
import study.moyak.user.entity.User;
import study.moyak.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDTO signup(User user) {

        UserDTO userDTO = new UserDTO();
        User existUser = findUser(user.getEmail());

        if (existUser != null) {
            userDTO.setUserId(existUser.getId());
            System.out.println(userDTO.getUserId());
            return userDTO;
        }

        userRepository.save(user);
        userDTO.setUserId(user.getId());

        return userDTO;
    }

    @Transactional
    // 가입자 혹은 비가입자 체크 처리
    public User findUser(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

}
