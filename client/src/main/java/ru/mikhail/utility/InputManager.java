package ru.mikhail.utility;


import ru.mikhail.asks.AskSpaceMarine;
import ru.mikhail.commandLine.ConsoleOutput;
import ru.mikhail.commandLine.Printable;
import ru.mikhail.exceptions.ExitException;
import ru.mikhail.exceptions.FIleFieldException;
import ru.mikhail.exceptions.InvalidFormException;
import ru.mikhail.models.SpaceMarine;
import ru.mikhail.network.Request;
import ru.mikhail.network.Response;
import ru.mikhail.network.ResponseStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Класс обработки пользовательского ввода
 */
public class InputManager {
    private final Printable console;
    private final Scanner userScanner;
    private final Client client;



    public InputManager(Printable console, Scanner userScanner, Client client) {
        this.console = console;
        this.userScanner = userScanner;
        this.client = client;
    }

        List<String> commandList = Arrays.stream(new String[]{"add", "add_if_min", "update"})
            .collect(Collectors.toList());


    /**
     * Перманентная работа с пользователем и выполнение команд
     */
    public void interactiveMode() {
        while (true) {
            try {
                if (!userScanner.hasNext()) throw new ExitException();

                String[] userCommand = (userScanner.nextLine().trim() + " ").split(" ", 2); // прибавляем пробел, чтобы split выдал два элемента в массиве
                for (String command : commandList) {
                    if (Objects.equals(userCommand[0], command)) {
                        SpaceMarine spaceMarine = new AskSpaceMarine(console).build();
                        if (!spaceMarine.validate()) throw new InvalidFormException();
                        Response newResponse = client.sendAndAskResponse(
                                new Request(
                                        userCommand[0].trim(),
                                        userCommand[1].trim(),
                                        spaceMarine));
                        if (newResponse.getStatus() != ResponseStatus.OK) {
                            console.printError(newResponse.getResponse());
                        } else {
                            this.printResponse(newResponse);
                        }
                        break;
                    }
                    else{
                        Response response = client.sendAndAskResponse(new Request(userCommand[0].trim(), userCommand[1].trim()));
                        this.printResponse(response);
                        switch (response.getStatus()) {
                            case ASK_OBJECT -> {
                                SpaceMarine spaceMarine = new AskSpaceMarine(console).build();
                                if (!spaceMarine.validate()) throw new InvalidFormException();
                                Response newResponse = client.sendAndAskResponse(
                                        new Request(
                                                userCommand[0].trim(),
                                                userCommand[1].trim(),
                                                spaceMarine));
                                if (newResponse.getStatus() != ResponseStatus.OK) {
                                    console.printError(newResponse.getResponse());
                                } else {
                                    this.printResponse(newResponse);

                                }

                            }

                            case EXIT -> throw new ExitException();
                            case EXECUTE_SCRIPT -> {
                                ConsoleOutput.setFileMode(true);
                                this.fileExecution(response.getResponse());
                                ConsoleOutput.setFileMode(false);
                            }
                            default -> {
                            }
                        }


                        break;

                    }
                }


            } catch (InvalidFormException err) {
                console.printError("Поля не валидны! Объект не создан");
            } catch (NoSuchElementException exception) {
                console.printError("Пользовательский ввод не обнаружен!");
            } catch (ExitException exitObliged) {
                console.println(OutputColors.toColor("До свидания!", OutputColors.YELLOW));
                return;
            }
        }
    }

    private void printResponse(Response response) {
        switch (response.getStatus()) {
            case OK -> {
                if ((Objects.isNull(response.getCollection()))) {
                    console.println(response.getResponse());
                } else {
                    console.println(response.getResponse() + "\n" + response.getCollection().toString());
                }
            }
            case ERROR -> console.printError(response.getResponse());
            case WRONG_ARGUMENTS -> console.printError("Неверное использование команды!");
            default -> {
            }
        }
    }

    private void fileExecution(String args) throws ExitException {
        if (args == null || args.isEmpty()) {
            console.printError("Путь не распознан");
            return;
        } else console.println(OutputColors.toColor("Путь получен успешно", OutputColors.PURPLE));
        args = args.trim();
        try {
            ExecuteManager.pushFile(args);
            for (String line = ExecuteManager.readLine(); line != null; line = ExecuteManager.readLine()) {
                String[] userCommand = (line + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                if (userCommand[0].isBlank()) return;
                if (userCommand[0].equals("execute_script")) {
                    if (ExecuteManager.fileRepeat(userCommand[1])) {
                        console.printError("Найдена рекурсия по пути " + new File(userCommand[1]).getAbsolutePath());
                        continue;
                    }
                }
                console.println(OutputColors.toColor("Выполнение команды " + userCommand[0], OutputColors.YELLOW));
                Response response = client.sendAndAskResponse(new Request(userCommand[0].trim(), userCommand[1].trim()));
                this.printResponse(response);
                switch (response.getStatus()) {
                    case ASK_OBJECT -> {
                        SpaceMarine spaceMarine;
                        try {
                            spaceMarine = new AskSpaceMarine(console).build();
                            if (!spaceMarine.validate()) throw new FIleFieldException();
                        } catch (FIleFieldException err) {
                            console.printError("Поля в файле не валидны! Объект не создан");
                            continue;
                        }
                        Response newResponse = client.sendAndAskResponse(
                                new Request(
                                        userCommand[0].trim(),
                                        userCommand[1].trim(),
                                        spaceMarine));
                        if (newResponse.getStatus() != ResponseStatus.OK) {
                            console.printError(newResponse.getResponse());
                        } else {
                            this.printResponse(newResponse);
                        }
                    }
                    case EXIT -> throw new ExitException();
                    case EXECUTE_SCRIPT -> {
                        this.fileExecution(response.getResponse());
                        ExecuteManager.popRecursion();
                    }
                    default -> {
                    }
                }
            }
            ExecuteManager.popFile();
        } catch (FileNotFoundException fileNotFoundException) {
            console.printError("Такого файла не существует");
        } catch (IOException e) {
            console.printError("Ошибка ввода вывода");
        }
    }
}

