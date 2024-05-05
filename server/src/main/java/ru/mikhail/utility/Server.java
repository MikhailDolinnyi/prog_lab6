package ru.mikhail.utility;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mikhail.managers.CommandManager;
import ru.mikhail.network.Request;
import ru.mikhail.network.Response;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class Server {

    private final int port;
    private final Printable console;
    private final RequestHandler requestHandler;

    static final Logger serverLogger = LogManager.getLogger(Server.class);


    public Server(int port, RequestHandler handler) {
        this.port = port;
        this.console = new PrintConsole();
        this.requestHandler = handler;
    }


    public void run(CommandManager commandManager) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            serverLogger.info("Сервер запущен на порту " + port);

//            Set commands = commandManager.getCommandsNames();
//            String[] array = new String[commands.size()];
//            commands.toArray(array);
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ObjectOutputStream oos = new ObjectOutputStream(baos);
//            // Сериализуем Set
//            oos.writeObject(array);
//            oos.flush();
//            byte[] byteCommands = baos.toByteArray();
//            DatagramPacket sendCommands = new DatagramPacket(byteCommands, byteCommands.length);
//            serverSocket.send(sendCommands); // Отправляем пакет с командами клиенту
//            serverLogger.info("Отправлен ответ клиенту: " + sendCommands);


            while (true) {


                byte[] receivingDataBuffer = new byte[10192];
                DatagramPacket receivePacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
                serverSocket.receive(receivePacket);


                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

//                byte[] data = receivePacket.getData();
//                for (int i = 0; i < data.length; i++) {
//                    if (data[i] == 0) {
//                        // Нашли элемент равный 0, теперь можно создать новый буфер, обрезанный до этого индекса
//                        byte[] newDataBuffer = Arrays.copyOf(receivingDataBuffer, i);
//                        // Делайте что-то с newDataBuffer
//                        break; // Выходим из цикла, т.к. нашли первый элемент равный 0
//                    }
//                }


                ByteArrayInputStream byteStream = new ByteArrayInputStream(receivePacket.getData());
                ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));


                try {
                    Request userRequest = (Request) is.readObject();


                    Response responseToUser = requestHandler.handle(userRequest);

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ObjectOutputStream os = new ObjectOutputStream(outputStream);
                    os.writeObject(responseToUser);
                    os.flush();

                    byte[] responseData = outputStream.toByteArray();


                    DatagramPacket sendPacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);

                    serverSocket.send(sendPacket);


                    serverLogger.info("Отправлен ответ клиенту: " + responseToUser);


                    os.close();
                    is.close();
                } catch (EOFException e) {
                    Response responseToUser = requestHandler.bufferError();

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ObjectOutputStream os = new ObjectOutputStream(outputStream);
                    os.writeObject(responseToUser);
                    os.flush();

                    byte[] responseData = outputStream.toByteArray();


                    DatagramPacket sendPacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);

                    serverSocket.send(sendPacket);


                    serverLogger.info("Отправлен ответ клиенту: " + responseToUser);


                    os.close();
                    is.close();


                    serverLogger.error("Данные не влезают в буфер на сервере");
                }
            }
        } catch (IOException | ClassNotFoundException e) {

            console.printError("Произошла ошибка: " + e.getMessage());

            serverLogger.error("Произошла ошибка: " + e.getMessage());


        }
    }
}
