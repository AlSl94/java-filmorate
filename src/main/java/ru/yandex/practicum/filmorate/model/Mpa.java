package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Mpa {
    private Short id;
    private String name;
}
