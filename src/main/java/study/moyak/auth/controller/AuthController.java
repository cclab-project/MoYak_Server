package study.moyak.auth.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import study.moyak.LoginProvider;
import study.moyak.auth.model.KakaoProfile;
import study.moyak.auth.service.AuthService;
import study.moyak.auth.model.OAuthToken;
import study.moyak.user.entity.User;
import study.moyak.user.service.UserService;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final HttpSession session;

    @PostMapping("/kakao/callback")
    public ResponseEntity<String> kakaoCallback(@RequestParam String code) { //데이터를 리턴해주는 컨트롤러 함수

        OAuthToken oauthToken = authService.getAccessTokenFromKakao(code);


        System.out.println("카카오 액세스 토큰 = " + oauthToken.getAccess_token());


        KakaoProfile kakaoProfile = authService.getUserInfo(oauthToken);

        //User 오브젝트: username(=닉네임), email(kakao 이메일)
        System.out.println("유저네임: " + kakaoProfile.getProperties().getNickname());
        System.out.println("이메일: " + kakaoProfile.getKakao_account().getEmail());


        User user = User.builder()
                .username(kakaoProfile.getProperties().getNickname())
                .loginProvider(LoginProvider.KAKAO)
                .email(kakaoProfile.getKakao_account().getEmail()).build();

        return ResponseEntity.ok(userService.signup(user));
    }
}
