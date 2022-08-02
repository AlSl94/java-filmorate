package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Mpa {
    private Short id;
    private String name;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("mpa_id", id);
        values.put("mpa", name);
        return values;
    }
}
