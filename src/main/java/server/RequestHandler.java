package server;

import java.io.*;

/**
 * Обработчики http запросов
 */
public class RequestHandler {

    /**
     * Уровень вложенности файлов
     */
    private static int nestingLevel = 0;

    /**
     * Метод обработки запроса. Читает запрос из потока InputStream. Отправлет response
     * в поток OutputStream.
     * Если request method GET, отправляет http response со статус кодом 200 и телом,
     * в котором перечисляется список всех директорий от корневой директории. В любом
     * другом случае отправляет http response со статус кодом 404.
     *
     * @param inputStream  - входной поток - request.
     * @param outputStream - выходной поток - response.
     */
    public void handle(InputStream inputStream, OutputStream outputStream) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
             BufferedWriter bwr = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            String s = br.readLine();

            if (s.contains("GET")) {
                final String content = getContent();
                bwr.write("HTTP/1.1 200 OK\r\n");
                bwr.write("Content-Type: text/html; charset=UTF-8\r\n");
                bwr.write("Content-Length: " + content.length() + "\r\n");
                bwr.write("\r\n");
                bwr.write(content);
            } else {
                bwr.write("HTTP/1.1 404 NotFound\r\n");
            }
        } catch (IOException | NullPointerException e) {
            System.out.println("Проблемы с чтением из ресурсов");
            e.printStackTrace();
        }
    }

    /**
     * Метод возвращает строковое представление списка директорий.
     *
     * @return - строковое представление списка директорий.
     */
    private String getContent() {
        final File dir = new File(".");
        return "<body>" +
                writeDirectory(dir) +
                "</body>";
    }

    /**
     * Формирует список файлов и директорий в директории dir.
     * Для вложенных директорий рекурсивно вызывается этот же метод.
     *
     * @param dir - директория, для которой сформируется список директорий.
     * @return - список директорий.
     */
    private String writeDirectory(File dir) {
        final StringBuilder stringBuilder = new StringBuilder();
        final File[] files = dir.listFiles();
        if (files == null) {
            throw new NullPointerException("В директории нет файлов");
        }
        for (File file : files) {
            stringBuilder
                    .append("<pre>")
                    .append(indent())
                    .append("-")
                    .append(file.getName());
            if (file.isDirectory()) {
                stringBuilder
                        .append(" :")
                        .append("</br>")
                        .append("</pre>");
                nestingLevel++;
                stringBuilder.append(writeDirectory(file));
                nestingLevel--;
            } else {
                stringBuilder
                        .append("</br>")
                        .append("</pre>");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Возращает отступ в зависимости от вложенности директории.
     *
     * @return - отступ в зависимости от вложенности директории.
     */
    private String indent() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < nestingLevel; i++) {
            stringBuilder.append("\t");
        }
        return stringBuilder.toString();
    }
}
