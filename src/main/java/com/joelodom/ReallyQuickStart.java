package com.joelodom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.Document;

import com.mongodb.ClientEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.CreateEncryptedCollectionParams;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;

/**
 * For the most part, I've tried to make the printed output explain the code,
 * so you should be able to read that as if it were comments.
 */

public class ReallyQuickStart {
    public static void main(String[] args) {    
        String MONGODB_URI = "mongodb://localhost:27017";
        String DATABASE = "really_quickstart_db";
        String COLLECTION = "people";
        String KEY_VAULT_DB = "really_quickstart_keys";
        String KEY_VAULT_COLL = "__keyVault";
        String KEY_VAULT_NAMESPACE = KEY_VAULT_DB + "." + KEY_VAULT_COLL;
        String SECRET_PROVIDER = "local";
        byte[] SECRET_DEMO_KEY
            = "AAAAAAAAAA01234567890123456789012345678901234567890123456789012345678901234567890123456789012345"
            .getBytes();
        String KEY_PROVIDER = "local";


        String ENCRYPTED_FIELDS_MAP_JSON = """
        {
            "fields": [
                {
                    "path": "ssn",
                    "bsonType": "string",
                    "queries": { "queryType": "equality" }
                    "keyId": null
                }
            ]
        }
        """;


        System.out.println("""
        ****
        ABOUT THIS SAMPLE / DEMONSTRATION
        ****

        This is a Queryable Encryption quickstart and demonstration. It should do
        the following when run:

        1. It will create a new collection in the database. The collection will
        have several fields that are configured for queryable encryption.

        2. It will insert sample data into the collection.

        3. It will run sample queries against the encrypted fields and print the
        results.

        """);

        try (MongoClient client = MongoClients.create(MONGODB_URI)) {



            /********************/
            
            System.out.println("""
            Dropping the database and key vault if they exist (for a clean demo)...

            """);

            MongoDatabase db = client.getDatabase(DATABASE);
            try {
                db.drop();
                client.getDatabase(KEY_VAULT_DB).drop();
            } catch (Exception ignored) {
            }

            //System.exit(0);

            /********************/








            /********************/

            System.out.println("""
            BUILDING THE KMS PROVIDER CREDENTIALS

            Queryable Encryption supports cloud KMS providers, on-premises
            providers, and other patterns. The KMS_PROVIDER_CREDS map stores the
            necessary credentials for the KMS providers you wish to use.

            """);

            Map<String, Map<String, Object>> KMS_PROVIDER_CREDS
                = new HashMap<>();
            Map<String, Object> secretLocalProvider = new HashMap<>();
            secretLocalProvider.put("key", SECRET_DEMO_KEY);
            KMS_PROVIDER_CREDS.put(SECRET_PROVIDER, secretLocalProvider);

            /********************/







            /********************/

            System.out.println("""
            CREATING THE CLIENT ENCRYPTION OBJECT

            A client encryption object is a helper object that allows you to do
            various things such as some data key management and creating
            encrypted collections. We set that up here and use it to create the
            encrypted collection in a later step.

            """);

            ClientEncryption CLIENT_ENCRYPTION
                = ClientEncryptions.create(ClientEncryptionSettings.builder()
                    .keyVaultMongoClientSettings(MongoClientSettings.builder()
                        .applyConnectionString(
                            new ConnectionString(MONGODB_URI)).build())
                    .keyVaultNamespace(KEY_VAULT_NAMESPACE)
                    .kmsProviders(KMS_PROVIDER_CREDS)
                    .build());

            /********************/





            /********************/

            System.out.println("""
            CREATING THE ENCRYPTED COLLECTION

            The first step to show Queryable Encryption is to create a collection
            that has encrypted fields. The encrypted fields map (defined in JSON
            in the source code) is placed as metadata on the collection so that
            the client knows what to encrypt and with what query types, and so
            the server knows what encrypted data to expect and how to index it.

            """);

            BsonDocument ENCRYPTED_FIELDS_MAP = BsonDocument.parse(
                ENCRYPTED_FIELDS_MAP_JSON);

            CreateCollectionOptions createCollectionOptions =
                new CreateCollectionOptions().encryptedFields(ENCRYPTED_FIELDS_MAP);

            CLIENT_ENCRYPTION.createEncryptedCollection(
                db,
                COLLECTION,
                createCollectionOptions,
                new CreateEncryptedCollectionParams(KEY_PROVIDER));

            /********************/







            /********************/

            System.out.println("""
            INSERTING SAMPLE DOCUMENTS

            Place some documentation here.

            """);

            MongoCollection<Document> coll = db.getCollection(COLLECTION);
            coll.insertMany(Arrays.asList(
                new Document("name", "Alice")
                    .append("age", 30)
                    .append("email", "alice@example.com")
                    .append("ssn", "123-45-6789")
            ));

            /********************/





            // // Create collection and insert sample documents
            // db.createCollection(collName);
            // System.out.println("Inserted 3 sample documents into collection '" + collName + "'.");

            // // Query: people older than 25
            // FindIterable<Document> results = coll.find(Filters.gt("age", 25));
            // System.out.println("Query results for age > 25:");
            // for (Document doc : results) {
            //     System.out.println(doc.toJson());
            // }

        } catch (Exception e) {
            System.err.println("Error during sample: " + e.getMessage());
            e.printStackTrace(System.err);
        }

        System.out.println("""
        **** END OF SAMPLE ****

        """);
    }
}

