package com.joelodom;

import java.util.Arrays;

import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
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
            "keyId": {
                "$binary": {
                "base64": "L8PkFV9NRDqplyCYBRJwSQ==",
                "subType": "04"
                }
            },
            "path": "ssn",
            "bsonType": "string",
            "queries": {
                "queryType": "equality"
            }
            },
            {
            "keyId": {
                "$binary": {
                "base64": "xFjGKy8aSOuynKuIp8D8VQ==",
                "subType": "04"
                }
            },
            "path": "age",
            "bsonType": "int",
            "queries": {
                "queryType": "range",
                "max": 150,
                "min": 0
            }
            },
            {
            "keyId": {
                "$binary": {
                "base64": "/Sy2cShSSmiVzk53BTJk1Q==",
                "subType": "04"
                }
            },
            "path": "balance",
            "bsonType": "double",
            "queries": {
                "queryType": "range",
                "max": 100000.0,
                "min": -100000.0,
                "precision": 6.0
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

    public static final BsonBinary ssnKey, ageKey, balanceKey;

    static {
        /**
         * One of the features of this demonstration is that it can be configured
         * to use AWS KMS. The awsKey document below describes the AWS key and
         * is only used if the provider is configured to be AWS KMS.
         * 
         * See KeyManagement.java for more details.
        */

        BsonDocument awsKey = new BsonDocument();
        awsKey.put("provider", new BsonString(Env.KEY_PROVIDER));
        awsKey.put("key", new BsonString(Env.AWS_KMS_KEY_ARN));
        awsKey.put("region", new BsonString(Env.AWS_KMS_KEY_REGION));

        /**
         * If we just want the keys to be created for us and laid down as part
         * of a server-side schema map, we could pass BsonNull values for
         * KeyId and it would be easy. But for client-side schemas I need
         * to get the key id from the server.
         */

        final String SSN_KEY = "ssnKey";
        final String AGE_KEY = "ageKey";
        final String BALANCE_KEY = "balanceKey";

        BsonDocument ssnKeyDocument = DatabaseManagement.CLIENT_ENCRYPTION
            .getKeyByAltName(SSN_KEY);
        BsonDocument ageKeyDocument = DatabaseManagement.CLIENT_ENCRYPTION
            .getKeyByAltName(AGE_KEY);
        BsonDocument balanceKeyDocument = DatabaseManagement.CLIENT_ENCRYPTION
            .getKeyByAltName(BALANCE_KEY);

        if (ssnKeyDocument == null) {
            DataKeyOptions dataKeyOptions = new DataKeyOptions()
                .keyAltNames(Arrays.asList(SSN_KEY));
            if ("aws".equals(Env.KEY_PROVIDER)) {
                dataKeyOptions.masterKey(awsKey);
            }
            ssnKey = DatabaseManagement.CLIENT_ENCRYPTION.createDataKey(
                Env.KEY_PROVIDER, dataKeyOptions);
        }
        else {
            ssnKey = ssnKeyDocument.getBinary("_id");
        }

        if (ageKeyDocument == null) {
            DataKeyOptions dataKeyOptions = new DataKeyOptions()
                .keyAltNames(Arrays.asList(AGE_KEY));
            if ("aws".equals(Env.KEY_PROVIDER)) {
                dataKeyOptions.masterKey(awsKey);
            }
            ageKey = DatabaseManagement.CLIENT_ENCRYPTION.createDataKey(
                Env.KEY_PROVIDER, dataKeyOptions);
        }
        else {
            ageKey = ageKeyDocument.getBinary("_id");
        }

        if (balanceKeyDocument == null) {
            DataKeyOptions dataKeyOptions = new DataKeyOptions()
                .keyAltNames(Arrays.asList(BALANCE_KEY));
            if ("aws".equals(Env.KEY_PROVIDER)) {
                dataKeyOptions.masterKey(awsKey);
            }
            balanceKey = DatabaseManagement.CLIENT_ENCRYPTION.createDataKey(
                Env.KEY_PROVIDER, dataKeyOptions);
        }
        else {
            balanceKey = balanceKeyDocument.getBinary("_id");
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
                                ),
                        /**
                         * It's quite important to set min and max and precision
                         * for floating-point types, otherwise the storage overhead
                         * for QE becomes very large because it'll index the entire
                         * space of the data type to its max precision. See
                         * https://www.mongodb.com/docs/manual/core/queryable-encryption/fundamentals/encrypt-and-query/#configure-encrypted-fields-for-optimal-search-and-storage
                         * 
                         * Precision is in digits after the decimal point. See
                         * https://github.com/mongodb/specifications/blob/43d2c7bacd62249de8d2173bf8ee39e6fd7a686e/source/client-side-encryption/client-side-encryption.md#encryptopts
                         */
                        new BsonDocument()
                                .append("keyId", balanceKey)
                                .append("path", new BsonString("balance"))
                                .append("bsonType", new BsonString("double"))
                                .append("queries", new BsonDocument()
                                        .append("queryType", new BsonString("range"))
                                        .append("max", new BsonDouble(RandomData.MAX_BALANCE))
                                        .append("min", new BsonDouble(RandomData.MIN_BALANCE))
                                        .append("precision", new BsonDouble(RandomData.BALANCE_PRECISION))
                                )
                )));
    }
}
