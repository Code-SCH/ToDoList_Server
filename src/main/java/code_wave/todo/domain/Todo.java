package code_wave.todo.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private boolean completed;
    private LocalDate date;

    /*@ManyToOne
    @JoinColumn(name = "user_id")
    private User user;*/

}
