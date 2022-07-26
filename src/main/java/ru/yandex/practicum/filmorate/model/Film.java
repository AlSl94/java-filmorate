package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.service.MPA_RATING;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Film {
    private Long filmId;

    @NotNull(message = "Название не может быть пустым")
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания не может превышать 200 символов")
    private String description;

    @Pattern(regexp = "^\\S*$")
    private Set<MPA_RATING> genres = new HashSet<>();

    @NotBlank(message = "Рейтинг не может быть пустым")
    private String rating;

    @Positive(message = "Продолжительность фильма должна быть больше 0")
    private double duration;

    private LocalDate releaseDate;

    private Set<Long> userLikes = new HashSet<>();
}