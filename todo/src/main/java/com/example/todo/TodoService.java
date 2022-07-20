package com.example.todo;

import com.example.todo.dao.TodoEntity;
import com.example.todo.dao.TodoRepository;
import com.example.todo.exception.TodoNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;

    public List<TodoEntity> findAllTodo() {
//        return todoRepository.findAll();
        return todoRepository.findAllByOrderByCreateTimeDesc();
    }

    public void setTodo(TodoForm formData){
        TodoEntity todo = new TodoEntity();
        todo.setTitle(formData.getTitle());
        todo.setDeadline(formData.getDeadline());
        todoRepository.save(todo);
    }

    public TodoEntity findTodoById(long todoId){
        Optional<TodoEntity> todoResult = todoRepository.findById(todoId);
        todoResult.orElseThrow(TodoNotFoundException::new);
        return todoResult.get();
    }

    public void switchTodo(Long todoId){
        TodoEntity todoEntity = findTodoById(todoId);
        todoEntity.setStatus((!todoEntity.isStatus()));
        todoRepository.save(todoEntity);
    }

    //6
    public List<TodoEntity> findTodoByTitle(String searchWord){
        return todoRepository.findByTitleContaining(searchWord);
    }

    //8
    public void editTodo(TodoForm formData){
        TodoEntity todoEntity = findTodoById(formData.getId());
        todoEntity.setTitle(formData.getTitle());
        todoEntity.setDeadline(formData.getDeadline());
        todoEntity.setStatus(formData.isStatus());
        todoRepository.save(todoEntity);
    }
}