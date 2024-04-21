package ru.mikhail.commands;


import ru.mikhail.exceptions.IllegalArgumentsException;
import ru.mikhail.managers.CollectionManager;
import ru.mikhail.models.SpaceMarine;
import ru.mikhail.network.Request;
import ru.mikhail.network.Response;
import ru.mikhail.network.ResponseStatus;
import ru.mikhail.utility.ConsoleOutput;

/**
 * Команда 'average_of_height'
 * Выводит среднее значение поля height всех элементов
 */
public class AverageOfHeight extends Command {
    private CollectionManager collectionManager;
    private ConsoleOutput consoleOutput;

    public AverageOfHeight(ConsoleOutput consoleOutput, CollectionManager collectionManager) {
        super("average_of_height", " : вывести среднее значение поля height всех элементов коллекции");
        this.consoleOutput = consoleOutput;
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) throws IllegalArgumentsException {
        if (!request.getArgs().isBlank()) throw new IllegalArgumentsException();

//        double averageHeight = Double.parseDouble(request.getArgs().trim());
        return new Response(ResponseStatus.OK,"Среднее значение роста во всех элементах " + collectionManager.getCollection().stream()
                .mapToDouble(SpaceMarine::getHeight)
                .average()
                .orElse(0.0));


    }


}
