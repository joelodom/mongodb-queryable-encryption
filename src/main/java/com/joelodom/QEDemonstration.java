package com.joelodom;

import java.util.Scanner;

/**
 * See the README.md for documentation about the overall demonstration.
 */
public class QEDemonstration {
    public static void main(String[] args) {
        // welcome messages
        System.out.println(Strings.WELCOME_MESSAGE);
        printStatus();

        // REPL
        try (  // to clean up the resources
            Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String input = read(scanner);
                if ("exit".equalsIgnoreCase(input)) {
                    break;
                }
                evaluate(input);
            }
        }
    }

    private static String read(Scanner scanner) {
        System.out.print("> ");
        return scanner.nextLine().trim();
    }

    private static void evaluate(String input) {
        if (null != input) switch (input) {
            case "help":
                printHelp();
                break;
            case "status":
                printStatus();
                break;
            case "create-database":
                DatabaseManagement.createDatabase();
                break;
            default:
                System.out.println(input + " is not a recognized command. Try help.");
                System.out.println();
                break;
        }
    }

    private static void printHelp() {
        System.out.println(Strings.HELP_MESSAGE);
        System.out.println();
    }

    private static void printStatus() {
        System.out.println("Status:");
        System.out.println();
        System.out.println("MONGODB_URI: " + Env.MONGODB_URI);
        System.out.println("SHARED_LIB_PATH: " + Env.SHARED_LIB_PATH);
        System.out.println();
    }
}
