package com.jinhyuk.todolistapi.dto;

import com.jinhyuk.todolistapi.entity.Task;
import com.jinhyuk.todolistapi.exception.ErrorCode;
import com.jinhyuk.todolistapi.exception.InvalidArgumentApiException;
import com.jinhyuk.todolistapi.repository.TaskRepository;
import lombok.*;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor @AllArgsConstructor
@Builder @Getter @Setter @ToString
public class TaskRequest {
    private int id;
    private String title;
    @Builder.Default
    private boolean isDone = false;
    @Builder.Default
    private Set<Integer> preTaskIds = new HashSet<>();

    public Task toEntity() {
        return Task.builder().title(title).isDone(isDone).build();
    }

    public boolean hasEmptyTitle() {
        return StringUtils.isEmpty(title);
    }

    public boolean hasItselfAsPreTask() {
        return preTaskIds != null && preTaskIds.contains(id);
    }

    public List<Task> getPreTasks(TaskRepository taskRepository) {
        return preTaskIds
                .stream()
                .map(taskRepository::findById)
                .map(task -> task.orElseThrow(() -> new InvalidArgumentApiException(ErrorCode.PRE_TASKS_NOT_FOUND)))
                .collect(Collectors.toList());
    }
}
