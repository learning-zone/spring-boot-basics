package com.learning.springbootlombok.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//lombok annotations
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoDto {
    private String title;
    private String content;
}
