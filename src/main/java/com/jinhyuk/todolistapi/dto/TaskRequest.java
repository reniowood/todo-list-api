package com.jinhyuk.todolistapi.dto;

import com.jinhyuk.todolistapi.entity.Task;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Builder @Getter @ToString
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
}
