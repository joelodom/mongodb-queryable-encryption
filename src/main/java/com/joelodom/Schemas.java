package com.joelodom;

import java.util.Arrays;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;

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

    static {
        ENCRYPTED_FIELDS_MAP = new BsonDocument().append("fields",
                new BsonArray(Arrays.asList(
                        new BsonDocument()
                                .append("keyId", new BsonNull())
                                .append("path", new BsonString("ssn"))
                                .append("bsonType", new BsonString("string"))
                                .append("queries", new BsonDocument()
                                        .append("queryType", new BsonString("equality"))),
                        new BsonDocument()
                                .append("keyId", new BsonNull())
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
