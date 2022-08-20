package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "reviewId")
public class Review {
    private Long reviewId;
    @NotNull(message = "Отзыв не может быть пустым")
    @NotBlank(message = "Отзыв не может быть пустым")
    private String content;
    @NotNull(message = "Не указано, позитивный или негативный отзыв")
    private Boolean isPositive;
    @NotNull(message = "Поле id пользователя не может быть пустым")
    private Long userId;
    @NotNull(message = "Поле id фильма не может быть пустым")
    private Long filmId;
    private Integer useful;

    public Review(Long reviewId, String content, Boolean isPositive, Long userId, Long filmId) {
        this.reviewId = reviewId;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
    }
}
