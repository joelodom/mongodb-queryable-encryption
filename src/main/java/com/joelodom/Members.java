package com.joelodom;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.InsertManyResult;

public class Members {

    /**
     * You can only insert so many records at once, so we do this in batches.
     */

    private final static int BATCH_SIZE = 200; // estimated to be okay

    /**
     * This function adds random members to the encrypted collection.
     */
    public static void addRandomMembers(int number) {
        /**
         * First we create a document with one field, ssn, which is encrypted.
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
                        .append("ssn", RandomData.generateRandomSSN());
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

    public static void findOne() {
        /**
         * Fetch the first record that find() finds. Automatic encryption means
         * that the record will be retured decrypted. Remember, this is done on
         * the client side (in this application) behind the scenes.
         * 
         * In the projection, I tidy thing up by supressing the _id and
         * __safeContent__, which is encrypted metadata used for encrypted
         * queries and is not meant to be used directly.
         */
        MongoCollection<Document> collection
                = DatabaseManagement.getEncryptedCollection();
        Document doc = collection.find()
                .projection(Projections.exclude("_id", "__safeContent__"))
                .first();

        if (doc == null) {
            System.out.println("Find() returned no results.");
        } else {
            JsonWriterSettings jsonWriterSettings = JsonWriterSettings.builder()
                    .indent(true)
                    .build();
            System.out.println(doc.toJson(jsonWriterSettings));
            System.out.println();
        }

        System.out.println();
    }
}
