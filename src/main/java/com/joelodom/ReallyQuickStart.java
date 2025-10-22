package com.joelodom;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Arrays;

public class ReallyQuickStart {
    public static void main(String[] args) {
        System.out.println("ReallyQuickStart demo: create collection, insert sample data, run a query.");

        String uri = System.getenv("MONGODB_URI");
        if (uri == null || uri.isEmpty()) {
            uri = "mongodb://localhost:27017";
        }

        try (MongoClient client = MongoClients.create(uri)) {
            MongoDatabase db = client.getDatabase("really_quickstart_db");
            String collName = "people";

            // Drop existing collection if present for a clean demo
            try {
                MongoCollection<Document> existing = db.getCollection(collName);
                existing.drop();
            } catch (Exception ignored) {
            }

            // Create collection and insert sample documents
            db.createCollection(collName);
            MongoCollection<Document> coll = db.getCollection(collName);

            Document a = new Document("name", "Alice").append("age", 30).append("email", "alice@example.com");
            Document b = new Document("name", "Bob").append("age", 22).append("email", "bob@example.com");
            Document c = new Document("name", "Carol").append("age", 41).append("email", "carol@example.com");

            coll.insertMany(Arrays.asList(a, b, c));
            System.out.println("Inserted 3 sample documents into collection '" + collName + "'.");

            // Query: people older than 25
            FindIterable<Document> results = coll.find(Filters.gt("age", 25));
            System.out.println("Query results for age > 25:");
            for (Document doc : results) {
                System.out.println(doc.toJson());
            }

        } catch (Exception e) {
            System.err.println("Error during demo: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}

