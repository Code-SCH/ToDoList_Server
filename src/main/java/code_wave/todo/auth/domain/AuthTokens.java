package code_wave.todo.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokens {
    private String accessToken;
    private String refreshToken;
    //private String grantType;
    private Long expiresIn;

    public static AuthTokens of(String accessToken, String refreshToken, Long expiresIn) {
        return new AuthTokens(accessToken, refreshToken, expiresIn);
    }
}
