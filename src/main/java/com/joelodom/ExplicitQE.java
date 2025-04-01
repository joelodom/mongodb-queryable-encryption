package com.joelodom;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonBinary;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.vault.EncryptOptions;
import com.mongodb.client.model.vault.RangeOptions;
import com.mongodb.client.result.InsertManyResult;

/**
 * TODO: Documentation
 * 
 * See
 * https://www.mongodb.com/docs/manual/core/queryable-encryption/fundamentals/manual-encryption/
 */

public class ExplicitQE {
    
    /**
     * TODO: Documentation
     */

    public static Document createExplicitMember() {
        Document member = RandomData.createRandomMember();

        BsonBinary encryptedSSN = DatabaseManagement.CLIENT_ENCRYPTION.encrypt(
            new BsonString(member.getString("ssn")),
            new EncryptOptions("Indexed").keyId(Schemas.ssnKey)
                .contentionFactor(8L));
        BsonBinary encryptedAge = DatabaseManagement.CLIENT_ENCRYPTION.encrypt(
            new BsonInt32(member.getInteger("age")),
            new EncryptOptions("Range").keyId(Schemas.ageKey)
                .contentionFactor(8L)
                .rangeOptions(new RangeOptions()
                    .min(new BsonInt32(0))
                    .max(new BsonInt32(RandomData.MAX_AGE))));

        member.put("ssn", encryptedSSN);
        member.put("age", encryptedAge);

        return member;
    }

    /**
     * You can only insert so many records at once, so we do this in batches.
     */
    private final static int BATCH_SIZE = 200; // estimated to be okay
    
    public static void addRandomMembers(int number) {
        /**
         * TODO
         */

        MongoClient unencryptedClient = MongoClients.create(Env.MONGODB_URI);
        MongoDatabase db = unencryptedClient.getDatabase(Env.DATABASE_NAME);
        MongoCollection collection = db.getCollection(Env.COLLECTION_NAME);

        int inserted = 0;
        while (inserted < number) {
            int remaining = number - inserted;
            int toInsert = remaining > BATCH_SIZE ? BATCH_SIZE : remaining;

            List<Document> documents = new ArrayList<>(toInsert);

            while (documents.size() < toInsert) {
                documents.add(createExplicitMember());
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
}