import java.util.Locale;
import java.util.Scanner;

/**
 * CodeAlpha Java Internship - Task 3: Artificial Intelligence Chatbot.
 * Educational rule-based chatbot using keyword matching and predefined replies.
 */
public class AIChatbot {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printWelcome();

        while (true) {
            System.out.print("\nYou: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Bot: Please type a message so I can help.");
                continue;
            }

            String response = getResponse(input);
            System.out.println("Bot: " + response);

            if (isExit(input)) {
                break;
            }
        }

        scanner.close();
    }

    private static void printWelcome() {
        System.out.println("======================================");
        System.out.println("       CodeAlpha AI Chatbot");
        System.out.println("======================================");
        System.out.println("I am a simple rule-based chatbot.");
        System.out.println("Ask about Java, OOP, programming, or study tips.");
        System.out.println("Type 'help' for examples and 'bye' to exit.");
    }

    private static String getResponse(String input) {
        String message = input.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (message.matches(".*\\b(hi|hello|hey|namaste)\\b.*")) {
            return "Hello! How can I help you today?";
        }
        if (message.matches(".*\\b(help|commands|examples)\\b.*")) {
            return "Try: 'What is Java?', 'Explain OOP', 'Give study tips', "
                    + "'What can you do?', or 'How do I learn programming?'";
        }
        if (message.matches(".*\\b(java|jdk|jvm)\\b.*")) {
            return "Java is a high-level, object-oriented programming language. "
                    + "The JDK provides tools to develop and compile Java programs.";
        }
        if (message.matches(".*\\b(oop|oops|object oriented|class|object|inheritance|polymorphism)\\b.*")) {
            return "OOP organizes programs around objects. Its common principles are "
                    + "encapsulation, inheritance, polymorphism, and abstraction.";
        }
        if (message.matches(".*\\b(programming|coding|learn code|learn programming)\\b.*")) {
            return "Start with variables, data types, conditions, loops, methods, arrays, "
                    + "then learn OOP and build small projects. Practise every day.";
        }
        if (message.matches(".*\\b(study|studying|exam|revision|focus)\\b.*")) {
            return "Choose one small goal, study for 25 minutes without distractions, "
                    + "take a 5-minute break, and revise using practice questions.";
        }
        if (message.matches(".*\\b(feature|features|what can you do|capabilities)\\b.*")) {
            return "I answer predefined questions about Java, OOP, programming, and study tips. "
                    + "I use rules, not live AI or internet access.";
        }
        if (isExit(message)) {
            return "Goodbye! Keep practising Java and have a great day.";
        }

        return "I don't know that one yet. Type 'help' for examples, or ask about "
                + "Java, OOP, programming, or study tips.";
    }

    private static boolean isExit(String input) {
        String message = input.toLowerCase(Locale.ROOT).trim();
        return message.matches(".*\\b(bye|goodbye|exit|quit)\\b.*");
    }
}
