package com.joelodom;

import io.github.cdimascio.dotenv.Dotenv;

public class Env {
    public static String MONGODB_URI;
    public static String SHARED_LIB_PATH;
    public static String DATABASE_NAME;
    public static String COLLECTION_NAME;

    // Force loading .env on startup
    private static final Env s_instance = new Env();

    /**
     * Loads our environment from the .env file. Remember to make a copy
     * of env_template and change the parameters there as needed.
     */
    private Env() {
        Dotenv dotenv = Dotenv.load();

        MONGODB_URI = dotenv.get("MONGODB_URI");
        SHARED_LIB_PATH = dotenv.get("SHARED_LIB_PATH");

        DATABASE_NAME = dotenv.get("DATABASE_NAME");
        COLLECTION_NAME = dotenv.get("COLLECTION_NAME");
    }
}
