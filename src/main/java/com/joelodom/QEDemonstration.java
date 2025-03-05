package com.joelodom;

import io.github.cdimascio.dotenv.Dotenv;

public class QEDemonstration {
    public static String MONGODB_URI;
    public static String SHARED_LIB_PATH;

    public static void main(String[] args) {
        System.out.println(Strings.WELCOME_MESSAGE);
        loadEnv();
        printStatus();
    }

    private static void loadEnv() {
        // Load environment variables from the .env file
        Dotenv dotenv = Dotenv.load();
        MONGODB_URI = dotenv.get("MONGODB_URI");
        SHARED_LIB_PATH = dotenv.get("SHARED_LIB_PATH");
    }

    private static void printStatus() {
        System.out.println("MONGODB_URI: " + MONGODB_URI);
        System.out.println("SHARED_LIB_PATH: " + SHARED_LIB_PATH);
    }
}
