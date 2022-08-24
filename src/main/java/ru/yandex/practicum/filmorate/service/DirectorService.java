package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import javax.validation.Valid;
import java.util.Collection;

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
     * Метод для получения конкретного режиссера по id
     * @param id id режиссера
     * @return режиссер
     */
    public Director findDirectorById(@Valid Integer id) {
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
