package ru.mikhail.utility;


import ru.mikhail.exceptions.*;
import ru.mikhail.managers.CommandManager;
import ru.mikhail.network.Request;
import ru.mikhail.network.Response;
import ru.mikhail.network.ResponseStatus;

public class RequestHandler {

    private final CommandManager commandManager;

    public RequestHandler(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public Response handle(Request request) {
        try {
            commandManager.addToHistory(request.getCommandName());
            return commandManager.execute(request);
        } catch (IllegalArgumentsException e) {
            return new Response(ResponseStatus.WRONG_ARGUMENTS,
                    "Неверное использование аргументов команды");
        } catch (CommandRuntimeException e) {
            return new Response(ResponseStatus.ERROR,
                    "Ошибка при исполнении программы");
        } catch (NoCommandException e) {
            return new Response(ResponseStatus.ERROR, "Такой команды нет в списке");
        } catch (ExitException e) {
            return new Response(ResponseStatus.EXIT);
        } catch (InvalidFormException e) {
            throw new RuntimeException(e);
        }
    }
}
