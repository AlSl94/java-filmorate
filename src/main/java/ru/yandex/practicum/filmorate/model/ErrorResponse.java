package ru.yandex.practicum.filmorate.model;

public class ErrorResponse {
    String error; // название ошибки
    String description; // подробное описание

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }  // геттеры необходимы, чтобы Spring Boot мог получить значения полей
    public String getDescription() {
        return description;
    }
}