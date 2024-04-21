package ru.mikhail.commands;


import ru.mikhail.managers.CollectionManager;
import ru.mikhail.network.Request;
import ru.mikhail.network.Response;
import ru.mikhail.network.ResponseStatus;
import ru.mikhail.utility.ConsoleOutput;
import ru.mikhail.exceptions.IllegalArgumentsException;

/**
 * Команда 'clear'
 * Очищает коллекцию
 */
public class Clear extends Command {
    private CollectionManager collectionManager;
    private ConsoleOutput consoleOutput;

    public Clear(ConsoleOutput consoleOutput, CollectionManager collectionManager) {
        super("clear", ": очистить коллекцию");
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
    public Response execute(Request request) throws IllegalArgumentsException {
        if (!request.getArgs().isBlank()) throw new IllegalArgumentsException();
        collectionManager.clear();
        return new Response(ResponseStatus.OK,"Элементы удалены");
    }
}
