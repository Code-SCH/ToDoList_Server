package code_wave.todo.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {

    @Value("${kakao.key.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;
}
