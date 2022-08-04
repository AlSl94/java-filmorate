package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    @EqualsAndHashCode.Include
    private Long id;
    @NotNull(message = "Название не может быть пустым")
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания не может превышать 200 символов")
    private String description;
    private LocalDate releaseDate;
    private Mpa mpa;
    @Positive(message = "Продолжительность фильма должна быть больше 0")
    private double duration;
    private List<Genre> genres = new ArrayList<>();
    private List<Long> userLikes = new ArrayList<>();
}