package com.joelodom;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DatabaseManagement {
    private static final MongoClient ENCRYPTED_MONGO_CLIENT = MongoClients.create(Env.MONGODB_URI);
    
    public static MongoClient getEncryptedClient() {
        return ENCRYPTED_MONGO_CLIENT;
    }

    public static MongoDatabase getDatabase() {
        return getEncryptedClient().getDatabase(Env.DATABASE_NAME);
    }

    /**
     * 
     */
    public static void createDatabase() {
        getDatabase().createCollection(Env.COLLECTION_NAME);
        System.out.println("Created database " + getDatabase().getName());
        System.out.println();
    }

    /**
     * 
     */
    public static void destroyDatabase() {
        ENCRYPTED_MONGO_CLIENT.getDatabase(Env.DATABASE_NAME).drop();
        System.out.println("Destryoed database " + getDatabase().getName());
        System.out.println();
    }
}
