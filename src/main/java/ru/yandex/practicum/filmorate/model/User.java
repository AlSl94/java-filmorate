package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class User {
    private int id;

    @NotBlank(message = "Адрес электронной почты не может быть пустым.")
    @NotNull(message = "Адрес электронной почты не может быть пустым.")
    @Email(message = "Неверный email")
    private String email;

    @NotNull(message = "Логин не может быть пустым")
    @NotEmpty(message = "Логин не может быть пустым")
    private String login;

    private String name;
    private LocalDate birthday;
}
