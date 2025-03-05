package com.joelodom;

import java.util.Scanner;

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
                String result = evaluate(input);
                print(result);
            }
        }
    }

    private static String read(Scanner scanner) {
        System.out.print("> ");
        return scanner.nextLine().trim();
    }

    private static String evaluate(String input) {
        return "You said: " + input;
    }

    private static void print(String output) {
        System.out.println(output);
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
