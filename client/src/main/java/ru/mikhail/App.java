package ru.mikhail;


import ru.mikhail.commandLine.ConsoleOutput;
import ru.mikhail.commandLine.Printable;
import ru.mikhail.exceptions.IllegalArgumentsException;
import ru.mikhail.utility.Client;
import ru.mikhail.utility.InputManager;

import java.util.Scanner;

public class App {
    private static String host;
    private static int port;
    private static Printable console = new ConsoleOutput();

    public static boolean parseHostPort(String[] args) {
        try {
            if (args.length != 2) throw new IllegalArgumentsException("Передайте хост и порт в аргументы " +
                    "командной строки в формате <host> <port>");
            host = args[0];
            port = Integer.parseInt(args[1]);
            if (port < 0) throw new IllegalArgumentsException("Порт должен быть натуральным числом");
            return true;
        } catch (IllegalArgumentsException e) {
            console.printError(e.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        if (!parseHostPort(args)) return;
        console = new ConsoleOutput();
        Client client = new Client(host, port, console);
        new InputManager(console, new Scanner(System.in), client).interactiveMode();

    }
}
