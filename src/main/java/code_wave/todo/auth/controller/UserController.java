package code_wave.todo.auth.controller;

import code_wave.todo.auth.domain.LoginResponse;
import code_wave.todo.auth.service.KakaoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RequestMapping("")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final KakaoService kakaoService;

    @GetMapping("/kakao/callback")
    @ResponseBody
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam("code") String code, HttpServletRequest request) {
        try{
            return ResponseEntity.ok(kakaoService.kakaoLogin(code));
        }catch(NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
