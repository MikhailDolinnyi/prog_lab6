package ru.mikhail;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mikhail.commands.*;
import ru.mikhail.exceptions.ExitException;
import ru.mikhail.managers.CollectionManager;
import ru.mikhail.managers.CommandManager;
import ru.mikhail.managers.FileManager;
import ru.mikhail.utility.*;

import java.util.List;
import java.util.Scanner;

public class App extends Thread {
    public static int PORT = 6086;
    private static final Printable console = new PrintConsole();
    private static final ConsoleOutput consoleOutput = new ConsoleOutput();

    static final Logger rootLogger = LogManager.getRootLogger();

    private static String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().trim();
    }

    public static void main(String[] args) {

        if (args.length != 0) {
            try {
                PORT = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }
        CollectionManager collectionManager = new CollectionManager();
        FileManager fileManager = new FileManager(console, collectionManager);
        // мб фиксить нужно
        try {
            App.rootLogger.info("Создание объектов");
            fileManager.findFile();
            fileManager.createObjects();
            App.rootLogger.info("Создание объектов успешно завершено");
        } catch (ExitException e) {
            console.println(OutputColors.toColor("До свидания!", OutputColors.YELLOW));
            App.rootLogger.error("Ошибка во времени создания объектов");
            return;
        }

        CommandManager commandManager = new CommandManager(fileManager);
        commandManager.addCommand(List.of(
                new Help(commandManager),
                new Show(collectionManager),
                new AddElement(consoleOutput,collectionManager),
                new AddIfMin(consoleOutput,collectionManager),
                new Clear(consoleOutput,collectionManager),
                new ExecuteScript(consoleOutput,fileManager,commandManager)

        ));
        App.rootLogger.debug("Создан объект менеджера команд");
        RequestHandler requestHandler = new RequestHandler(commandManager);
        App.rootLogger.debug("Создан объект обработчика запросов");
        Server server = new Server(PORT, requestHandler, fileManager);
        App.rootLogger.debug("Создан объект сервера");

        new Thread(() -> {
            while (true) {
                String userInput = getUserInput();
                if (userInput.equalsIgnoreCase("save")) {
                    fileManager.saveObjects();
                }
            }
        }).start();

        Runtime.getRuntime().addShutdownHook(new Thread(new AutoSaveHook(fileManager)));


        server.run();

    }
}
