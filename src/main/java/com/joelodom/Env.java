package com.joelodom;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Loads our environment from the .env file on startup. Remember to make a copy
 * of env_template and change the parameters there as needed.
 */

public class Env {

    public static final String MONGODB_URI;
    public static final String SHARED_LIB_PATH;
    public static final String SHARED_LIB_JAR_PATH;

    public static final String DATABASE_NAME;
    public static final String COLLECTION_NAME;

    public static final String KEY_VAULT_DATABASE;
    public static final String KEY_VAULT_COLLECTION;
    public static final String KEY_VAULT_NAMESPACE;

    public static boolean VOID_WARRANTY = false;

    public static final String KEY_PROVIDER;

    public static final String AWS_KMS_KEY_ARN;
    public static final String AWS_KMS_KEY_REGION;
    public static final String AWS_KMS_ACCESS_KEY_ID;
    public static final String AWS_KMS_ACCESS_KEY_SECRET;

    static {
        Dotenv dotenv = Dotenv.load();

        MONGODB_URI = dotenv.get("MONGODB_URI");
        SHARED_LIB_PATH = dotenv.get("SHARED_LIB_PATH");
        SHARED_LIB_JAR_PATH = dotenv.get("SHARED_LIB_JAR_PATH");

        DATABASE_NAME = dotenv.get("DATABASE_NAME");
        COLLECTION_NAME = dotenv.get("COLLECTION_NAME");

        KEY_VAULT_DATABASE = dotenv.get("KEY_VAULT_DATABASE");
        KEY_VAULT_COLLECTION = dotenv.get("KEY_VAULT_COLLECTION");
        KEY_VAULT_NAMESPACE = KEY_VAULT_DATABASE + "." + KEY_VAULT_COLLECTION;

        String voidWarranty = dotenv.get("VOID_WARRANTY");
        if (voidWarranty != null) {
            VOID_WARRANTY = voidWarranty.equals("true");
        }

        KEY_PROVIDER = dotenv.get("KEY_PROVIDER");

        AWS_KMS_KEY_ARN = dotenv.get("AWS_KMS_KEY_ARN");
        AWS_KMS_KEY_REGION = dotenv.get("AWS_KMS_KEY_REGION");
        AWS_KMS_ACCESS_KEY_ID = dotenv.get("AWS_KMS_ACCESS_KEY_ID");
        AWS_KMS_ACCESS_KEY_SECRET = dotenv.get("AWS_KMS_ACCESS_KEY_SECRET");
    }
}
