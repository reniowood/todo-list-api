package com.jinhyuk.todolistapi.repository;

import com.jinhyuk.todolistapi.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Integer> {
}
