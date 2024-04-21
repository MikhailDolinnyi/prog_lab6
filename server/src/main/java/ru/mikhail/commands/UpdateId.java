package ru.mikhail.commands;


import ru.mikhail.exceptions.IllegalArgumentsException;
import ru.mikhail.exceptions.InvalidFormException;
import ru.mikhail.managers.CollectionManager;
import ru.mikhail.network.Request;
import ru.mikhail.network.Response;
import ru.mikhail.network.ResponseStatus;
import ru.mikhail.utility.ConsoleOutput;

import java.util.Objects;

/**
 * Команда 'update'
 * Обновляет значение элемента коллекции, id которого равен заданному
 */
public class UpdateId extends Command {
    private final CollectionManager collectionManager;
    private final ConsoleOutput consoleOutput;

    public UpdateId(ConsoleOutput consoleOutput, CollectionManager collectionManager) {
        super("update", " id {element}: обновить значение элемента коллекции, id которого равен заданному");
        this.collectionManager = collectionManager;
        this.consoleOutput = consoleOutput;
    }

    /**
     * Исполнить команду
     *
     * @param request аргументы команды
     * @throws IllegalArgumentsException неверные аргументы команды
     */
    @Override
    public Response execute(Request request) throws IllegalArgumentsException{
        if (request.getArgs().isBlank()) throw new IllegalArgumentsException();
        class NoSuchId extends RuntimeException{

        }
        try {
            int id = Integer.parseInt(request.getArgs().trim());
            if (!collectionManager.checkExist((long)id)) throw new NoSuchId();
            if (Objects.isNull(request.getObject())){
                return new Response(ResponseStatus.ASK_OBJECT, "Для команды " + this.getName() + " требуется объект");
            }
            collectionManager.editById((long)id, request.getObject());
            return new Response(ResponseStatus.OK, "Объект успешно обновлен");
        } catch (NoSuchId err) {
            return new Response(ResponseStatus.ERROR,"В коллекции нет элемента с таким id");
        } catch (NumberFormatException exception) {
            return new Response(ResponseStatus.ERROR,"id должно быть числом типа int");
        } catch (InvalidFormException e) {
            throw new RuntimeException(e);
        }
    }
}