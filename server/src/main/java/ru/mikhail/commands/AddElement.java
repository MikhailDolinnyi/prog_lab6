package ru.mikhail.commands;


import ru.mikhail.exceptions.IllegalArgumentsException;
import ru.mikhail.exceptions.InvalidFormException;
import ru.mikhail.managers.CollectionManager;
import ru.mikhail.network.Request;
import ru.mikhail.network.Response;
import ru.mikhail.network.ResponseStatus;


/**
 * Команда 'add'
 * Добавляет новый элемент в коллекцию
 */
public class AddElement extends Command {
    private final CollectionManager collectionManager;


    public AddElement(CollectionManager collectionManager) {
        super("add", " {element} : добавить новый элемент в коллекцию");
        this.collectionManager = collectionManager;
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

        collectionManager.addElement(request.getObject());
        return new Response(ResponseStatus.OK, "Объект успешно добавлен");

    }
}
