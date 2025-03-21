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
                "keyId": null,
                "path": "ssn",
                "bsonType": "string",
                "queries": {
                    "queryType": "equality"
                }
                },
                {
                "keyId": null,
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
     * We can send null for data key ids and they will be generated on the server,
     * but if you're using server- and client-side schema enforcement, it's
     * better just to generate them explicitly as part of the schema.
     * 
     * The schema is pulled from the server after the collection is created,
     * so the same data keys are reused. TODO: rationalize this with client-side and generally update this comment
     */

    static {
        BsonBinary ssnKey, ageKey;
        BsonDocument ssnKeyDocument = DatabaseManagement.CLIENT_ENCRYPTION
            .getKeyByAltName("ssnKey");
        BsonDocument ageKeyDocument = DatabaseManagement.CLIENT_ENCRYPTION
            .getKeyByAltName("ssnKey");

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
