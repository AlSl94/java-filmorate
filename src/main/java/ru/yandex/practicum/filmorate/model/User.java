package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashMap;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class User {
    private Long userId;

    @Email(message = "Неверный email")
    private String email;

    @Pattern(regexp = "^\\S*$")
    @NotNull(message = "Логин не может быть пустым")
    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    private String name;

    @Past
    private LocalDate birthday;

    private HashMap<Long, Boolean> friends = new HashMap<>();

}
