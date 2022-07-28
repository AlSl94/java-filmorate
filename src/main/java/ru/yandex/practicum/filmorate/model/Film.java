package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.service.MPA_RATING;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class Film {
    private Long filmId;

    @NotNull(message = "Название не может быть пустым")
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания не может превышать 200 символов")
    private String description;

    @Pattern(regexp = "^\\S*$")
    private List<String> genres;

    @NotBlank(message = "Рейтинг не может быть пустым")
    private MPA_RATING rating;

    @Positive(message = "Продолжительность фильма должна быть больше 0")
    private double duration;

    private LocalDate releaseDate;

    private List<Long> userLikes;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("rating", rating);
        values.put("duration", duration);
        values.put("release_date", releaseDate);
        return values;
    }

    public Film asFilm() {
        return Film.builder()
                .filmId(filmId)
                .name(name)
                .description(description)
                .genres(genres)
                .rating(rating)
                .duration(duration)
                .releaseDate(releaseDate)
                .userLikes(userLikes)
                .build();
    }

    public Film fromFilm(Film film) {
        return Film.builder()
                .filmId(film.getFilmId())
                .name(film.getName())
                .description(film.getDescription())
                .genres(film.getGenres())
                .rating(film.getRating())
                .duration(film.getDuration())
                .releaseDate(film.getReleaseDate())
                .userLikes(film.getUserLikes())
                .build();
    }
}