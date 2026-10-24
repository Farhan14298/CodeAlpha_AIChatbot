
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ChatServer {

    public static void main(String[] args) throws Exception {

        // -----------------------------------------
        // PORT
        // Local computer = 8081
        // Online hosting = PORT environment variable
        // -----------------------------------------

        String portValue = System.getenv("PORT");

        int port = 8081;

        if (portValue != null && !portValue.isEmpty()) {
            port = Integer.parseInt(portValue);
        }


        // -----------------------------------------
        // CREATE SERVER
        // -----------------------------------------

        HttpServer server = HttpServer.create(
                new InetSocketAddress(port),
                0
        );


        // -----------------------------------------
        // HTML
        // -----------------------------------------

        server.createContext("/", exchange -> {

            Path filePath = Path.of(
                    "web",
                    "index.html"
            );

            byte[] response =
                    Files.readAllBytes(filePath);

            exchange.getResponseHeaders().set(
                    "Content-Type",
                    "text/html; charset=UTF-8"
            );

            exchange.sendResponseHeaders(
                    200,
                    response.length
            );

            exchange.getResponseBody().write(response);
            exchange.getResponseBody().close();
        });


        // -----------------------------------------
        // CSS
        // -----------------------------------------

        server.createContext("/style.css", exchange -> {

            Path filePath = Path.of(
                    "web",
                    "style.css"
            );

            byte[] response =
                    Files.readAllBytes(filePath);

            exchange.getResponseHeaders().set(
                    "Content-Type",
                    "text/css; charset=UTF-8"
            );

            exchange.sendResponseHeaders(
                    200,
                    response.length
            );

            exchange.getResponseBody().write(response);
            exchange.getResponseBody().close();
        });


        // -----------------------------------------
        // JAVASCRIPT
        // -----------------------------------------

        server.createContext("/script.js", exchange -> {

            Path filePath = Path.of(
                    "web",
                    "script.js"
            );

            byte[] response =
                    Files.readAllBytes(filePath);

            exchange.getResponseHeaders().set(
                    "Content-Type",
                    "application/javascript; charset=UTF-8"
            );

            exchange.sendResponseHeaders(
                    200,
                    response.length
            );

            exchange.getResponseBody().write(response);
            exchange.getResponseBody().close();
        });


        // -----------------------------------------
        // CHAT API
        // -----------------------------------------

        server.createContext("/chat", exchange -> {

            if ("POST".equalsIgnoreCase(
                    exchange.getRequestMethod()
            )) {

                try {

                    // Message received from browser
                    String message = new String(
                            exchange
                                    .getRequestBody()
                                    .readAllBytes(),
                            StandardCharsets.UTF_8
                    );

                    System.out.println(
                            "Browser message: " + message
                    );


                    // Send question to AI
                    String response =
                            AIChatbotAPI.getAIResponse(
                                    message
                            );


                    System.out.println(
                            "AI response: " + response
                    );


                    // Convert response to bytes
                    byte[] responseBytes =
                            response.getBytes(
                                    StandardCharsets.UTF_8
                            );


                    exchange.getResponseHeaders().set(
                            "Content-Type",
                            "text/plain; charset=UTF-8"
                    );


                    exchange.sendResponseHeaders(
                            200,
                            responseBytes.length
                    );


                    exchange
                            .getResponseBody()
                            .write(responseBytes);


                    exchange
                            .getResponseBody()
                            .close();


                } catch (Exception e) {

                    e.printStackTrace();

                    String errorMessage =
                            "Server error: "
                                    + e.getMessage();


                    byte[] errorBytes =
                            errorMessage.getBytes(
                                    StandardCharsets.UTF_8
                            );


                    exchange.getResponseHeaders().set(
                            "Content-Type",
                            "text/plain; charset=UTF-8"
                    );


                    exchange.sendResponseHeaders(
                            500,
                            errorBytes.length
                    );


                    exchange
                            .getResponseBody()
                            .write(errorBytes);


                    exchange
                            .getResponseBody()
                            .close();
                }

            } else {

                exchange.sendResponseHeaders(
                        405,
                        -1
                );

                exchange.close();
            }
        });


        // -----------------------------------------
        // START SERVER
        // -----------------------------------------

        server.start();


        System.out.println(
                "======================================"
        );

        System.out.println(
                "CodeAlpha AI Chatbot Server"
        );

        System.out.println(
                "======================================"
        );

        System.out.println(
                "Server running on port: " + port
        );


        // Keep Java application running
        Thread.currentThread().join();
    }
}
