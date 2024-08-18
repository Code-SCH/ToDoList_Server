package code_wave.todo.auth.controller;

import code_wave.todo.auth.domain.KakaoUserInfoDto;
import code_wave.todo.auth.service.KakaoService;
import code_wave.todo.domain.Member;
import code_wave.todo.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {

    private final KakaoService kakaoService;

    @Autowired
    private MemberRepository memberRepository;

    @PostMapping("/kakao/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) throws IOException {

        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        KakaoUserInfoDto userInfo = kakaoService.getUserInfo(accessToken);

        String userId = String.valueOf(userInfo.getId());
        String userEmail = userInfo.getKakaoAccount().getEmail();
        String userName = userInfo.getKakaoAccount().getProfile().getNickName();

        Member member = new Member(userEmail, userName, userId);

        if(memberRepository.findBySnsId(userId).isEmpty()) {
            System.out.println("이미 가입함");
            return new ResponseEntity<>(HttpStatus.OK);
        }else{
            memberRepository.save(member);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PostMapping("/test")
    public ResponseEntity<?> test() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}