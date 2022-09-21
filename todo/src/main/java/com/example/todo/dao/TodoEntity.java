package com.example.todo.dao;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="qiita_todo")
public class TodoEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title")
    private String title;

    @Column(name="deadline")
    private LocalDate deadline;

    @Column(name="status")
    private boolean status;

    @CreationTimestamp
    @Column(name="create_time")
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name="update_time")
    private LocalDateTime updateTime;
}
