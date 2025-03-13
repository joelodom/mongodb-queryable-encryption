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

    private static final Map<String, Object> EXTRA_OPTIONS = new HashMap<>();
    private static final AutoEncryptionSettings AUTO_ENCRYPTION_SETTINGS;
    private static final MongoClientSettings CLIENT_SETTINGS;
    private static final MongoClient ENCRYPTED_MONGO_CLIENT;

    static {
        /**
         * See the README which discusses the crypt_shared library and provides
         * some references.
         */

        EXTRA_OPTIONS.put("cryptSharedLibPath", Env.SHARED_LIB_PATH);

        // final Map<String, BsonDocument> SCHEMA_MAP = Map.of(
        //     Env.DATABASE_NAME + "." + Env.COLLECTION_NAME,
        //     Schemas.ENCRYPTED_FIELDS_MAP
        // );
        
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
         */

        AUTO_ENCRYPTION_SETTINGS = AutoEncryptionSettings.builder()
                .keyVaultNamespace(Env.KEY_VAULT_NAMESPACE)
                .kmsProviders(KeyManagement.KMS_PROVIDER_CREDS)
                .extraOptions(EXTRA_OPTIONS)
                //TODO   .schemaMap(SCHEMA_MAP)
                .build();

        CLIENT_SETTINGS = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(Env.MONGODB_URI))
                .autoEncryptionSettings(AUTO_ENCRYPTION_SETTINGS)
                .build();

        ENCRYPTED_MONGO_CLIENT = MongoClients.create(CLIENT_SETTINGS);
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
         * A client encryption object is a helper object that allows you to do
         * various things such as some data key management and creating
         * encrypted collections. We set that up here.
         */

        ClientEncryptionSettings clientEncryptionSettings
                = ClientEncryptionSettings.builder()
                        .keyVaultMongoClientSettings(MongoClientSettings.builder()
                                .applyConnectionString(
                                    new ConnectionString(Env.MONGODB_URI)).build())
                        .keyVaultNamespace(Env.KEY_VAULT_NAMESPACE)
                        .kmsProviders(KeyManagement.KMS_PROVIDER_CREDS)
                        .build();

        ClientEncryption clientEncryption
                = ClientEncryptions.create(clientEncryptionSettings);

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
         * "local" key provider for this demonstration. Normally you would use
         * a key vault.
         */

        clientEncryption.createEncryptedCollection(
                getDatabase(),
                Env.COLLECTION_NAME,
                createCollectionOptions,
                new CreateEncryptedCollectionParams("local"));

        System.out.println(
            "Created encrypted collection " + Env.COLLECTION_NAME);
        System.out.println();
    }

    /**
     * This drops the database without ceremony. It also drops the key
     * vault database. If you don't drop the key vault database it's not the
     * end of the world, but it wouldn't be helpful anymore and could grow
     * with useless keys if you rerun the demonstration.
     * 
     * 
     * TODO: test Can we add "undork dropCollection
     * for QE collections to the backlog? If you run a drop collection command
     * on a QE collection outside an encrypted session, it really screws things
     * up.
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
        }
        else {
            System.out.println("I'm sorry, Dev. I'm afraid I can't do that.");
        }

        System.out.println();
    }

    public static MongoCollection<Document> getEncryptedCollection() {
        return getDatabase().getCollection(Env.COLLECTION_NAME);
    }
}
