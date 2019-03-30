package com.jinhyuk.todolistapi.service;

import com.jinhyuk.todolistapi.dto.TaskRequest;
import com.jinhyuk.todolistapi.entity.Task;
import com.jinhyuk.todolistapi.exception.InvalidArgumentApiException;
import com.jinhyuk.todolistapi.repository.TaskRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskServiceTest {
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRepository taskRepository;

    @Test
    @Transactional
    public void testAddTask() {
        // given
        final String title = "청소하기";
        final TaskRequest taskRequest = TaskRequest.builder().title(title).build();

        // when
        final int id = taskService.addTask(taskRequest);

        // then
        final Optional<Task> taskOptional = taskRepository.findById(id);
        taskOptional.ifPresentOrElse(
            task -> Assert.assertEquals(title, task.getTitle()),
            () -> Assert.fail("taskOptional must not be empty")
        );
    }

    @Test(expected = InvalidArgumentApiException.class)
    public void testAddEmptyTitleTask() {
        // given
        final String emptyTitle = "";
        final TaskRequest taskRequest = TaskRequest.builder().title(emptyTitle).build();

        // when
        taskService.addTask(taskRequest);
    }


    @Test(expected = InvalidArgumentApiException.class)
    @Transactional
    public void testAddTaskWithWrongPreTaskId() {
        // given
        final Task preTask = taskRepository.save(Task.builder().isDone(true).title("씻기").build());

        final String title = "청소하기";
        final TaskRequest taskRequest = TaskRequest.builder().title(title).preTaskIds(Set.of(preTask.getId(), -1)).build();

        // when
        taskService.addTask(taskRequest);
    }
}
