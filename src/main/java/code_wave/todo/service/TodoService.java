package code_wave.todo.service;

import code_wave.todo.domain.Todo;
import code_wave.todo.repository.TodoRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
public class TodoService {

    private final TodoRepository todoRepository;
    Todo todo = new Todo();

    @Autowired
    public TodoService(TodoRepository todoRepository){this.todoRepository = todoRepository;}
}
