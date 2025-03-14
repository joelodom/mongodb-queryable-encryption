package com.joelodom;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.InsertManyResult;

public class Members {

    /**
     * You can only insert so many records at once, so we do this in batches.
     */
    private final static int BATCH_SIZE = 200; // estimated to be okay

    private final static JsonWriterSettings JSON_WRITER_SETTINGS
            = JsonWriterSettings.builder().indent(true).build();

    /**
     * This function adds random members to the encrypted collection. The
     * encryption is transparent to the function because of the encrypted schema
     * map.
     */
    public static void addRandomMembers(int number) {
        /**
         * First we create a document with an encrypted SSN and an encrypted
         * age. SSN is equality searchable and age is range searchable. Other
         * fields are unencrypted.
         *
         * TODO: Error if the database doesn't exist or it will create a
         * collection that doesn't have an encryption schema. Or add client-side
         * schema enforcement.
         */

        MongoCollection collection = DatabaseManagement.getEncryptedCollection();

        int inserted = 0;
        while (inserted < number) {
            int remaining = number - inserted;
            int toInsert = remaining > BATCH_SIZE ? BATCH_SIZE : remaining;

            List<Document> documents = new ArrayList<>(toInsert);

            while (documents.size() < toInsert) {
                Document document = new Document("name",
                        RandomData.generateRandomFullName())
                        .append("ssn", RandomData.generateRandomSSN())
                        .append("age", RandomData.generateRandomAge())
                        .append("zipCode", RandomData.generateRandomZipCode());
                documents.add(document);
            }

            InsertManyResult result = collection.insertMany(documents);

            if (!result.wasAcknowledged()) {
                System.out.println("Failed to insert the records.");
                System.out.println();
                return;
            }

            inserted += toInsert;
        }

        System.out.println("Successfully inserted " + inserted + " records.");
        System.out.println();
    }

    public static FindIterable<Document> find(BsonDocument filter) {
        /**
         * Automatic encryption means that the record will be retured decrypted.
         * Remember, this is done on the client side (in this application)
         * behind the scenes.
         *
         * In the projection, We tidy thing up by supressing the _id and
         * __safeContent__, which is encrypted metadata used for encrypted
         * queries and is not meant to be used directly.
         */
        MongoCollection<Document> collection
                = DatabaseManagement.getEncryptedCollection();
        return collection.find(filter)
                .projection(Projections.exclude("_id", "__safeContent__"));
    }

    public static void printDocument(Document doc) {
        if (doc == null) {
            System.out.println("No document to print.");
            return;
        }

        System.out.println(doc.toJson(JSON_WRITER_SETTINGS));
    }

    public static void printFindResults(FindIterable<Document> it) {
        int count = 0;
        for (Document doc : it) {
            printDocument(doc);
            count++;
        }
        System.out.println();
        System.out.println("Found " + count + " results.");
        System.out.println();
    }

    public static void findOne() {
        Document doc = find(new BsonDocument()).first();
        printDocument(doc);
    }

    public static void findBySSN(String ssn) {
        BsonDocument filter = new BsonDocument(
                "ssn", new BsonString(ssn)
        );
        printFindResults(find(filter));
    }

    public static void findByAge(int age) {
        // Constructs a filter: { "age": { "$lte": age } }
        BsonDocument filter = new BsonDocument(
                "age", new BsonDocument("$lte", new BsonInt32(age)));
        printFindResults(find(filter));
    }

    public static void compoundFilter(int age, String ssn) {
        /**
         *
         * {
         * "$and": [ { "age": { "$lte": 31 } }, { "ssn": { "$eq": "123-45-6789"
         * } } ] }
         */

        BsonDocument query = new BsonDocument(
                "age", new BsonDocument("$lte", new BsonInt32(age)))
                .append("ssn", new BsonString(ssn));
        printFindResults(find(query));
    }
}
