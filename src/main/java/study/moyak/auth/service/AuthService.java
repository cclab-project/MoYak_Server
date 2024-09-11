package study.moyak.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import study.moyak.auth.model.KakaoProfile;
import study.moyak.auth.model.OAuthToken;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${kakao.client_id}")
    private String client_id;
    @Value("${kakao.redirect_uri}")
    private String redirect_uri;


    private String KAUTH_TOKEN_URL ="https://kauth.kakao.com/oauth/token";
    private String KAUTH_USER_URL = "https://kapi.kakao.com/v2/user/me";

    //토큰을 발급 받으면 나의 서비스에서 카카오에 저장된 회원 정보에 접근 가능해짐
    public OAuthToken getAccessTokenFromKakao(String code) {

        //POST방식으로 key=value 데이터를 카카오로 요청
        //Retrofit2, OkHttp, RestTemplate

        RestTemplate rt = new RestTemplate();

        //header 정보
        HttpHeaders headers =new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"); //컨텐트 타입으로 http body데이터가 key=value형식인 것을 알려줌

        //body 정보 (MultiValueMap: 폼 데이터에 적합)
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", client_id);
        params.add("redirect_uri", redirect_uri);
        params.add("code",code);

        //header와 body를 하나의 오브젝트에 담기 -> exchange라는 함수가 HttpEntity를 받기 때문
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        //실제로 요청한 후 응답을 받는 코드
        ResponseEntity<String> response = rt.exchange(
                KAUTH_TOKEN_URL, //요청 주소
                HttpMethod.POST, //요청메서드: POST
                kakaoTokenRequest, //바디 + 헤더
                String.class //응답 받을 타입 (response는 String으로 옴)
        );

        //json 데이터를 처리할 때 필요한 라이브러리: Gson, Json Simple, ObjectMapper(기본적으로 내장 되어있으며 쉬움)등
        //json 데이터를 자바에서 처리하기 위해서 자바 오브젝트로 바꿔야함
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return oauthToken;
    }

    //토큰을 통해 카카오에 사용자 정보 요청
    public KakaoProfile getUserInfo(OAuthToken oauthToken) {

        //카카오에 사용자 정보 요청
        RestTemplate rt = new RestTemplate();

        //header 정보 (바디는 없음. 공식문서 참고)
        HttpHeaders headers =new HttpHeaders();
        headers.add("Authorization", "Bearer " + oauthToken.getAccess_token());
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(
                KAUTH_USER_URL, //요청 주소
                HttpMethod.POST, //요청메서드: POST
                kakaoProfileRequest,
                String.class //응답 받을 타입
        );

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(response .getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return kakaoProfile;
    }
}
