package ru.mikhail.utility;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mikhail.managers.FileManager;
import ru.mikhail.network.Request;
import ru.mikhail.network.Response;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {

    private final int port;
    private final Printable console;
    private final RequestHandler requestHandler;

    static final Logger serverLogger = LogManager.getLogger(Server.class);



    public Server(int port, RequestHandler handler, FileManager fileManager) {
        this.port = port;
        this.console = new PrintConsole();
        this.requestHandler = handler;
    }

//    public void saveCollection(FileManager fileManager) {
//        fileManager.saveObjects(); // Предполагается, что объект fileManager инициализирован где-то
//    }
    public void run() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            serverLogger.info("Сервер запущен на порту " + port);





            while (true) {
                byte[] receivingDataBuffer = new byte[5096];
                DatagramPacket receivePacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
                serverSocket.receive(receivePacket);

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                ByteArrayInputStream byteStream = new ByteArrayInputStream(receivePacket.getData());
                ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));

                Request userRequest = (Request) is.readObject();
                serverLogger.info("Получен запрос от клиента: " + userRequest.getCommandName(), userRequest);

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
            }
        } catch (IOException | ClassNotFoundException e) {
            console.printError("Произошла ошибка: " + e.getMessage());
            serverLogger.error("Произошла ошибка: " + e.getMessage());
        }
    }
}
