package ru.mikhail.commands;



import ru.mikhail.commandLine.asks.AskSpaceMarine;
import ru.mikhail.exceptions.*;
import ru.mikhail.managers.CollectionManager;
import ru.mikhail.models.SpaceMarine;
import ru.mikhail.network.Request;
import ru.mikhail.network.Response;
import ru.mikhail.network.ResponseStatus;
import ru.mikhail.utility.ConsoleOutput;

import java.util.Collection;
import java.util.Objects;

/**
 * Команда 'remove_lower'
 * Удаляет из коллекции все элементы, меньшие, чем заданный
 */
public class RemoveLower extends Command {
    private CollectionManager collectionManager;
    private ConsoleOutput consoleOutput;

    public RemoveLower(ConsoleOutput consoleOutput, CollectionManager collectionManager) {
        super("remove_lower", "{element} : удалить из коллекции все элементы, меньшие, чем заданный");
        this.consoleOutput = consoleOutput;
        this.collectionManager = collectionManager;
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
        class NoElements extends RuntimeException{

        }
        try {
            if (Objects.isNull(request.getObject())){
                return new Response(ResponseStatus.ASK_OBJECT, "Для команды " + this.getName() + " требуется объект");
            }
            Collection<SpaceMarine> toRemove = collectionManager.getCollection().stream()
                    .filter(Objects::nonNull)
                    .filter(studyGroup -> studyGroup.compareTo(request.getObject()) <= -1)
                    .toList();
            collectionManager.removeElements(toRemove);
            return new Response(ResponseStatus.OK,"Удалены элементы меньшие чем заданный");
        } catch (NoElements e){
            return new Response(ResponseStatus.ERROR,"В коллекции нет элементов");
        } catch (FIleFieldException e){
            return new Response(ResponseStatus.ERROR,"Поля в файле не валидны! Объект не создан");
        }
    }

    //    @Override
//    public void execute(String args) throws IllegalArgumentsException {
//        if (!args.isBlank()) throw new IllegalArgumentsException();
//        class NoElements extends RuntimeException {
//
//        }
//        try {
//            consoleOutput.println(OutputColors.toColor("Создание объекта StudyGroup", OutputColors.CYAN));
//            SpaceMarine newElement = new AskSpaceMarine(consoleOutput).build();
//            consoleOutput.println(OutputColors.toColor("Создание объекта StudyGroup окончено успешно!", OutputColors.CYAN));
//            Collection<SpaceMarine> toRemove = collectionManager.getCollection().stream()
//                    .filter(Objects::nonNull)
//                    .filter(spaceMarine -> spaceMarine.compareTo(newElement) <= -1)
//                    .toList();
//            collectionManager.removeElements(toRemove);
//            consoleOutput.println(OutputColors.toColor("Удалены элементы, меньшие, чем заданный", OutputColors.GREEN));
//        } catch (NoElements e) {
//            consoleOutput.printError("В коллекции нет элементов");
//        } catch (FIleFieldException e) {
//            consoleOutput.printError("Поля в файле не валидны! Объект не создан");
//        }
//    }

}
