package com.jinhyuk.todolistapi.entity;

import com.jinhyuk.todolistapi.dto.TaskRequest;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor @AllArgsConstructor
@Builder @Getter @Setter @ToString
public class Task {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "is_done")
    private boolean isDone;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "task_relation",
            joinColumns = {@JoinColumn(name = "post_task_id")},
            inverseJoinColumns = {@JoinColumn(name = "pre_task_id")})
    private List<Task> preTasks = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void addPreTask(Task preTask) {
        preTasks.add(preTask);
    }
}
