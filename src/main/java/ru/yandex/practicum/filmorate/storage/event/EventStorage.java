package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface EventStorage {

    void createEvent(long userId, long entityId, int eventTypeId, int operationId);
    Collection<Event> getFeedByUserId(long userId);
}
