package com.jinhyuk.todolistapi.entity;

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "task_relation",
            joinColumns = {@JoinColumn(name = "post_task_id")},
            inverseJoinColumns = {@JoinColumn(name = "pre_task_id")})
    private List<Task> preTasks;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void addPreTask(Task preTask) {
        if (preTasks == null) {
            preTasks = new ArrayList<>();
        }
        preTasks.add(preTask);
    }
}
