package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;
@Service
@Validated
public class MpaService {

    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    /**
     * Получения MPA по его id
     * @param mpaId - айди
     * @return - экземпляр класса mpa
     */
    public Mpa getMpaById(Integer mpaId) {
        if (mpaId < 0 || mpaId > 5) {
            throw new WrongParameterException("mpa.id должен быть больше 0 и меньше 5");
        }
        return mpaStorage.getMpaById(mpaId);
    }

    /**
     * Получение всех MPA рейтингов
     * @return коллекция со всеми экземплярами класса Mpa
     */
    public Collection<Mpa> allMpa() {
        return mpaStorage.allMpa();
    }
}
