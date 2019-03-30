package com.jinhyuk.todolistapi.controller;

import com.jinhyuk.todolistapi.dto.TaskRequest;
import com.jinhyuk.todolistapi.dto.TaskResponse;
import com.jinhyuk.todolistapi.dto.TaskResponses;
import com.jinhyuk.todolistapi.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/tasks")
    public TaskResponses getTasks(Pageable pageable) {
        return TaskResponses.builder().tasks(taskService.getAllTasks(pageable)).build();
    }

    @PostMapping("/tasks")
    public TaskResponse addTask(@RequestBody TaskRequest taskRequest) {
        return taskService.addTask(taskRequest);
    }

    @GetMapping("/tasks/{id}")
    public TaskResponse getTask(@PathVariable("id") int id) {
        return taskService.getTask(id);
    }

    @PutMapping("/tasks/{id}")
    public void updateTask(@PathVariable("id") int id, @RequestBody TaskRequest taskRequest) {
        taskRequest.setId(id);
        taskService.updateTask(taskRequest);
    }
}
