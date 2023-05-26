import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class SimpleWebServer {
    public static void main(String[] args) {
        int port = 9000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                StringBuilder requestBuilder = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    requestBuilder.append(line).append("\r\n");
                }
                String request = requestBuilder.toString().trim();
                // System.out.println(request);

                String response, version;

                if (request.split("\n")[0].contains("HTTP/1.1")) {
                    version = "HTTP/1.1";
                } else if (request.contains("HTTP/1.0")) {
                    version = "HTTP/1.0";
                } else {
                    logData("400: Не поддерживаемая версия");
                    response = "HTTP/1.1 400 Bad Request\r\n\r\n";
                    out.println(response);
                    return;
                }

                String route = request.split(" ")[1];

                if (request.startsWith("GET")) {
                    response = handleGetRequest(version, route);
                } else if (request.startsWith("HEAD")) {
                    response = handleHeadRequest(version, route);
                } else if (request.startsWith("POST")) {
                    try {
                        int contentLength = 0;
                        String contentLengthHeader = getHeader(request, "Content-Length");

                        if (contentLengthHeader != null) {
                            contentLength = Integer.parseInt(contentLengthHeader);
                        }

                        // Читаем тело запроса, если оно присутствует
                        if (contentLength > 0) {
                            char[] requestBody = new char[contentLength];
                            in.read(requestBody, 0, contentLength);
                            String requestBodyString = new String(requestBody);

                            response = handlePostRequest(requestBodyString, request, version, route);
                        }
                        else {
                            logData("400: Заголовок Content-Length отсутствует либо равен 0");
                            response = version + " 400 Bad Request\r\n\r\n";
                        }
                    }
                    catch (IOException e) {
                        logData("400: Ошибка при обработке запроса; возможен нечисловой Content-Length");
                        response = version + " 400 Bad Request\r\n\r\n";
                    }
                } else {
                    logData("400: Не поддерживаемый метод запроса");
                    response = version + " 400 Bad Request\r\n\r\n";
                }

                // System.out.println("Response:\n" + response);
                out.print(response); // Отправка ответа клиенту
            } catch (Exception e) {
                logData("Ошибка при обработке клиентского запроса: " + e.getMessage());
                System.out.println("Ошибка при обработке клиентского запроса: " + e.getMessage());
            }
            finally {
                try {
                    this.clientSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private String handleGetRequest(String version, String route) throws IOException {
            String status, body;
            String filePath, fileName;

            try {
                Path routePath = Path.of(route);
                //  Если есть '.' => есть и имя файл
                if (routePath.toString().contains(".")) {
                    filePath = routePath.getParent().toString();
                    fileName = routePath.getFileName().toString();

                } else {
                    filePath = routePath.toString();
                    if (version.equals("HTTP/1.0")) {
                        fileName = "index11.html";
                    } else {
                        fileName = "index.html";
                    }
                }

                body = Files.readString(Path.of("resources" + filePath, fileName));
                status = "200 OK";
            } catch (IOException e) {
                logData("404: " + route + " не существует");
                status = "404 Not Found";
                fileName = "404.html";
                body = Files.readString(Path.of("resources", fileName));
            }

            String content_type = "application/octet-stream";
            if (fileName.endsWith(".txt") || fileName.endsWith(".md")) content_type = "text/plain";
            if (fileName.endsWith(".html")) content_type = "text/html";
            if (fileName.endsWith(".xml")) content_type = "text/xml";
            if (fileName.endsWith(".json")) content_type = "application/json";

            String headers = "Content-Type: " + content_type + "\n\rContent-Length: %s".formatted(body.length());

            return version + " " + status + "\r\n" + headers + "\r\n\r\n" + body + "\r\n\r\n";
        }

        private String handleHeadRequest(String version, String route) throws IOException {
            return handleGetRequest(version, route).split("\r\n\r\n")[0] + "\r\n\r\n";
        }

//         For tests telnet localhost 9000
//POST / HTTP/1.1
//Content-Type: application/json
//Content-Length: 18
//
//{"a": 10, "b": 25}
        private String handlePostRequest(String json, String request, String version, String route) throws IOException {
            String response;

            if (!route.equals("/") && !route.equals("/index")) {
                logData("404 POST: " + route + " не существует");
                return version + " 404 Not Found\r\n\r\n";
            }

            String content_type = getHeader(request, "Content-Type");
            if (content_type == null || !content_type.contains("application/json")) {
                logData("400 POST: Заголовок Content-Type не существует либо не содержит application/json");
                return version + " 400 Bad Request\r\n\r\n";
            }

            JsonObject jsonRequest = parseJson(json);
            if (jsonRequest != null && isValidJsonPostIndexRequest(jsonRequest)) {

                int a = jsonRequest.get("a").getAsInt();
                int b = jsonRequest.get("b").getAsInt();
                int sum = a + b;
                if (version.equals("HTTP/1.0")) sum -= 10;

                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("sum", sum);

                String headers = "Content-Type: application/json\r\nContent-Length: " + jsonResponse.toString().length();
                response = version + " 200 OK\r\n" + headers + "\r\n\r\n" + jsonResponse + "\r\n";
            } else {
                logData("400 POST: Неправильный json формат в запросе");
                response = version + " 400 Bad Request\r\n\r\n";
            }

            return response;
        }

        private boolean isValidJsonPostIndexRequest(JsonObject jsonRequest) {
            return jsonRequest.has("a") && jsonRequest.has("b") &&
                    jsonRequest.get("a").isJsonPrimitive() && jsonRequest.get("b").isJsonPrimitive();
        }

        private JsonObject parseJson(String json) {
            try {
                return new Gson().fromJson(json, JsonObject.class);
            } catch (Exception e) {
                return null;
            }
        }

        private static String getHeader(String request, String headerName) {
            String[] lines = request.split("\r\n");
            for (String line : lines) {
                if (line.startsWith(headerName)) {
                    String[] parts = line.split(": ");
                    if (parts.length == 2) {
                        return parts[1];
                    }
                }
            }
            return null;
        }

        protected static synchronized void logData(String data) {
            try(FileWriter writer = new FileWriter("log.txt", true))
            {
                Date date = new Date();
                writer.write(date + ": " + data + "\n");
                writer.flush();
            }
            catch(IOException e){
                System.out.println(e.getMessage());
            }
        }
    }
}

