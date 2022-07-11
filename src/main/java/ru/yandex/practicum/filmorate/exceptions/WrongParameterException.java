package ru.yandex.practicum.filmorate.exceptions;

public class WrongParameterException extends RuntimeException{

    public WrongParameterException(String s) {
        super(s);
    }
}
