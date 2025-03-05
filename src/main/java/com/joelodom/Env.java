package com.joelodom;

import io.github.cdimascio.dotenv.Dotenv;

public class Env {
    public static String MONGODB_URI;
    public static String SHARED_LIB_PATH;

    // Force loading .env on startup
    private static final Env s_instance = new Env();

    private Env() {
        // Load environment variables from the .env file
        Dotenv dotenv = Dotenv.load();
        MONGODB_URI = dotenv.get("MONGODB_URI");
        SHARED_LIB_PATH = dotenv.get("SHARED_LIB_PATH");
    }
}
