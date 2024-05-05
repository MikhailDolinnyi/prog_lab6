package ru.mikhail.utility;

import ru.mikhail.commandLine.Printable;
import ru.mikhail.network.Request;
import ru.mikhail.network.Response;
import ru.mikhail.network.ResponseStatus;


import java.io.*;
import java.net.*;

public class Client {
    private final String host;
    private final int port;
    private final Printable console;

    public Client(String host, int port, Printable console) {
        this.host = host;
        this.port = port;
        this.console = console;
    }

    public Response sendAndAskResponse(Request request) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(request);
            os.flush();

            byte[] requestData = outputStream.toByteArray();
            InetAddress serverAddress = InetAddress.getByName(host);

            // Создание сокета без указания локального порта
            ObjectInputStream is = getObjectInputStream(requestData, serverAddress);

            try {
                Response response = (Response) is.readObject();


                is.close();
                os.close();
                outputStream.close();

                return response;
            } catch (EOFException e) {
                console.printError("Данные не влезают в буфер на клиенте");
                return new Response(ResponseStatus.ERROR, "Данные не влезают в буфер на клиенте");
            }
        } catch (IOException | ClassNotFoundException e) {
            console.printError("Ошибка при отправке запроса или получении ответа: " + e.getMessage());
            return new Response(ResponseStatus.ERROR, "Ошибка при отправке запроса или получении ответа");
        }
    }

    private ObjectInputStream getObjectInputStream(byte[] requestData, InetAddress serverAddress) throws IOException {
        DatagramPacket receivePacket;
        try (DatagramSocket socket = new DatagramSocket()) {

            DatagramPacket sendPacket = new DatagramPacket(requestData, requestData.length, serverAddress, port);
            socket.send(sendPacket);

            byte[] receivingDataBuffer = new byte[10192];
            receivePacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
            socket.receive(receivePacket);
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(receivePacket.getData());
        return new ObjectInputStream(new BufferedInputStream(inputStream));
    }

}
