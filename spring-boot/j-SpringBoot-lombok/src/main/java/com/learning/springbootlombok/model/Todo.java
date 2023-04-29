package com.learning.springbootlombok.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "todos")

//lombok annotations
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Todo {

    @SequenceGenerator(name = "todo_seq", allocationSize = 1, sequenceName = "todo_seq" )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "todo_seq")
    @Id
    private Long id;

    @Column(name = "title")
    private String title;


    @Column(name = "content")
    private String content;

    @Column(name = "status")
    private Boolean isDone;

}
