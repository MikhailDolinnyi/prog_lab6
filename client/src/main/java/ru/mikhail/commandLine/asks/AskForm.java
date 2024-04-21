package ru.mikhail.commandLine.asks;


import ru.mikhail.exceptions.InvalidFormException;

/**
 * Абстрактный класс для пользовательского ввода
 *
 * @param <T> класс формы
 */

public abstract class AskForm<T> {
    public abstract T build() throws InvalidFormException;
}
