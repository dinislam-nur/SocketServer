package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Сервер сокет.
 */
public class Server {
    /**
     * Порт, на котором запускается сервер.
     */
    private static final int PORT = 8080;

    /**
     * Метод запускает сервер. Ожидает соединение клиента и прокидывает входной и выходной
     * потока от соединения в метод handle() класса RequestHandler.
     */
    public void start() {
        try (final ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket accept = serverSocket.accept();

                final RequestHandler requestHandler = new RequestHandler();
                requestHandler.handle(accept.getInputStream(), accept.getOutputStream());
                accept.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
