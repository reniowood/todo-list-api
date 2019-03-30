package com.jinhyuk.todolistapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinhyuk.todolistapi.dto.TaskRequest;
import com.jinhyuk.todolistapi.dto.TaskResponse;
import com.jinhyuk.todolistapi.dto.TaskResponses;
import com.jinhyuk.todolistapi.entity.Task;
import com.jinhyuk.todolistapi.repository.TaskRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @Transactional
    @SuppressWarnings("unchecked")
    public void testGetTasks() throws Exception {
        // given
        final Task task1 = taskRepository.save(Task.builder().title("title1").build());
        final Task task2 = taskRepository.save(Task.builder().title("title2").build());
        final Task task3 = taskRepository.save(Task.builder().title("title3").build());

        // when
        MvcResult result = mockMvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();

        // then
        TaskResponses response = objectMapper.readValue(result.getResponse().getContentAsString(), TaskResponses.class);
        List<TaskResponse> tasks = response.getTasks();
        Assert.assertEquals(3, tasks.size());
        Assert.assertThat(
                tasks,
                contains(
                        hasProperty("id", is(task1.getId())),
                        hasProperty("id", is(task2.getId())),
                        hasProperty("id", is(task3.getId()))
                )
        );
    }

    @Test
    @Transactional
    public void testAddTask() throws Exception {
        // given
        final TaskRequest taskRequest = TaskRequest.builder().title("title1").isDone(true).build();

        // when
        MvcResult result = mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        TaskResponse taskResponse = objectMapper.readValue(result.getResponse().getContentAsString(), TaskResponse.class);
        Assert.assertEquals(taskRequest.getTitle(), taskResponse.getTitle());
        Assert.assertEquals(taskRequest.isDone(), taskResponse.isDone());
    }

    @Test
    @Transactional
    public void testGetTask() throws Exception {
        // given
        final Task task = taskRepository.save(Task.builder().title("title1").isDone(false).build());

        // when
        MvcResult result = mockMvc.perform(get(String.format("/api/tasks/%d", task.getId()))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();

        // then
        TaskResponse taskResponse = objectMapper.readValue(result.getResponse().getContentAsString(), TaskResponse.class);
        Assert.assertEquals(task.getId(), taskResponse.getId());
        Assert.assertEquals(task.getTitle(), taskResponse.getTitle());
        Assert.assertEquals(task.isDone(), taskResponse.isDone());
    }

    @Test
    @Transactional
    public void testUpdateTask() throws Exception {
        // given
        final Task task = taskRepository.save(Task.builder().title("title1").isDone(false).build());

        // when
        final TaskRequest taskRequest = TaskRequest.builder()
                .id(task.getId())
                .title(task.getTitle())
                .isDone(true)
                .build();

        mockMvc.perform(put(String.format("/api/tasks/%d", task.getId()))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isOk());

        // then
        final Optional<Task> taskOptional = taskRepository.findById(task.getId());
        Assert.assertTrue(taskOptional.isPresent());

        final Task updatedTask = taskOptional.get();
        Assert.assertEquals(taskRequest.getTitle(), updatedTask.getTitle());
        Assert.assertEquals(taskRequest.isDone(), updatedTask.isDone());
    }
}
