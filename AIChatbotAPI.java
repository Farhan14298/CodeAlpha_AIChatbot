
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class AIChatbotAPI {

    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        String apiKey = System.getenv("OPENAI_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("Error: OPENAI_API_KEY not found.");
            scanner.close();
            return;
        }

        try {

            String conversationId = createConversation(apiKey);

            if (conversationId == null) {
                System.out.println("Error: Could not create conversation.");
                scanner.close();
                return;
            }

            System.out.println("======================================");
            System.out.println("          CodeAlpha AI Chatbot");
            System.out.println("======================================");
            System.out.println("Ask me anything!");
            System.out.println("Chat memory: ON");
            System.out.println("Type 'bye' to exit.");

            while (true) {

                System.out.print("\nYou: ");
                String question = scanner.nextLine().trim();

                if (question.isEmpty()) {
                    System.out.println("Aarohi: Please enter a question.");
                    continue;
                }

                if (question.equalsIgnoreCase("bye")) {
                    System.out.println("Aarohi: Goodbye!");
                    break;
                }

                try {

                    System.out.println("Aarohi: Thinking...");

                    String response = askAI(
                            question,
                            apiKey,
                            conversationId
                    );

                    String answer = extractAnswer(response);

                    System.out.println("Aarohi: " + answer);

                } catch (Exception e) {

                    System.out.println("Bot: Something went wrong.");
                    System.out.println("Error: " + e.getMessage());
                }
            }

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());
        }

        scanner.close();
    }


    // --------------------------------------------------
    // METHOD FOR WEB SERVER
    // ChatServer.java will call this method
    // --------------------------------------------------

    public static String getAIResponse(String question) {

        try {

            String apiKey = System.getenv("OPENAI_API_KEY");

            if (apiKey == null || apiKey.isEmpty()) {
                return "Error: OPENAI_API_KEY not found.";
            }

            if (question == null || question.trim().isEmpty()) {
                return "Please enter a message.";
            }

            String conversationId = createConversation(apiKey);

            if (conversationId == null) {
                return "Error: Could not create AI conversation.";
            }

            String response = askAI(
                    question.trim(),
                    apiKey,
                    conversationId
            );

            return extractAnswer(response);

        } catch (Exception e) {

            System.out.println(
                    "Web chatbot error: " + e.getMessage()
            );

            return "Sorry, something went wrong while contacting the AI.";
        }
    }


    // --------------------------------------------------
    // CREATE CONVERSATION
    // --------------------------------------------------

    private static String createConversation(String apiKey)
            throws Exception {

        String json = "{}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(
                        URI.create(
                                "https://api.openai.com/v1/conversations"
                        )
                )
                .header(
                        "Content-Type",
                        "application/json"
                )
                .header(
                        "Authorization",
                        "Bearer " + apiKey
                )
                .POST(
                        HttpRequest.BodyPublishers.ofString(json)
                )
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {

            System.out.println(
                    "Conversation API Error "
                            + response.statusCode()
            );

            System.out.println(response.body());

            return null;
        }

        return extractJsonString(
                response.body(),
                "id"
        );
    }


    // --------------------------------------------------
    // SEND QUESTION TO AI
    // --------------------------------------------------

    private static String askAI(
            String question,
            String apiKey,
            String conversationId
    ) throws Exception {

        String safeQuestion = escapeJson(question);

        String json = """
                {
                  "model": "gpt-6-luna",
                  "conversation": "%s",
                  "input": "%s",
                  "instructions": "You are a helpful AI chatbot. Remember the conversation context. Answer clearly and simply. If the user writes in Hindi or Hinglish, you may answer in simple Hinglish."
                }
                """.formatted(
                conversationId,
                safeQuestion
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(
                        URI.create(
                                "https://api.openai.com/v1/responses"
                        )
                )
                .header(
                        "Content-Type",
                        "application/json"
                )
                .header(
                        "Authorization",
                        "Bearer " + apiKey
                )
                .POST(
                        HttpRequest.BodyPublishers.ofString(json)
                )
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {

            return "API_ERROR: "
                    + response.statusCode()
                    + " - "
                    + response.body();
        }

        return response.body();
    }


    // --------------------------------------------------
    // EXTRACT AI ANSWER
    // --------------------------------------------------

    private static String extractAnswer(String json) {

        if (json.startsWith("API_ERROR:")) {
            return json;
        }

        int outputTextPosition =
                json.indexOf("\"type\": \"output_text\"");

        if (outputTextPosition == -1) {

            outputTextPosition =
                    json.indexOf("\"type\":\"output_text\"");
        }

        if (outputTextPosition == -1) {
            return "Sorry, I could not read the AI response.";
        }

        int textPosition =
                json.indexOf(
                        "\"text\"",
                        outputTextPosition
                );

        if (textPosition == -1) {
            return "Sorry, I could not find the answer.";
        }

        int colonPosition =
                json.indexOf(
                        ":",
                        textPosition
                );

        if (colonPosition == -1) {
            return "Sorry, I could not read the answer.";
        }

        int startQuote =
                json.indexOf(
                        "\"",
                        colonPosition
                );

        if (startQuote == -1) {
            return "Sorry, I could not read the answer.";
        }

        startQuote++;

        StringBuilder answer =
                new StringBuilder();

        boolean escaped = false;

        for (
                int i = startQuote;
                i < json.length();
                i++
        ) {

            char c = json.charAt(i);

            if (escaped) {

                switch (c) {

                    case 'n':
                        answer.append('\n');
                        break;

                    case 'r':
                        answer.append('\r');
                        break;

                    case 't':
                        answer.append('\t');
                        break;

                    case '"':
                        answer.append('"');
                        break;

                    case '\\':
                        answer.append('\\');
                        break;

                    case 'u':

                        if (i + 4 < json.length()) {

                            String hex =
                                    json.substring(
                                            i + 1,
                                            i + 5
                                    );

                            try {

                                int unicode =
                                        Integer.parseInt(
                                                hex,
                                                16
                                        );

                                answer.append(
                                        (char) unicode
                                );

                                i += 4;

                            } catch (NumberFormatException e) {

                                answer.append("\\u");
                            }
                        }

                        break;

                    default:
                        answer.append(c);
                }

                escaped = false;

            } else {

                if (c == '\\') {

                    escaped = true;

                } else if (c == '"') {

                    break;

                } else {

                    answer.append(c);
                }
            }
        }

        return answer.toString();
    }


    // --------------------------------------------------
    // EXTRACT JSON VALUE
    // --------------------------------------------------

    private static String extractJsonString(
            String json,
            String key
    ) {

        String search =
                "\"" + key + "\"";

        int keyPosition =
                json.indexOf(search);

        if (keyPosition == -1) {
            return null;
        }

        int colonPosition =
                json.indexOf(
                        ":",
                        keyPosition
                );

        if (colonPosition == -1) {
            return null;
        }

        int startQuote =
                json.indexOf(
                        "\"",
                        colonPosition
                );

        if (startQuote == -1) {
            return null;
        }

        int endQuote =
                json.indexOf(
                        "\"",
                        startQuote + 1
                );

        if (endQuote == -1) {
            return null;
        }

        return json.substring(
                startQuote + 1,
                endQuote
        );
    }


    // --------------------------------------------------
    // MAKE TEXT SAFE FOR JSON
    // --------------------------------------------------

    private static String escapeJson(String text) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}
