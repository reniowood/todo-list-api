package com.jinhyuk.todolistapi.dto;

import com.jinhyuk.todolistapi.entity.Task;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString
public class TaskResponse {
    private int id;
    private String title;
    private boolean isDone;
    private List<TaskResponse> preTasks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskResponse from(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .isDone(task.isDone())
                .preTasks(task.getPreTasks().stream().map(TaskResponse::from).collect(Collectors.toList()))
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
