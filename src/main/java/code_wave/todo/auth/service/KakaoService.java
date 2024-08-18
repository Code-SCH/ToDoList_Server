package code_wave.todo.auth.service;

import code_wave.todo.auth.domain.AuthTokens;
import code_wave.todo.auth.domain.AuthTokensGenerator;
import code_wave.todo.auth.domain.JwtTokenProvider;
import code_wave.todo.auth.domain.LoginResponse;
import code_wave.todo.domain.User;
import code_wave.todo.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final UserRepository userRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.key.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;


    private String getAccessToken(String code) {

        // POST방식으로 key=value 데이트 요청 (카카오로)
        RestTemplate rt = new RestTemplate();

        // 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        // 바디 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "8d1db208851a60f146c9763bfa9112c5");
        params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback");
        params.add("code", code);

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        // Http 요청하기 - Post 방식으로 - 그리고 response 변수의 응답을 받음
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        String responseBody = response.getBody();
        // json 데이터를 처리하기 위한 라이브러리: Gson, Json Simple, ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try{
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return jsonNode.get("access_token").asText(); // 토큰 전송
    }

    public LoginResponse kakaoLogin(String code) {
        String accessToken = getAccessToken(code);

        HashMap<String, Object> userInfo = getKakaoUserInfo(accessToken);

        LoginResponse kakaoUserResponse = kakaoUserLogin(userInfo);

        return kakaoUserResponse;
    }

    private HashMap<String, Object> getKakaoUserInfo(String accessToken) {
        HashMap<String, Object> userInfo = new HashMap<>();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try{
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String nickname = jsonNode.get("properties").get("nickname").asText();

        userInfo.put("id", id);
        userInfo.put("email", email);
        userInfo.put("nickname", nickname);
        ;

        return userInfo;
    }

    private LoginResponse kakaoUserLogin(HashMap<String, Object> userInfo) {

        Long uid = Long.valueOf(userInfo.get("id").toString());
        String email = userInfo.get("email").toString();
        String nickname = userInfo.get("nickname").toString();

        User kakaouser = userRepository.findByEmail(email).orElse(null);
        log.info(kakaouser.toString());

        if(kakaouser == null) {
            kakaouser = new User();
            kakaouser.setId(uid);
            kakaouser.setEmail(email);
            kakaouser.setNickname(nickname);
            userRepository.save(kakaouser);
        }

        AuthTokens token = authTokensGenerator.generate(uid.toString());
        return new LoginResponse(uid, email, nickname, token);
    }
}
