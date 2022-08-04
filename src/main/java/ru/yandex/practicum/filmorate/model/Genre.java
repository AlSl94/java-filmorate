package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Genre {

    @NotNull
    Integer id;
    String name;
}
