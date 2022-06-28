package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class AfterCustomDateValidator implements ConstraintValidator<AfterCustomDate, LocalDate> {

    protected final LocalDate OLDEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate.isBefore(OLDEST_RELEASE_DATE)) {
            throw new ValidationException("Дата создания должна быть не раньше 1895-12-28");
        } else return true;
    }
}