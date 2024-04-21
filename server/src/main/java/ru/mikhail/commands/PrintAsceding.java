package ru.mikhail.commands;


import ru.mikhail.exceptions.IllegalArgumentsException;
import ru.mikhail.managers.CollectionManager;
import ru.mikhail.models.SpaceMarine;
import ru.mikhail.network.Request;
import ru.mikhail.network.Response;
import ru.mikhail.network.ResponseStatus;
import ru.mikhail.utility.ConsoleOutput;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Команда 'print_asceding'
 * выводит элементы коллекции в порядке возрастания
 */

public class PrintAsceding extends Command {
    private ConsoleOutput consoleOutput;
    private CollectionManager collectionManager;

    public PrintAsceding(ConsoleOutput consoleOutput, CollectionManager collectionManager) {
        super("print_asceding", " : вывести элементы коллекции в порядке возрастания");
        this.consoleOutput = consoleOutput;
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) throws IllegalArgumentsException {

        if (!request.getArgs().isBlank()) throw new IllegalArgumentsException();
        Collection<SpaceMarine> collection = collectionManager.getCollection();
        if (collection == null || collection.isEmpty()) {
            return new Response(ResponseStatus.ERROR,"Коллекция еще не инициализирована");
        }
        return new Response(ResponseStatus.OK, "Коллекция: ", collection.stream().filter(Objects::nonNull).
                sorted(SpaceMarine::compareTo).collect(Collectors.toList()));
    }



//        if (collectionManager.getCollection() == null || collectionManager.getCollection().isEmpty()) {
//            consoleOutput.printError("Тут пуста, выводить нечего, братиш");
//            return;
//        }
////        collectionManager.getCollection().stream()
////               .filter(Objects::nonNull).sorted(SpaceMarine::compareTo).
////               collect(Collectors.toList());
//        System.out.println(collectionManager.getCollection().stream()
//                .filter(Objects::nonNull)
//                .sorted(SpaceMarine::compareTo).collect(Collectors.toList()));
//
//
//    }

}
