package com.example.todo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {
    //6
    List<TodoEntity> findByTitleContaining(@Param("searchWord") String searchWord);

    //9
    List<TodoEntity> findAllByOrderByCreateTimeDesc();
}
