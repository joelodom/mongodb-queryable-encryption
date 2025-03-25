package com.joelodom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertManyResult;

/**
 * This is to demonstrate how $lookup and QE behave together.
 * Comments are inline.
 * 
 * TODO: Reference new documentation here and note THIS WILL NOT WORK
 * pre MongoDB 8.1 and Java driver 5.4.0. You'll also need crypt-shared 8.1+.
 */

public final class LookupDemonstration {
    private static final String LOCATIONS_COLLECTION_NAME = "locations";
    private static final int NUMBER_OF_LOCATIONS = RandomData.NUMBER_OF_ZIP_CODES;

    public static void lookupDemonstration() {
        /**
         * This demonstration is intended to be run with a clean database,
         * so first we create the members collection and add some members.
         * If the database is already created, destroy it first.
         */

        System.out.println("Creating members collection...");
        DatabaseManagement.createEncryptedCollection();

        final int MEMBERS_TO_ADD = 5000;
        System.out.println("Adding " + MEMBERS_TO_ADD + " members...");
        Members.addRandomMembers(MEMBERS_TO_ADD);

        /**
         * Add a new collection that has information about each of our
         * locations.
         */

        System.out.println("Creating the locations colletion...");

        // Create the collection
        MongoDatabase db = DatabaseManagement.getDatabase();
        db.createCollection(LOCATIONS_COLLECTION_NAME);
        MongoCollection locationsCollection
            = db.getCollection(LOCATIONS_COLLECTION_NAME);

        // Populate the collection

        List<Document> documents = new ArrayList<>(NUMBER_OF_LOCATIONS);
        while (documents.size() < NUMBER_OF_LOCATIONS) {
            Document document = new Document("city",
                    RandomData.getRandomCity())
                    .append("zipCode", RandomData.generateRandomZipCode());
            documents.add(document);
        }

        InsertManyResult result = locationsCollection.insertMany(documents);
        if (!result.wasAcknowledged()) {
            System.out.println("Failed to insert the records.");
            System.out.println();
            return;
        }

        System.out.println(
            "Successfully inserted " + NUMBER_OF_LOCATIONS + " locations.");
        System.out.println();

        /**
         * Now lookup members by location.
         */

        // Create the $lookup stage to join with the "members" collection on zipCode
        Document lookupStage = new Document("$lookup", 
            new Document("from", "members")
                .append("localField", "zipCode")
                .append("foreignField", "zipCode")
                .append("as", "memberInfo")
        );

        // Unwind
        Document unwindStage = new Document("$unwind",
            new Document("path", "$memberInfo"));

        // Project out metadata
        Document projectStage = new Document("$project",
            new Document("_id", 0)
            .append("memberInfo._id", 0)
            .append("memberInfo.__safeContent__", 0)
        );

        // Run the aggregation pipeline
        AggregateIterable<Document> results = locationsCollection.aggregate(
            Arrays.asList(lookupStage, unwindStage, projectStage));

        // Iterate through and print each resulting document
        int count = 0;
        for (Document doc : results) {
            System.out.println(Utils.docToPrettyJSON(doc));
            ++count;
        }

        System.out.println("$lookup returned " + count + " documents.");
        System.out.println();
    }
}
