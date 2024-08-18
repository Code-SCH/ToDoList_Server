package code_wave.todo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("")
@Slf4j
public class ToDoListController {

    @GetMapping("/api/todos")
    public ResponseEntity<?> home() {


        return new ResponseEntity<>(HttpStatus.OK);
    }
}
