package ru.mikhail.commands;


import ru.mikhail.exceptions.CommandRuntimeException;
import ru.mikhail.exceptions.ExitException;
import ru.mikhail.exceptions.IllegalArgumentsException;
import ru.mikhail.exceptions.InvalidFormException;
import ru.mikhail.managers.CollectionManager;
import ru.mikhail.network.Request;
import ru.mikhail.network.Response;
import ru.mikhail.network.ResponseStatus;
import ru.mikhail.utility.ConsoleOutput;

/**
 * Команда 'remove_head'
 * Выводит первый элемент коллекции и удаляет его
 */

public class RemoveHead extends Command {
    private CollectionManager collectionManager;
    private ConsoleOutput consoleOutput;

    public RemoveHead(ConsoleOutput consoleOutput, CollectionManager collectionManager) {
        super("remove_head", " вывести первый элемент коллекции и удалить его");
        this.collectionManager = collectionManager;
        this.consoleOutput = consoleOutput;
    }

    @Override
    public Response execute(Request request)  {
        if(collectionManager.getCollection()==null|| collectionManager.getCollection().isEmpty()){
            return new Response(ResponseStatus.ERROR,"коллекш из емпти");

        }
        return new Response(ResponseStatus.OK, "данный элемент удалён: " + collectionManager.getCollection().poll());
    }


    //    @Override
//    public void execute(String args) throws IllegalArgumentsException {
//        if (collectionManager.getCollection() == null || collectionManager.getCollection().isEmpty()) {
//            consoleOutput.printError("Нечего удалять, братиш! Пуста");
//            return;
//        }
//        System.out.println(collectionManager.getCollection().peek());
//        collectionManager.removeElement(collectionManager.getCollection().poll());
//
//        consoleOutput.println(OutputColors.toColor("Объект удален успешно", OutputColors.GREEN));
//
//
//    }
}
