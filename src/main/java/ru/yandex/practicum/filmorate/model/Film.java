package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.validation.AfterCustomDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class Film {
    private int id;

    @NotNull(message = "Название не может быть пустым")
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания не может превышать 200 символов")
    private String description;

    @Positive(message = "Продолжительность фильма должна быть больше 0")
    private double duration;

    @AfterCustomDate
    private LocalDate releaseDate;

    private Set<Long> userLikes = new HashSet<>();
}