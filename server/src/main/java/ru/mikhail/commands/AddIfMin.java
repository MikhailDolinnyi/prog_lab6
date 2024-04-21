package ru.mikhail.commands;



import ru.mikhail.commands.Command;
import ru.mikhail.exceptions.IllegalArgumentsException;
import ru.mikhail.exceptions.InvalidFormException;
import ru.mikhail.managers.CollectionManager;
import ru.mikhail.models.SpaceMarine;
import ru.mikhail.network.Request;
import ru.mikhail.network.Response;
import ru.mikhail.network.ResponseStatus;
import ru.mikhail.utility.ConsoleOutput;

import java.util.Objects;

/**
 * Команда 'add_if_min'
 * Добавляет элемент, если его значение меньше, чем у наименьшего элемента
 */
public class AddIfMin extends Command {

    private CollectionManager collectionManager;
    private ConsoleOutput consoleOutput;

    public AddIfMin(ConsoleOutput consoleOutput, CollectionManager collectionManager) {
        super("add_if_min", " {element}: добавить элемент в коллекцию если его значение меньше," +
                " чем у наименьшего элемента");
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
    public Response execute(Request request) throws IllegalArgumentsException, InvalidFormException {
        if (!request.getArgs().isBlank()) throw new IllegalArgumentsException();
        if (Objects.isNull(request.getObject())){
            return new Response(ResponseStatus.ASK_OBJECT, "Для команды " + this.getName() + " требуется объект");
        }
        if (request.getObject().compareTo(Objects.requireNonNull(collectionManager.getCollection().stream()
                .filter(Objects::nonNull)
                .min(SpaceMarine::compareTo)
                .orElse(null))) <= -1)
        {
            collectionManager.addElement(request.getObject());
            return new Response(ResponseStatus.OK,"Объект успешно добавлен");
        }
        return new Response(ResponseStatus.ERROR,"Элемент меньше максимального");
    }

}
