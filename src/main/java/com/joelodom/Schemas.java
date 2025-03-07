package com.joelodom;

import java.util.Arrays;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonNull;
import org.bson.BsonString;

public class Schemas {
    /**
     * The encrypted fields map specifices which fields in the collection
     * should be encrypted, and which query type is allowed for the field.
     * See https://www.mongodb.com/docs/manual/core/queryable-encryption/qe-create-encryption-schema/#std-label-qe-create-encryption-schema.
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
                                .append("queryType", new BsonString("equality"))))));
    }
}
