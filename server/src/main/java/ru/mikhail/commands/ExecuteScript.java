package ru.mikhail.commands;


import ru.mikhail.exceptions.IllegalArgumentsException;
import ru.mikhail.managers.CommandManager;
import ru.mikhail.managers.FileManager;
import ru.mikhail.network.Request;
import ru.mikhail.network.Response;
import ru.mikhail.network.ResponseStatus;
import ru.mikhail.utility.ConsoleOutput;

/**
 * Команда 'execute_script'
 * Считать и исполняет скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.
 */
public class ExecuteScript extends Command {
    private final FileManager fileManager;
    private final ConsoleOutput consoleOutput;
    private final CommandManager commandManager;

    public ExecuteScript(ConsoleOutput consoleOutput, FileManager fileManager, CommandManager commandManager) {
        super("execute_script", " file_name: считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
        this.fileManager = fileManager;
        this.consoleOutput = consoleOutput;
        this.commandManager = commandManager;
    }

    /**
     * Исполнить команду
     *
     * @param request аргументы команды
     * @throws IllegalArgumentsException неверные аргументы команды
     */
    @Override
    public Response execute(Request request) throws IllegalArgumentsException {
        if (request.getArgs().isBlank()) throw new IllegalArgumentsException(
        );
        return new Response(ResponseStatus.EXECUTE_SCRIPT, request.getArgs());
    }
}
