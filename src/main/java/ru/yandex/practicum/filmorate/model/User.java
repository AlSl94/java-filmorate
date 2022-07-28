package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class User {
    private Long userId;

    @Email(message = "Неверный email")
    private String email;

    @Pattern(regexp = "^\\S*$")
    @NotNull(message = "Логин не может быть пустым")
    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    private String name;

    @Past
    private LocalDate birthday;

    private HashMap<Long, Boolean> friends = new HashMap<>();

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("email", email);
        values.put("login", login);
        values.put("name", name);
        values.put("birthday", birthday);
        values.put("friends", friends);
        return values;
    }

    public User asUser() {
        return User.builder()
                .userId(userId)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .friends(friends)
                .build();
    }

    public User fromUser(User user) {
        return User.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .friends(user.getFriends())
                .build();
    }
}
