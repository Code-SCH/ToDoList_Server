package code_wave.todo.auth.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfoDto {

    //카카오에서 준 id
    @JsonProperty("id")
    public long id;

    @JsonProperty("kakao_account")
    public KakaoAccount kakaoAccount;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class KakaoAccount {
        @JsonProperty("profile")
        public Profile profile;

        @JsonProperty("email")
        public String email;

        @Getter
        public class Profile{
            @JsonProperty("nickname")
            public String nickName;
        }
    }
}
