package com.jinhyuk.todolistapi.repository;

import com.jinhyuk.todolistapi.entity.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskRepositoryTest {
    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void testSave() {
        // given
        final String title = "청소하기";
        final Task task = Task.builder().title(title).isDone(false).build();
        final Task savedTask = taskRepository.save(task);

        // when
        final Optional<Task> taskOptional = taskRepository.findById(savedTask.getId());

        // then
        Assert.assertTrue(taskOptional.isPresent());
        final Task foundTask = taskOptional.get();
        Assert.assertEquals(title, foundTask.getTitle());
        Assert.assertFalse(foundTask.isDone());
        Assert.assertNotNull(foundTask.getCreatedAt());
        Assert.assertNotNull(foundTask.getUpdatedAt());
    }

    @Test
    public void testManyToMany() {
        // given
        final Task preTask1 = taskRepository.save(Task.builder().title("손씻기").isDone(false).build());
        final Task preTask2 = taskRepository.save(Task.builder().title("요리하기").isDone(false).build());
        final Task task = Task.builder().title("식사하기").isDone(false).build();
        task.addPreTask(preTask1);
        task.addPreTask(preTask2);
        taskRepository.save(task);

        // when
        final Optional<Task> taskOptional = taskRepository.findById(task.getId());

        // then
        Assert.assertTrue(taskOptional.isPresent());
        final Task foundTask = taskOptional.get();
        Assert.assertFalse(foundTask.getPreTasks().isEmpty());
        Assert.assertEquals(2, foundTask.getPreTasks().size());
    }
}
