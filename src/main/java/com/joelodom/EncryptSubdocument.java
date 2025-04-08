package com.joelodom;

import java.util.Arrays;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.Document;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.CreateEncryptedCollectionParams;

/**
 * This is a simple example of how you can encrypt a subdocument with
 * Queryable Encryption. The subdocument is not queryable so "queries" is
 * omitted from the encrypted fields map.
 */

public class EncryptSubdocument {
    /**
     * If I ever make this a formal part of the demonstration, I should move
     * the sample data into appropriate classes in this project.
     */
    
    public static final Document DOCUMENT = new Document("name", "Alice")
        .append("age", 30)
        .append("address", new Document("street", "123 Maple Street")
            .append("city", "Springfield")
            .append("state", "IL")
            .append("zip", "62704"));

    public static final BsonDocument ENCRYPTED_FIELDS_MAP
        = new BsonDocument().append("fields",
                new BsonArray(Arrays.asList(
                        new BsonDocument()
                                .append("keyId", new BsonNull())
                                .append("path", new BsonString("address"))
                                .append("bsonType", new BsonString("object"))
                                //.append("queries", new BsonDocument())
                )));
    
    public static final String COLLECTION_NAME = "encryptedSubdocumentCollection";

    public static void encryptSubdocument() {
        MongoDatabase db = DatabaseManagement.getDatabase();

        /**
         * Create the encrypted collection in the same database as the rest
         * of this demonstration.
         */

        CreateCollectionOptions createCollectionOptions
                = new CreateCollectionOptions().encryptedFields(
                        EncryptSubdocument.ENCRYPTED_FIELDS_MAP);

        DatabaseManagement.CLIENT_ENCRYPTION.createEncryptedCollection(
                db,
                EncryptSubdocument.COLLECTION_NAME,
                createCollectionOptions,
                new CreateEncryptedCollectionParams(Env.KEY_PROVIDER));

        System.out.println(
                "Created encrypted collection, " + EncryptSubdocument.COLLECTION_NAME + ".");

        /**
         * Insert the subdocument (with encryption)
         */

        db.getCollection(EncryptSubdocument.COLLECTION_NAME).insertOne(
            DOCUMENT);

        System.out.println(
            "Inserted a document with an encrypted subdocument.");

        System.out.println();
    }
}
