package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.Collection;
@Slf4j
@Service
@Validated
public class MpaService {

    private final MpaDbStorage mpaStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaStorage) {
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
