package com.joelodom;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

import org.bson.Document;

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

        maybeUnpackSharedLib();

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
            case "destroy-database" -> {
                DatabaseManagement.destroyDatabase();
                // Exit because we need to recreate keys (TODO, allow non-static creation)
                System.exit(0);
            }
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
            case "no-ssn-query" -> Members.findBySSN(RandomData.NO_SSN);
            case "age-query" -> Members.findByAge(Integer.parseInt(noun));
            case "compound-query" -> Members.compoundFilter(18, "000-00-0000");
            case "compact-collection" -> DatabaseManagement.compactCollection();

            /**
             * Secret commands for those reading the code who want to go deeper.
             */

            case "destroy-all-databases" -> DatabaseManagement.destroyAllDatabases();
            case "drop-collection" -> DatabaseManagement.dropCollection();
            case "lookup-demonstration" -> LookupDemonstration.lookupDemonstration();
            case "add-members-explicit" -> {
                if (noun == null) {
                    System.out.println(
                        "Please specify the number of members to add.");
                    System.out.println();
                } else {
                    ExplicitQE.addRandomMembers(Integer.parseInt(noun));
                }
            }
            case "encrypt-subdocument" -> EncryptSubdocument.encryptSubdocument();

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
        System.out.println("Status:");
        System.out.println();

        // URI is redacted because it has my Atlas password in it and some day
        // I'm going to show the demo and forget about that.
        //System.out.println("MONGODB_URI: " + Env.MONGODB_URI);
        System.out.println("SHARED_LIB_PATH: " + Env.SHARED_LIB_PATH);
        System.out.println("SHARED_LIB_JAR_PATH:" + Env.SHARED_LIB_JAR_PATH);
        System.out.println("DATABASE_NAME: " + Env.DATABASE_NAME);
        System.out.println("COLLECTION_NAME: " + Env.COLLECTION_NAME);
        System.out.println("KEY_VAULT_NAMESPACE: " + Env.KEY_VAULT_NAMESPACE);
        System.out.println();

        System.out.println("Encrypted fields map:");
        System.out.println(Utils.docToPrettyJSON(Schemas.ENCRYPTED_FIELDS_MAP));
        System.out.println();

        System.out.println("Collection info:");
        System.out.println();
        for (Document c: DatabaseManagement.getDatabase().listCollections()) {
            System.out.println(Utils.docToPrettyJSON(c));
        }
    }

    /**
     * This unpacks the automatic encryption shared library from the JAR file,
     * if it's configured to do so. See the README.
     */
    private static void maybeUnpackSharedLib() {
        if (Env.SHARED_LIB_JAR_PATH.length() == 0) {
            return; // using external library, not from JAR file
        }

        try {
            FileExtractor.extractResource(
                Env.SHARED_LIB_JAR_PATH,
                Env.SHARED_LIB_PATH);
        } catch (IOException e) {
            System.out.println(
                "Unable to extract shared library: " + e.getMessage());
        }
    }
}
