package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Objects;

@Service
@Validated
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    /**
     * Метод для получения всех режиссеров
     * @return коллекцию с режиссерами
     */
    public Collection<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    /**
     * Метод для полечения конкретного режиссера по id
     * @param id id режиссера
     * @return режжисер
     */
    public Director findDirectorById(@Valid Integer id) {
        if (getAllDirectors().stream().noneMatch(d -> Objects.equals(id, d.getId()))) {
            throw new WrongParameterException("director.id не найден");
        }
        return directorStorage.findDirectorById(id);
    }

    /**
     * Метод для создания режиссера
     * @param director объект режиссера
     * @return режиссер
     */
    public Director create(@Valid Director director) {
        return directorStorage.create(director);
    }

    /**
     * Метод для обновления режиссера
     * @param director объект режиссера
     * @return обновленный режиссер
     */
    public Director update(@Valid Director director) {
        if (getAllDirectors().stream().noneMatch(d -> Objects.equals(director.getId(), d.getId()))) {
            throw new WrongParameterException("director.id не найден");
        }
        return directorStorage.update(director);
    }

    /**
     * Метод для удаления режиссера
     * @param id айди режиссера
     */
    public void delete(@Valid Integer id) {
        directorStorage.delete(id);
    }
}
