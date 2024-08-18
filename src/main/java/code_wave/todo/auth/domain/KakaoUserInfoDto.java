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

//        @JsonProperty("profile_needs_agreement")
//        private boolean isProfileAgree;
//
//        @JsonProperty("profile_nickname_needs_agreement")
//        public boolean isNicknameAgree;

        @JsonProperty("profile")
        public Profile profile;

//        @JsonProperty("email_needs_agreement")
//        public boolean isEmailAgree;
//
//        @JsonProperty("is_email_verified")
//        public boolean isEmailVerified;

        @JsonProperty("email")
        public String email;

        @Getter
        public class Profile{
            @JsonProperty("nickname")
            public String nickName;
        }
    }
}
