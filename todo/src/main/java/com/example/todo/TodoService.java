package com.example.todo;

import com.example.todo.dao.TodoEntity;
import com.example.todo.dao.TodoRepository;
import com.example.todo.exception.TodoNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
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

    public void setTodo(@NotNull TodoForm formData) {
        TodoEntity todo = new TodoEntity();
        todo.setTitle(formData.getTitle());
        todo.setDeadline(formData.getDeadline());
        todoRepository.save(todo);
    }

    public TodoEntity findTodoById(long todoId) {
        Optional<TodoEntity> todoResult = todoRepository.findById(todoId);
        todoResult.orElseThrow(TodoNotFoundException::new);
        return todoResult.get();
    }

    public void switchTodo(Long todoId) {
        TodoEntity todoEntity = findTodoById(todoId);
        todoEntity.setStatus((!todoEntity.isStatus()));
        todoRepository.save(todoEntity);
    }

    //6
    public List<TodoEntity> findTodoByTitle(String searchWord) {
        return todoRepository.findByTitleContaining(searchWord);
    }

    //8
    public void editTodo(TodoForm formData) {
        TodoEntity todoEntity = findTodoById(formData.getId());
        todoEntity.setTitle(formData.getTitle());
        todoEntity.setDeadline(formData.getDeadline());
        todoEntity.setStatus(formData.isStatus());
        todoRepository.save(todoEntity);
    }

    //目標③ Excel読込
    public ArrayList<TodoForm> getExcel(TodoExcelForm filePath, BindingResult error) throws EncryptedDocumentException, IOException{

        TodoEntity todo = new TodoEntity();
        ArrayList<TodoForm> excelList = new ArrayList<>();

        String errorMsg = null;

        //読込対象のExcelパス
        String path = filePath.getFilePath();

        //Excelのインスタンスを作成
        Workbook workbook = null;

        try {
            workbook = WorkbookFactory.create((new File(path)));
        } catch (FileNotFoundException e) {
            //ファイルの存在チェックエラー（ファイルが存在しない場合、エラー）
            errorMsg = "ファイルが存在しません。";
            FieldError fieldError = new FieldError(error.getObjectName(), "filePath", errorMsg);
            error.addError(fieldError);
            return excelList;
        } catch (Exception e){
            //その他エラー（ファイルを読み込めない場合、エラー）
            errorMsg = "ファイル形式を「xlsx」または「xls」にしてください。";
            FieldError fieldError = new FieldError(error.getObjectName(), "filePath", errorMsg);
            error.addError(fieldError);
            return excelList;
        }

        //シートを指定
        Sheet sheet = null;
        sheet = workbook.getSheet("INPUT_DATA");

        if (sheet == null) {
            //シートの形式チェック
            errorMsg = "シート名を「INPUT_DATA」にしてください。";
            FieldError fieldError = new FieldError(error.getObjectName(), "filePath", errorMsg);
            error.addError(fieldError);
            return excelList;
        }

        //行数を取得※空行を含む可能性あり
        int rows = sheet.getPhysicalNumberOfRows();

        //ファイルデータの必須チェック（項目行以降がNULLの場合、エラー）
        if(rows < 2){
            errorMsg = "データを入力してください。";
            FieldError fieldError = new FieldError(error.getObjectName(), "filePath", errorMsg);
            error.addError(fieldError);
            return excelList;
        }

        for (int i = 1; i <= rows; i++) {
            //行を取得
            Row row = sheet.getRow(i);

            //空の場合：処理終了
            if (row == null) {
                break;
            }

            //セルに入力されている値を取得（タイトル）
            Cell cellTitle = row.getCell(0);
            String title = null;

            //タイトルの必須チェック
            if (cellTitle == null || cellTitle.getCellType() == CellType.BLANK) {
                //期日の必須チェック(空の場合、エラー)
                CellReference ref = new CellReference(i, 0);
                errorMsg = ref.formatAsString() + "セル:" + "「title」を入力してください。";
                FieldError fieldError = new FieldError(error.getObjectName(), "filePath", errorMsg);
                error.addError(fieldError);
                return excelList;
            }

            //タイトルの桁数チェック
            title = cellTitle.getStringCellValue();
            if (1 > title.length() || 30 < title.length()) {
                CellReference ref = new CellReference(i, 0);
                errorMsg = ref.formatAsString()+"セル:"+"「title」を1 から 30 の間のサイズにしてください。";
                FieldError fieldError = new FieldError(error.getObjectName(), "filePath", errorMsg);
                error.addError(fieldError);
                return excelList;
            }

            //セルに入力されている値を取得（期日）
            Cell cellDeadline = row.getCell(1);
            String deadlineSt = null;
            LocalDate deadlineLd = null;

            //期日の必須チェック
            if (cellDeadline == null || cellDeadline.getCellType() == CellType.BLANK) {
                //期日の必須チェック(空の場合、エラー)
                CellReference ref = new CellReference(i, 1);
                errorMsg = ref.formatAsString() + "セル:" + "「deadline」を入力してください。";
                FieldError fieldError = new FieldError(error.getObjectName(), "filePath", errorMsg);
                error.addError(fieldError);
                return excelList;
            }

            //期日の形式チェック
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            try {
                //期日が日付型である場合、後続処理へ
                deadlineLd = LocalDate.from(cellDeadline.getLocalDateTimeCellValue());
            }catch (IllegalStateException e) {
                //期日が日付型でない場合
                try {
                    //期日が"yyyy-MM-dd"形式である場合、後続処理へ
                    deadlineSt = cellDeadline.getStringCellValue();
                    deadlineLd = LocalDate.parse(deadlineSt, dtf);
                } catch (Exception e1) {
                    //期日が"yyyy-MM-dd"形式で無い場合、エラー
                    CellReference ref = new CellReference(i, 1);
                    errorMsg = ref.formatAsString()+"セル:"+"「deadline」を日付形式(yyyy-MM-dd)にしてください。";
                    FieldError fieldError = new FieldError(error.getObjectName(), "filePath", errorMsg);
                    error.addError(fieldError);
                    return excelList;
                }
            }

            //Excelから取得したデータを格納する
            TodoForm inputData = new TodoForm();
            inputData.setTitle(title);
            inputData.setDeadline(deadlineLd);
            excelList.add(inputData);
        }
        return excelList;
    }

    public void setTodoExcel(ArrayList<TodoForm> excelList) {
        int excelListLen = excelList.size();
        for (int i = 1; i <= excelListLen; i++) {
            setTodo(excelList.get(i - 1));
        }
    }
}