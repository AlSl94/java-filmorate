package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaStorage {

    Mpa getMpa(Long id);

    Collection<Mpa> allMpa();
}
