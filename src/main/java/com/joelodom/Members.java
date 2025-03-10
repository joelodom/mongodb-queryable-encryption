package com.joelodom;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;

public class Members {

    /**
     * This function adds a single random member to the encrypted collection.
     * 
     * TODO: Bulk inserts.
     */

    public static void addRandomMember() {
        /**
         * First we create a document with one field, ssn, which is encrypted.
         * 
         * TODO: Error if the database doesn't exist or it will create a
         * collection that doesn't have an encryption schema. Or add
         * client-side schema enforcement.
         */

        MongoCollection collection = DatabaseManagement.getEncryptedCollection();

        Document document = new Document("name",
            RandomData.generateRandomFullName())
            .append("ssn", RandomData.generateRandomSSN());

        InsertOneResult result = collection.insertOne(document);
        if (result.wasAcknowledged()) {
            System.out.println("Successfully inserted the record.");
        }
        else {
            System.out.println("Failed to insert the record.");
        }

        System.out.println();
    }
}
