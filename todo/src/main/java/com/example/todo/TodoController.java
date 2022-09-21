package com.example.todo;

import com.example.todo.dao.TodoEntity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller //①
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/top") //②
    public String top(Model model) {
        List<TodoEntity> todoEntityList = todoService.findAllTodo();
        model.addAttribute("todoList", todoEntityList);
        return "top"; //③
    }

    @PostMapping("/register")
    public String register(@Validated @ModelAttribute TodoForm formDate, BindingResult error, RedirectAttributes attributes) {
        if (error.hasErrors()) {
            attributes.addFlashAttribute("errorMessages", error);
            return "redirect:/top";
        }
        todoService.setTodo(formDate);
        return "redirect:/top";
    }

    @PatchMapping("/toggle-status/{id}")
    public String switchStatus(@PathVariable("id") Long todoId) {
        todoService.switchTodo(todoId);
        return "redirect:/top";
    }

    //6
    @GetMapping("/search")
    public String showSearch() {
        return "search";
    }

    //6
    @GetMapping("/search/result")
    public String searchResult(Model model, @ModelAttribute TodoSearchForm searchForm) {
        List<TodoEntity> searchResult = todoService.findTodoByTitle(searchForm.getSearchWord());
        model.addAttribute("searchResults", searchResult);
        return "search";
    }

    //8
    @GetMapping("/edit/{id}")
    public String showEdit(@NotNull Model model, @PathVariable("id") long id) {
        TodoEntity editTarget = todoService.findTodoById(id);
        model.addAttribute("editTarget", editTarget);
        return "edit";
    }

    @PutMapping("/edit/{id}/complete")
    public String edit(@ModelAttribute TodoForm formData, @PathVariable("id") long id) {
        todoService.editTodo(formData);
        return "redirect:/top";
    }

    //目標③
    @GetMapping("/excel")
    public String excel(Model model) {
        List<TodoEntity> todoEntityList = todoService.findAllTodo();
        model.addAttribute("todoList", todoEntityList);
        return "excel";
    }

    @PostMapping("/excel/register")
    public String excelRegister(@Validated @ModelAttribute TodoExcelForm filePath, BindingResult error, RedirectAttributes attributes) {
        if (error.hasErrors()) {
            attributes.addFlashAttribute("errorMessages", error);
            return "redirect:/excel";
        }

        //Excel読込
        ArrayList<TodoForm> excelList = todoService.getExcel(filePath, error);
        if (error.hasErrors()) {
            attributes.addFlashAttribute("errorMessages", error);
            return "redirect:/excel";
        }

        //Todo登録（Excel）
        todoService.setTodoExcel(excelList);

        return "redirect:/excel";
    }
}
