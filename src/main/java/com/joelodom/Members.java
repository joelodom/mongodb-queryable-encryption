package com.joelodom;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertManyResult;

public class Members {

    /**
     * This function adds random members to the encrypted collection.
     * 
     * TODO: Bulk inserts.
     */

    public static void addRandomMembers(int number) {
        /**
         * First we create a document with one field, ssn, which is encrypted.
         * 
         * TODO: Error if the database doesn't exist or it will create a
         * collection that doesn't have an encryption schema. Or add
         * client-side schema enforcement.
         */

        MongoCollection collection = DatabaseManagement.getEncryptedCollection();

        List<Document> documents = new ArrayList<>(number);

        while (documents.size() < number) {
            Document document = new Document("name",
                RandomData.generateRandomFullName())
                .append("ssn", RandomData.generateRandomSSN());
            documents.add(document);
        }

        InsertManyResult result = collection.insertMany(documents);

        if (result.wasAcknowledged()) {
            System.out.println("Successfully inserted " + number + " records.");
        }
        else {
            System.out.println("Failed to insert the records.");
        }

        System.out.println();
    }
}
