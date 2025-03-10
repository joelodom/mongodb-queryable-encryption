package com.joelodom;

import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;

/**
 * See the README.md for documentation about the overall demonstration.
 * There are lots of comments in the source code, but the README gives the
 * big picture, so start there. This file isn't as commented because it's
 * not really about QE and is pretty straightforward.
 */

public class QEDemonstration {
    public static void main(String[] args) {
        // welcome messages
        System.out.println(Strings.WELCOME_MESSAGE);
        System.out.println();
        printStatus();

        /**
         * This is a cheap REPL implementation. It'll do for now.
         */
        try (  // to clean up the resources
            Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String input = read(scanner);
                
                if ("exit".equalsIgnoreCase(input)) {
                    break;
                }
                else if ("".equals(input)) {
                    continue;
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
        /**
         * TODO: More robust error handling and allow using arrow keys and all.
         * This is pretty rough code.
         */

        String[] args = input.split("\\s+");

        String verb = args[0];
        String noun = args.length > 1 ? args[1] : null;

        Instant start = Instant.now();

        switch (verb) {
            case "help" -> printHelp();
            case "status" -> printStatus();
            case "create-collection" -> DatabaseManagement.createEncryptedCollection();
            case "destroy-database" -> DatabaseManagement.destroyDatabase();
            case "add-members" -> {
                if (noun == null) {
                    System.out.println(
                        "Please specify the number of members to add.");
                    System.out.println();
                } else {
                    Members.addRandomMembers(Integer.parseInt(noun));
                }
            }
            case "find-one" -> Members.findOne();
            case "equality-query" -> Members.findBySSN(RandomData.NO_SSN);
            case "age-query" -> Members.findByAge(Integer.parseInt(noun));
            default -> {
                System.out.println(input + " is not a recognized command. Try help.");
                System.out.println();
            }
        }

        System.out.println("Execution time: " + Duration.between(
            start, Instant.now()).toMillis() + " ms");
        System.out.println();
    }

    private static void printHelp() {
        System.out.println(Strings.HELP_MESSAGE);
        System.out.println();
    }

    private static void printStatus() {
        JsonWriterSettings jsonWriterSettings = JsonWriterSettings.builder()
            .indent(true)
            .build();
            
        System.out.println("Status:");
        System.out.println();

        System.out.println("MONGODB_URI: " + Env.MONGODB_URI);
        System.out.println("SHARED_LIB_PATH: " + Env.SHARED_LIB_PATH);
        System.out.println("DATABASE_NAME: " + Env.DATABASE_NAME);
        System.out.println("COLLECTION_NAME: " + Env.COLLECTION_NAME);
        System.out.println("KEY_VAULT_NAMESPACE: " + Env.KEY_VAULT_NAMESPACE);
        System.out.println();

        System.out.println("Encrypted fields map:");
        System.out.println(Schemas.ENCRYPTED_FIELDS_MAP.toJson(jsonWriterSettings));
        System.out.println();

        System.out.println("Collection info:");
        System.out.println();
        for (Document c: DatabaseManagement.getDatabase().listCollections()) {
            System.out.println(c.toJson(jsonWriterSettings));
        }
    }
}
