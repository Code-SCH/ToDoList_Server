package code_wave.todo.auth.controller;

import code_wave.todo.auth.domain.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("")
@RestController
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/kakao/callback")
    @ResponseBody
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam("code") String code, HttpServletRequest request) {
        return null;
    }
}
