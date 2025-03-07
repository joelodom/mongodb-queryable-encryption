package com.joelodom;

import com.mongodb.ClientEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.CreateEncryptedCollectionParams;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;

public class DatabaseManagement {
    private static final MongoClient ENCRYPTED_MONGO_CLIENT
        = MongoClients.create(Env.MONGODB_URI);
    
    public static MongoClient getEncryptedClient() {
        return ENCRYPTED_MONGO_CLIENT;
    }

    public static MongoDatabase getDatabase() {
        return getEncryptedClient().getDatabase(Env.DATABASE_NAME);
    }

    /**
     * 
     */
    public static void createEncryptedCollection() {

        /**
         * A client encryption object is a helper object that allows you to do
         * various things such as some data key management and creating
         * encrypted collections. We set that up here.
         */

        ClientEncryptionSettings clientEncryptionSettings
            = ClientEncryptionSettings.builder()
                .keyVaultMongoClientSettings(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(Env.MONGODB_URI)).build())
                .keyVaultNamespace(Env.KEY_VAULT_NAMESPACE)
                .kmsProviders(KeyManagement.KMS_PROVIDER_CREDS)
                .build();

        ClientEncryption clientEncryption
            = ClientEncryptions.create(clientEncryptionSettings);

        /**
         * A QE-enabled collection should have a server-side schema to enforce
         * rejection of non-encrypted payloads that should be encrypted.
         */

        CreateCollectionOptions createCollectionOptions
            = new CreateCollectionOptions().encryptedFields(
                Schemas.ENCRYPTED_FIELDS_MAP);

        /**
         * Here's where we create the collection. Remember we're just using
         * a "local" key provider for this demonstration.
         */

        try {
            clientEncryption.createEncryptedCollection(
                    getDatabase(),
                    Env.COLLECTION_NAME,
                    createCollectionOptions,
                    new CreateEncryptedCollectionParams("local"));
        } 
        catch (Exception e) {
            System.out.println(
                "Unable to create encrypted collection: " + e.getMessage());
            System.out.println();
            return;
        }
            
        // getDatabase().createCollection(Env.COLLECTION_NAME);
        // System.out.println("Created collection " + Env.COLLECTION_NAME
        //     + " in database " + getDatabase().getName());
        // System.out.println();
    }

    /**
     * This drops the database without ceremony.
     * TODO: I need to drop the keyvault and all and remember to test
     * Can we add "undork dropCollection for QE collections to the backlog? If you run a drop collection command on a QE collection outside an encrypted session, it really screws things up.
     */
    public static void destroyDatabase() {
        ENCRYPTED_MONGO_CLIENT.getDatabase(Env.DATABASE_NAME).drop();
        System.out.println("Destryoed database " + getDatabase().getName());
        System.out.println();
    }
}
