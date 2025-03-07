package com.joelodom;

import io.github.cdimascio.dotenv.Dotenv;

public class Env {
    public static String MONGODB_URI;
    public static String SHARED_LIB_PATH;

    public static String DATABASE_NAME;
    public static String COLLECTION_NAME;

    public static String KEY_VAULT_DATABASE;
    public static String KEY_VAULT_COLLECTION;
    public static String KEY_VAULT_NAMESPACE;

    /**
     * Loads our environment from the .env file on startup. Remember to make a
     * copy of env_template and change the parameters there as needed.
     */
    static {
        Dotenv dotenv = Dotenv.load();

        MONGODB_URI = dotenv.get("MONGODB_URI");
        SHARED_LIB_PATH = dotenv.get("SHARED_LIB_PATH");

        DATABASE_NAME = dotenv.get("DATABASE_NAME");
        COLLECTION_NAME = dotenv.get("COLLECTION_NAME");

        DATABASE_NAME = dotenv.get("DATABASE_NAME");
        COLLECTION_NAME = dotenv.get("COLLECTION_NAME");
        KEY_VAULT_NAMESPACE = KEY_VAULT_DATABASE + "." + KEY_VAULT_COLLECTION;
    }
}
