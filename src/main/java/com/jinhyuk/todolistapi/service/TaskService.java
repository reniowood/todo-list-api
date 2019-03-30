package com.jinhyuk.todolistapi.service;

import com.jinhyuk.todolistapi.dto.TaskRequest;
import com.jinhyuk.todolistapi.dto.TaskResponse;
import com.jinhyuk.todolistapi.entity.Task;
import com.jinhyuk.todolistapi.exception.ErrorCode;
import com.jinhyuk.todolistapi.exception.InvalidArgumentApiException;
import com.jinhyuk.todolistapi.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskResponse addTask(TaskRequest taskRequest) {
        if (taskRequest.hasEmptyTitle()) {
            throw new InvalidArgumentApiException(ErrorCode.TASK_TITLE_IS_EMPTY);
        }

        final Task task = createTask(taskRequest);
        final Task savedTask = taskRepository.save(task);

        return TaskResponse.from(savedTask);
    }

    private Task createTask(TaskRequest taskRequest) {
        final Task task = taskRequest.toEntity();

        taskRequest.getPreTaskIds().forEach(taskId -> taskRepository.findById(taskId).ifPresentOrElse(
                task::addPreTask,
                () -> {
                    throw new InvalidArgumentApiException(ErrorCode.PRE_TASKS_NOT_FOUND);
                }
        ));

        return task;
    }

    public TaskResponse getTask(int id) {
        final Task task = taskRepository.findById(id).orElseThrow(() -> new InvalidArgumentApiException(ErrorCode.TASK_NOT_FOUND));

        return TaskResponse.from(task);
    }

    public List<TaskResponse> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateTask(TaskRequest taskRequest) {
        if (taskRequest.hasEmptyTitle()) {
            throw new InvalidArgumentApiException(ErrorCode.TASK_TITLE_IS_EMPTY);
        }

        if (taskRequest.hasItselfAsPreTask()) {
            throw new InvalidArgumentApiException(ErrorCode.TASK_HAS_ITSELF_AS_PRE_TASK);
        }

        taskRepository.findById(taskRequest.getId()).ifPresentOrElse(
                task -> {
                    updateTask(taskRequest, task);

                    taskRepository.save(task);
                },
                () -> { throw new InvalidArgumentApiException(ErrorCode.TASK_NOT_FOUND); }
        );
    }

    private void updateTask(TaskRequest taskRequest, Task task) {
        task.setTitle(taskRequest.getTitle());
        task.setDone(taskRequest.isDone());
        task.setPreTasks(taskRequest.getPreTasks(taskRepository));
    }
}
