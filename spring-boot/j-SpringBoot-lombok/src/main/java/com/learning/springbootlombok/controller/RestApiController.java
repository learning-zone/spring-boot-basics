package com.learning.springbootlombok.controller;


import com.learning.springbootlombok.dto.TodoDto;
import com.learning.springbootlombok.model.Todo;
import com.learning.springbootlombok.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/todos")
public class RestApiController {

    private final TodoRepository todoRepository;

    @Autowired
    public RestApiController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping("/")
    public ResponseEntity<List<Todo>> getAllTodos() {
        return ResponseEntity.ok(todoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable String id) {
        Optional<Todo> todo = todoRepository.findById(Long.parseLong(id));

        if (todo.isEmpty())
            throw new RuntimeException("Invalid todo id passed");

        return ResponseEntity.ok(todo.get());
    }


    @PostMapping("/new")
    public ResponseEntity<Todo> createTodo(@RequestBody TodoDto todoDto) {
        Todo todo = Todo.builder()
                .title(todoDto.getTitle())
                .content(todoDto.getContent())
                .isDone(false)
                .build();

        return new ResponseEntity<>(todoRepository.save(todo), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateTodo(@PathVariable String id, @RequestBody Boolean isDone) {

        Optional<Todo> todo = todoRepository.findById(Long.parseLong(id));

        if (todo.isEmpty())
            throw new RuntimeException("Invalid todo id passed");

        Todo todo1 = todo.get();

        todo1.setIsDone(isDone);

        todoRepository.save(todo1);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteteTodo(@PathVariable String id) {

        Optional<Todo> todo = todoRepository.findById(Long.parseLong(id));

        if (todo.isEmpty())
            throw new RuntimeException("Invalid todo id passed");
        Todo todo1 = todo.get();
        todoRepository.delete(todo1);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
