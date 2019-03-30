package com.jinhyuk.todolistapi.service;

import com.jinhyuk.todolistapi.dto.TaskRequest;
import com.jinhyuk.todolistapi.dto.TaskResponse;
import com.jinhyuk.todolistapi.entity.Task;
import com.jinhyuk.todolistapi.exception.InvalidArgumentApiException;
import com.jinhyuk.todolistapi.repository.TaskRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

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

    @Test
    @Transactional
    public void testGetTask() {
        // given
        final Task task = taskRepository.save(Task.builder().title("씻기").isDone(true).build());

        // when
        final TaskResponse taskResponse = taskService.getTask(task.getId());

        // then
        Assert.assertEquals(task.getTitle(), taskResponse.getTitle());
        Assert.assertTrue(taskResponse.isDone());
    }

    @Test(expected = InvalidArgumentApiException.class)
    public void testGetTaskDoesNotExist() {
        // given
        // when
        taskService.getTask(0);
    }

    @Test
    @Transactional
    @SuppressWarnings("unchecked")
    public void testGetTaskWithPreTasks() {
        // given
        final Task preTask1 = taskRepository.save(Task.builder().title("장보기").isDone(true).build());
        final Task preTask2 = taskRepository.save(Task.builder().title("밥하기").isDone(false).build());
        final Task task = taskRepository.save(Task.builder().title("저녁식사").isDone(false).preTasks(List.of(preTask1, preTask2)).build());

        // when
        final TaskResponse taskResponse = taskService.getTask(task.getId());

        // then
        Assert.assertEquals(task.getTitle(), taskResponse.getTitle());
        Assert.assertFalse(taskResponse.isDone());
        Assert.assertEquals(2, taskResponse.getPreTasks().size());
        Assert.assertThat(
                taskResponse.getPreTasks(),
                contains(
                        hasProperty("id", is(preTask1.getId())),
                        hasProperty("id", is(preTask2.getId()))
                )
        );
    }

    @Test
    @Transactional
    @SuppressWarnings("unchecked")
    public void testGetAllTasks() {
        // given
        final Task preTask1 = taskRepository.save(Task.builder().title("장보기").isDone(true).build());
        final Task preTask2 = taskRepository.save(Task.builder().title("밥하기").isDone(false).build());
        final Task task = taskRepository.save(Task.builder().title("저녁식사").isDone(false).preTasks(List.of(preTask1, preTask2)).build());

        // when
        final List<TaskResponse> taskResponses = taskService.getAllTasks(PageRequest.of(0, 10));

        // then
        Assert.assertEquals(3, taskResponses.size());
        Assert.assertThat(
                taskResponses,
                contains(
                        hasProperty("id", is(preTask1.getId())),
                        hasProperty("id", is(preTask2.getId())),
                        hasProperty("id", is(task.getId()))
                ));
    }

    @Test
    @Transactional
    public void testGetAllTasksWithPaging() {
        // given
        final int TOTAL_SIZE = 20;
        for (int i = 0; i < TOTAL_SIZE; ++i) {
            taskRepository.save(Task.builder().title(String.format("task %d", i)).build());
        }

        // when
        final int PAGE_SIZE = 7;
        final List<TaskResponse> firstPage = taskService.getAllTasks(PageRequest.of(0, PAGE_SIZE));
        final List<TaskResponse> secondPage = taskService.getAllTasks(PageRequest.of(1, PAGE_SIZE));
        final List<TaskResponse> thirdPage = taskService.getAllTasks(PageRequest.of(2, PAGE_SIZE));

        // then
        Assert.assertEquals(PAGE_SIZE, firstPage.size());
        Assert.assertEquals(PAGE_SIZE, secondPage.size());
        Assert.assertEquals(TOTAL_SIZE - 2 * PAGE_SIZE, thirdPage.size());
    }

    @Test
    @Transactional
    public void testUpdateTask() {
        // given
        final Task task = taskRepository.save(Task.builder().title("장보기").isDone(false).build());

        // when
        final TaskRequest taskRequest = TaskRequest.builder().id(task.getId()).title("요리하기").isDone(true).build();
        taskService.updateTask(taskRequest);

        // then
        final Optional<Task> taskOptional = taskRepository.findById(task.getId());
        Assert.assertTrue(taskOptional.isPresent());
        Assert.assertEquals(taskRequest.getTitle(), taskOptional.get().getTitle());
        Assert.assertEquals(taskRequest.isDone(), taskOptional.get().isDone());
    }

    @Test(expected = InvalidArgumentApiException.class)
    @Transactional
    public void testUpdateTaskDoesNotExist() {
        // given
        // when
        final TaskRequest taskRequest = TaskRequest.builder().id(-1).title("title").isDone(true).build();
        taskService.updateTask(taskRequest);
    }

    @Test(expected = InvalidArgumentApiException.class)
    @Transactional
    public void testUpdateTaskWithEmptyTitle() {
        // given
        final Task task = taskRepository.save(Task.builder().title("장보기").isDone(false).build());

        // when
        final TaskRequest taskRequest = TaskRequest.builder().id(task.getId()).isDone(true).build();
        taskService.updateTask(taskRequest);
    }

    @Test(expected = InvalidArgumentApiException.class)
    @Transactional
    public void testUpdateTaskHasItselfAsPreTask() {
        // given
        final Task task = taskRepository.save(Task.builder().title("장보기").isDone(false).build());

        // when
        final TaskRequest taskRequest = TaskRequest.builder()
                .id(task.getId())
                .title(task.getTitle())
                .isDone(true)
                .preTaskIds(Set.of(task.getId()))
                .build();
        taskService.updateTask(taskRequest);
    }

    @Test(expected = InvalidArgumentApiException.class)
    @Transactional
    public void testUpdateTaskHasInvalidPreTaskIds() {
        // given
        final Task preTask1 = taskRepository.save(Task.builder().title("장보기").isDone(true).build());
        final Task preTask2 = taskRepository.save(Task.builder().title("밥하기").isDone(false).build());
        final Task task = taskRepository.save(Task.builder().title("저녁식사").isDone(false).preTasks(List.of(preTask1, preTask2)).build());

        // when
        final TaskRequest taskRequest = TaskRequest.builder()
                .id(task.getId())
                .title(task.getTitle())
                .isDone(true)
                .preTaskIds(Set.of(preTask1.getId(), preTask2.getId(), 0, -1))
                .build();
        taskService.updateTask(taskRequest);
    }
}
