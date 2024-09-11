package study.moyak;

public enum LoginProvider {

    KAKAO("KAKAO"), GOOGLE("GOOGLE");

    private String value;

    LoginProvider(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
