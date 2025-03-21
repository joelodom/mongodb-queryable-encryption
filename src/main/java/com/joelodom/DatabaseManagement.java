package com.joelodom;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

import com.mongodb.AutoEncryptionSettings;
import com.mongodb.ClientEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.CreateEncryptedCollectionParams;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;

/**
 * This is the database management class. It acts as a static class and is used
 * to do things like create the database with the encrypted collection.
 */
public class DatabaseManagement {

    private static final MongoClient ENCRYPTED_MONGO_CLIENT;
    public static final ClientEncryption CLIENT_ENCRYPTION;
    private static final Map<String, Object> EXTRA_OPTIONS = new HashMap<>();

    //public static final BsonBinary DATA_KEY_1, DATA_KEY_2;  TODO delete if unneeded
    static {
        /**
         * See the README which discusses the crypt_shared library.
         */

        EXTRA_OPTIONS.put("cryptSharedLibPath", Env.SHARED_LIB_PATH);

        /**
         * A client encryption object is a helper object that allows you to do
         * various things such as some data key management and creating
         * encrypted collections. We set that up here and use it in other
         * places, too.
         */
        ClientEncryptionSettings clientEncryptionSettings
                = ClientEncryptionSettings.builder()
                        .keyVaultMongoClientSettings(MongoClientSettings.builder()
                                .applyConnectionString(
                                        new ConnectionString(Env.MONGODB_URI)).build())
                        .keyVaultNamespace(Env.KEY_VAULT_NAMESPACE)
                        .kmsProviders(KeyManagement.KMS_PROVIDER_CREDS)
                        .build();

        CLIENT_ENCRYPTION = ClientEncryptions.create(clientEncryptionSettings);

        /**
         * Automatic encryption is a QE feature that allows you to insert into
         * the database and query the database without having to specify what to
         * encrypt every time. The encryption and decryption become transparent
         * to your client.
         *
         * When you're using server-side schema validation, if you try to insert
         * into the database without encryption (maybe something in the code
         * changes accidentally and the client omits the automatic encryption),
         * you will see an error alerting you that the field that was supposed
         * to be encrypted wasn't.
         *
         * The AutoEncryptionSettings object sets up automatic encryption and is
         * applied to the MongoClientSettings so that your Mongo Client can
         * perform the automatic encryption during the session.
         *
         * TODO: Work on client schema map and discussion. Review this comment.
         *
         */
        AutoEncryptionSettings autoEncryptionSettings = AutoEncryptionSettings.builder()
                .keyVaultNamespace(Env.KEY_VAULT_NAMESPACE)
                .kmsProviders(KeyManagement.KMS_PROVIDER_CREDS)
                .extraOptions(EXTRA_OPTIONS)
                //.encryptedFieldsMap(clientSchemaMap)  TODO
                .build();

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(Env.MONGODB_URI))
                .autoEncryptionSettings(autoEncryptionSettings)
                .build();

        ENCRYPTED_MONGO_CLIENT = MongoClients.create(clientSettings);
    }

    public static MongoClient getEncryptedClient() {
        return ENCRYPTED_MONGO_CLIENT;
    }

    public static MongoDatabase getDatabase() {
        return getEncryptedClient().getDatabase(Env.DATABASE_NAME);
    }

    public static MongoDatabase getKeyVault() {
        return getEncryptedClient().getDatabase(Env.KEY_VAULT_DATABASE);
    }

    public static void createEncryptedCollection() {
        /**
         * A QE-enabled collection should have a server-side schema to enforce
         * rejection of non-encrypted payloads that should be encrypted. This
         * bit of code creates the collection with the encryption schema.
         *
         * There are also some metadata collections and an encrypted key vault
         * collection that are at this time. See
         * https://www.mongodb.com/docs/manual/core/queryable-encryption/fundamentals/manage-collections/
         * for more information about those.
         */

        CreateCollectionOptions createCollectionOptions
                = new CreateCollectionOptions().encryptedFields(
                        Schemas.ENCRYPTED_FIELDS_MAP);

        /**
         * Here's where we create the collection. Remember we're just using a
         * "local" key provider for this demonstration. Normally you would use a
         * key vault.
         */
        CLIENT_ENCRYPTION.createEncryptedCollection(
                getDatabase(),
                Env.COLLECTION_NAME,
                createCollectionOptions,
                new CreateEncryptedCollectionParams("local"));

        System.out.println(
                "Created encrypted collection, " + Env.COLLECTION_NAME + ".");
        System.out.println();
    }

    /**
     * This drops the database without ceremony. It also drops the key vault
     * database. If you don't drop the key vault database it's not the end of
     * the world, but it wouldn't be helpful anymore and could grow with useless
     * keys if you rerun the demonstration.
     *
     *
     * TODO: test Can we add "undork dropCollection for QE collections to the
     * backlog? If you run a drop collection command on a QE collection outside
     * an encrypted session, it really screws things up.
     */
    public static void destroyDatabase() {
        getDatabase().drop();
        getKeyVault().drop();

        System.out.println("Destroyed databases "
                + getDatabase().getName() + " and " + getKeyVault().getName());
        System.out.println();
    }

    /**
     * Reverse engineer this and use at your own risk.
     */
    public static void destroyAllDatabases() {
        if (Env.VOID_WARRANTY) {
            Set RESERVED_SET = Set.of("admin", "config", "local");
            MongoIterable<String> databaseNames
                    = ENCRYPTED_MONGO_CLIENT.listDatabaseNames();
            for (String dbName : databaseNames) {
                if (RESERVED_SET.contains(dbName)) {
                    continue;
                }
                System.out.println("Dropping database: " + dbName);
                ENCRYPTED_MONGO_CLIENT.getDatabase(dbName).drop();
            }
        } else {
            System.out.println("I'm sorry, Dev. I'm afraid I can't do that.");
        }

        System.out.println();
    }

    public static MongoCollection<Document> getEncryptedCollection() {
        return getDatabase().getCollection(Env.COLLECTION_NAME);
    }

    /**
     * Queryable Encryption collections use semi-hidden metadata collections to
     * store encrypted index data (Reference
     * https://www.mongodb.com/docs/manual/core/queryable-encryption/fundamentals/manage-collections/).
     * An encrypted client is aware of these collections and drops them when you
     * drop an encrypted collection. This shows that unencrypted clients do not
     * clean up the metadata collections, something we can and should remedy.
     *
     * Normally the MongoDB Atlas UI and Compass will hide these metadata
     * collections, but I use mongosh to see them.
     */
    public static void dropCollection() {
        // I have affirmed this drops the aux collections
        getEncryptedCollection().drop();
        System.out.println(
                "Dropped " + Env.COLLECTION_NAME + " with encrypted client.");

        // I have affirmed thit LEAVES the aux collections
        // MongoClient unencryptedClient = MongoClients.create(Env.MONGODB_URI);
        // unencryptedClient.getDatabase(
        //     Env.DATABASE_NAME).getCollection(Env.COLLECTION_NAME).drop();
        // System.out.println(
        //     "Dropped " + Env.COLLECTION_NAME + " WITHOUT encrypted client.");
        System.out.println();
    }

    // /**
    //  * Server-side enforcement of the encryption schema is nice because it
    //  * prevents mistakes that would otherwise allow the server to store
    //  * unencrypted data. The nice thing about client-side enforcement is that it
    //  * prevents a malicious server admin from changing the schema undetected.
    //  * 
    //  * Normally you'd enable one or the other on an encrypted collection. But
    //  * you can enable both. The trick is that the client-side schema has to have
    //  * the same key IDs as the server-side schema, which is why we have to
    //  * lay down the encrypted collection on the server first then use the
    //  * minor gymnastics in Schemas.getEncryptedFieldsMapWithKeyIds to get the
    //  * Key IDs.
    //  */

    // public static void enableClientEnforcement() {
    //     // Get the encrypted fields map that contains the key IDs

    //     BsonDocument encryptedFieldsMap
    //         = Schemas.getEncryptedFieldsMapWithKeyIds();

    //     if (encryptedFieldsMap == null) {
    //         System.out.println(
    //             "Unable to extract the client fields map with KeyIds");
    //         System.out.println();
    //         return;
    //     }

    //     // Create a client schema map from the schema
        
    //     Map<String, BsonDocument> clientSchemaMap = new HashMap<>();
    //     clientSchemaMap.put(Env.DATABASE_NAME + "." + Env.COLLECTION_NAME,
    //         encryptedFieldsMap);

    //     // Go through the same process described above to re-create the
    //     // client, but now with the client schema map

    //     AutoEncryptionSettings autoEncryptionSettings
    //         = AutoEncryptionSettings.builder()
    //             .keyVaultNamespace(Env.KEY_VAULT_NAMESPACE)
    //             .kmsProviders(KeyManagement.KMS_PROVIDER_CREDS)
    //             .extraOptions(EXTRA_OPTIONS)
    //             .encryptedFieldsMap(clientSchemaMap)
    //             .build();

    //     MongoClientSettings clientSettings = MongoClientSettings.builder()
    //             .applyConnectionString(new ConnectionString(Env.MONGODB_URI))
    //             .autoEncryptionSettings(autoEncryptionSettings)
    //             .build();

    //     ENCRYPTED_MONGO_CLIENT = MongoClients.create(clientSettings);

    //     System.out.println("The client now has a client-side schema map.");
    //     System.out.println();
    // }
}
