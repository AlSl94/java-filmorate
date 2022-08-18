package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "eventId")
public class Event {

    private Long eventId;

    @NotNull
    private Long userId;

    @NotNull
    private Long entityId;

    @NotNull
    @NotEmpty
    private String eventType;

    @NotNull
    @NotEmpty
    private String operation;

    @NotNull
    private long timestamp;
}