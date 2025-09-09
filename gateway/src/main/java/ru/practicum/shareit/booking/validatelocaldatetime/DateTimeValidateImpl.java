package ru.practicum.shareit.booking.validatelocaldatetime;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.BookItemRequestDto;

import java.time.LocalDateTime;

public class DateTimeValidateImpl implements ConstraintValidator<DateTimeValidate, BookItemRequestDto> {
    @Override
    public boolean isValid(BookItemRequestDto bookItemRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime now = LocalDateTime.now().minusSeconds(5);

        if (bookItemRequestDto.getStart() == null || bookItemRequestDto.getEnd() == null) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Дата начала и окончания бронирования " +
                            "должны быть указаны")
                    .addPropertyNode("start/end")
                    .addConstraintViolation();
            return false;
        }
        // Проверка start
        if (bookItemRequestDto.getStart().isBefore(now)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Дата начала бронирования не должна быть " +
                            "указана в прошедшем времени")
                    .addPropertyNode("start")
                    .addConstraintViolation();
            return false;
        }

        // Проверка end
        if (bookItemRequestDto.getEnd().isBefore(bookItemRequestDto.getStart())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Дата окончания бронирования не должно " +
                            "быть раньше даты начала бронирования")
                    .addPropertyNode("end")
                    .addConstraintViolation();
            return false;
        }

        if (bookItemRequestDto.getStart().equals(bookItemRequestDto.getEnd())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Дата начала и конца бронирования не " +
                            "должны быть равны")
                    .addPropertyNode("end")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
