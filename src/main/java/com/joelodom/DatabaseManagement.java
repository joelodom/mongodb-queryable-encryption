package com.joelodom;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DatabaseManagement {

    /**
     * 
     */
    public static void createDatabase() {
        MongoClient encryptedClient = MongoClients.create(Env.MONGODB_URI);
        MongoDatabase database = encryptedClient.getDatabase(Env.DATABASE_NAME);
        database.createCollection(Env.COLLECTION_NAME);
    }

    /**
     * 
     */
    public static void destroyDatabases() {

    }
}
