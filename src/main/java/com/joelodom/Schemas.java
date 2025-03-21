package com.joelodom;

import java.util.Arrays;

import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;

import com.mongodb.client.model.vault.DataKeyOptions;

public class Schemas {

    /**
     * The encrypted fields map specifices which fields in the collection should
     * be encrypted, and which query type is allowed for the encrypted fields.
     *
     * One important note. For range queries it is HIGHLY RECOMMENDED that you
     * set min, max and precision. If you don't it'll cost way too much storage
     * overhead.
     * 
     * See https://www.mongodb.com/docs/manual/core/queryable-encryption/qe-create-encryption-schema/
     * and https://www.mongodb.com/docs/manual/core/queryable-encryption/fundamentals/encrypt-and-query/#std-label-qe-field-configuration
     * 
     * This schema map looks like this in JSON:

            {
            "fields": [
                {
                "keyId": <key id (null will create a key for you)>,
                "path": "ssn",
                "bsonType": "string",
                "queries": {
                    "queryType": "equality"
                }
                },
                {
                "keyId": <key id (null will create a key for you)>,
                "path": "age",
                "bsonType": "int",
                "queries": {
                    "queryType": "range",
                    "max": 150,
                    "min": 0
                }
                }
            ]
            }

     * 
     */
    
    public static final BsonDocument ENCRYPTED_FIELDS_MAP;

    /**
     * Generating the encrypted fields map is done with more gymnastics than
     * need be. This is because I want to demonstrate client- and server-side
     * schema maps for encryption. See the README and the comments below.
     */

    static {
        /**
         * If we just want the keys to be created for us and laid down as part
         * of a server-side schema map, we could pass BsonNull values for
         * KeyId and it would be easy. But for client-side schemas I need
         * to get the key id from the server.
         */

        BsonBinary ssnKey, ageKey;
        BsonDocument ssnKeyDocument = DatabaseManagement.CLIENT_ENCRYPTION
            .getKeyByAltName("ssnKey");
        BsonDocument ageKeyDocument = DatabaseManagement.CLIENT_ENCRYPTION
            .getKeyByAltName("ageKey");

        if (ssnKeyDocument == null) {
            ssnKey = DatabaseManagement.CLIENT_ENCRYPTION.createDataKey(
                "local", new DataKeyOptions().keyAltNames(
                    Arrays.asList("ssnKey")
                )
            );
        }
        else {
            ssnKey = ssnKeyDocument.getBinary("_id");
        }

        if (ageKeyDocument == null) {
            ageKey = DatabaseManagement.CLIENT_ENCRYPTION.createDataKey(
                "local", new DataKeyOptions().keyAltNames(
                    Arrays.asList("ageKey")
                )
            );
        }
        else {
            ageKey = ageKeyDocument.getBinary("_id");
        }

        /**
         * Now after all that hassle, we have the KeyIds. Again, the easy way
         * to do this is to just pass BsonNull and we'd only need the map
         * below. But for client-side schema validation we need the keys.
         */

        ENCRYPTED_FIELDS_MAP = new BsonDocument().append("fields",
                new BsonArray(Arrays.asList(
                        new BsonDocument()
                                .append("keyId", ssnKey)
                                .append("path", new BsonString("ssn"))
                                .append("bsonType", new BsonString("string"))
                                .append("queries", new BsonDocument()
                                        .append("queryType", new BsonString("equality"))),
                        new BsonDocument()
                                .append("keyId", ageKey)
                                .append("path", new BsonString("age"))
                                .append("bsonType", new BsonString("int"))
                                .append("queries", new BsonDocument()
                                        .append("queryType", new BsonString("range"))
                                        .append("max", new BsonInt32(RandomData.MAX_AGE))
                                        .append("min", new BsonInt32(0))
                                )
                )));
    }
}
