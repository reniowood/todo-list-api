package com.jinhyuk.todolistapi.dto;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor @AllArgsConstructor
@Getter @ToString
public class TaskResponses {
    private List<TaskResponse> tasks;
}
