package code_wave.todo.controller;

import code_wave.todo.oauth.domain.LoginResponse;
import code_wave.todo.oauth.service.KakaoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class UserController {

    private final KakaoService kakaoService;

    @ResponseBody
    @GetMapping("/kakao/callback")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam String code, HttpServletRequest request){

        try{
            return ResponseEntity.ok(kakaoService.kakaoLogin(code));
        }catch(NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
