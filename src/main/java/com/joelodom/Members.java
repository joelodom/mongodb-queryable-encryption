package com.joelodom;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;

public class Members {
    public static void addRandomMember() {
        // TODO: Document this and error out if the database doesn't already exist!

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
    }
}
