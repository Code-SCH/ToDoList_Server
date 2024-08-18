package code_wave.todo.auth.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuthTokensGenerator {
    private static final String BEARER = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60; // 1시간
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 7일

    private final JwtTokenProvider jwtTokenProvider;

    // jwt 토큰 발급
    public AuthTokens generate(String uid){
        long now = (new Date()).getTime();
        Date accessTokenExpiration = new Date(now + ACCESS_TOKEN_EXPIRATION_TIME);
        Date refreshTokenExpiration = new Date(now + REFRESH_TOKEN_EXPIRATION_TIME);

        String accessToken = jwtTokenProvider.accessTokenGenerate(uid, accessTokenExpiration);
        String refreshToken = jwtTokenProvider.refreshTokenGenerate(refreshTokenExpiration);

        return AuthTokens.of(accessToken, refreshToken, BEARER, ACCESS_TOKEN_EXPIRATION_TIME, REFRESH_TOKEN_EXPIRATION_TIME);
    }
}
