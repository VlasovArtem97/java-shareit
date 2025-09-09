package ru.practicum.shareit.booking.validatelocaldatetime;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = DateTimeValidateImpl.class)
@Target({ElementType.TYPE}) // аннотация для класса
@Retention(RetentionPolicy.RUNTIME)
public @interface DateTimeValidate {
    String message() default "Некорректный период бронирования";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
