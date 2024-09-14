package study.moyak.auth.model;

import lombok.Data;

@Data
public class OAuthToken { //토큰 정보를 담을 오브젝트

    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;
    private int refresh_token_expires_in;
}
