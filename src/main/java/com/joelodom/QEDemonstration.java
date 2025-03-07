package com.joelodom;

import java.util.Scanner;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;

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
            case "help" -> printHelp();
            case "status" -> printStatus();
            case "create-collection" -> DatabaseManagement.createEncryptedCollection();
            case "destroy-database" -> DatabaseManagement.destroyDatabase();
            case "add-member" -> Members.addRandomMember();
            default -> {
                System.out.println(input + " is not a recognized command. Try help.");
                System.out.println();
            }
        }
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
