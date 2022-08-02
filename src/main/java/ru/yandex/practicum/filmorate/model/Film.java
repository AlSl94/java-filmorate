package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    @Positive
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Название не может быть пустым")
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания не может превышать 200 символов")
    private String description;

    private LocalDate releaseDate;

    private List<Genre> genres;
    private Mpa mpa;

    @Positive(message = "Продолжительность фильма должна быть больше 0")
    private double duration;

    private List<Long> userLikes;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("mpa_rating", mpa);
        values.put("duration", duration);
        values.put("release_date", releaseDate);
        return values;
    }

    public Film asFilm() {
        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .genres(genres)
                .mpa(mpa)
                .duration(duration)
                .userLikes(userLikes)
                .build();
    }

    public Film fromFilm(Film film) {
        return Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .genres(film.getGenres())
                .mpa(film.getMpa())
                .duration(film.getDuration())
                .userLikes(film.getUserLikes())
                .build();
    }
}