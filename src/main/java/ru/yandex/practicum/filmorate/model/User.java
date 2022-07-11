package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;

    @Email(message = "Неверный email")
    private String email;

    @Pattern(regexp = "^\\S*$")
    @NotNull(message = "Логин не может быть пустым")
    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    private String name;

    @Past
    private LocalDate birthday;

    private Set<Integer> friends = new HashSet<>();
}
