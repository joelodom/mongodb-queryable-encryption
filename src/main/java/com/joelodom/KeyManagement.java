package com.joelodom;

import java.util.HashMap;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonString;

import com.mongodb.client.model.vault.RewrapManyDataKeyOptions;

/**
 *
 * MongoDB Queryable Encryption supports different key providers, including AWS
 * KMS, GCP KMS, and Azure Key Vault. We also support KMIP, Hashicorp Vault, and
 * local key providers. We're going to use a local key provider for this
 * demonstration because it's just a demonstration. That said, a local key
 * provider may be useful in some production applications, such as injecting a
 * key via an environment variable or via a sidecar process.
 * 
 * ENHANCEMENTS:
 * 
 *   On-demand callback for AWS credentials (https://jira.mongodb.org/browse/DRIVERS-2011)
 *   Demonstrate key rotation
 *   Demonstrate multiple providers (including cloud providers)
 *   Demonstrate changing providers
 *   Demonstrate key refresh interval change
 *
 */

public class KeyManagement {

    /**
     * The key should be 96 bytes. It's hardcoded here because this is just a
     * demonstration. For an example that uses a key vault, see
     * https://www.mongodb.com/docs/manual/core/queryable-encryption/qe-create-cmk/.
     */

    private static final byte[] DEMO_KEY
        = "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345"
            .getBytes();

    /**
     * The KMS provider credentials is a data structure that explains to
     * queryable encryption how to access whatever KMS provider(s) are in use.
     * See
     * https://www.mongodb.com/docs/manual/core/queryable-encryption/fundamentals/kms-providers/#std-label-qe-fundamentals-kms-providers
     */
    
    public static final Map<String, Map<String, Object>> KMS_PROVIDER_CREDS
            = new HashMap<>();

    static { // initialize on startup
        Map<String, Object> localProvider = new HashMap<>();
        localProvider.put("key", DEMO_KEY);
        KMS_PROVIDER_CREDS.put("local", localProvider);

        /**
         * You can also configure this demonstration to use AWS KMS. To make
         * it work you need to setup your AWS KMS as described at
         * https://www.mongodb.com/docs/manual/core/csfle/tutorials/aws/aws-automatic/
         * (You may notice that link is for CSFLE, but the key provider prinicples
         * are the same.)
         * 
         * Once you have your AWS KMS set up, you'll update your .env file with
         * the parameters described in the link above and in env_template.
         * 
         * There are some additional resources at
         * https://www.mongodb.com/docs/manual/core/queryable-encryption/qe-create-cmk/
         * and advanced reading material at
         * https://github.com/mongodb/specifications/blob/master/source/auth/auth.md
         * 
         * For posterity, my IAM user is named qe-demo-key-user and the policy
         * enc-dec-with-key is attached to qe-demo-key-user. The policy itself
         * looks like the following.
         * 
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Sid": "VisualEditor0",
                            "Effect": "Allow",
                            "Action": [
                                "kms:Decrypt",
                                "kms:Encrypt"
                            ],
                            "Resource": "arn:aws:kms:us-east-1:700633163637:key/1a9eeae0-8de1-4771-93c3-72d5d5feffa3"
                        }
                    ]
                }
         *     
         * ASSUMING AN AWS ROLE INSTEAD OF USING A LONG-LIVED SECRET
         * 
         * You'll notice that in the documentation at
         * https://www.mongodb.com/docs/manual/core/csfle/tutorials/aws/aws-automatic/#configure-the-mongoclient
         * you're advised to use an AWS IAM role to authenticate to your KMS.
         * To put that differently, you'll still use an IAM user, but instead
         * of giving that user long-term permissions to use the KMS, you'll
         * allow that user to assume an AWS IAM role temporarily. This avoids
         * long-term secret management and makes access control more robust.
         * 
         * Here is how I configured that.
         * 
         *   1. I removed the enc-dec-with-key policy for qe-demo-user
         *      (see above). The user can no longer use the key directly.
         * 
         *   2. I created a role called qe-demo-role and attached the policy
         *      use-qe-demo-key. It's the same as the policy seen above but
         *      is now attached to the role and not the user.
         * 
         * Next, I used aws configure --profile qe-demo-user on my Mac to
         * set up that user. I used the same credentials as above. Then I used
         * aws sts assume-role --role-arn arn:aws:iam::700633163637:role/qe-demo-role --role-session-name QESession --profile qe-demo-user
         * to get the short-term credentials to assume the role.
         * 
         * You may export those short-term credentials as follows:
         * 
                export AWS_ACCESS_KEY_ID="..."
                export AWS_SECRET_ACCESS_KEY="..."
                export AWS_SESSION_TOKEN="..."
         *
         * and set the awsProvider map below to an empty HashMap, or you may do
         * what I've done, which is to add the session token to the key
         * provider (see env-template). If you take this approach, you'll have
         * to manage AWS sessions in your application.
         */

        Map<String, Object> awsProvider = new HashMap<>();
        awsProvider.put("accessKeyId", Env.AWS_KMS_ACCESS_KEY_ID);
        awsProvider.put("secretAccessKey", Env.AWS_KMS_ACCESS_KEY_SECRET);
        if (Env.USE_AWS_ASSUME_ROLE) {
            awsProvider.put("sessionToken", Env.AWS_KMS_SESSION_TOKEN);
        }
        KMS_PROVIDER_CREDS.put("aws", awsProvider);
    }

    /**
     * To understand key rotation, you have to understand how Queryable
     * Encryption wraps keys. At the root level of the key hierarchy, there
     * is the customer master key (CMK). This is used to encrypt one data
     * encryption key (DEK) per field. When you rotate your CMK, new DEKs
     * are encrypted with the old DEK. See
     * https://www.mongodb.com/docs/manual/core/queryable-encryption/fundamentals/manage-keys/
     * 
     * So rotating your CMK doesn't re-encrypt existing DEKs. You can show this
     * by creating a colletion with this demonstration and looking at your
     * key vault (see env_template for the default location). You'll see one
     * key per encrypted field in there. If you rotate your AWS key, the wrapped
     * key won't change.
     * 
     * If you're wondering how your client can use the old keys if they haven't
     * been re-encrypted (rewrapped), it's because "AWS KMS saves all previous
     * versions of the cryptographic material in perpetuity so you can decrypt
     * any data encrypted with that KMS key." Key rotation in this context is
     * not so much about destroying old keys as it is about key wearout.
     * 
     * (https://docs.aws.amazon.com/kms/latest/developerguide/rotate-keys.html)
     * 
     * Remember that the CMK is only used to encrypt and decrypt the individual
     * data keys. The client only needs Encrypt and Decrypt permission on the
     * key in the KMS because it sends the encrypted / decrypted keys to the
     * KMS for decrypting / encrypting. The client should never be able to
     * access the actual CMK.
     * 
     * If you do need to rewrap your keys in the new CMK, it's easy. See below.
     */

    public static void rewrapDataKeys() {
        /** See
         * 
         * https://www.mongodb.com/docs/manual/reference/method/KeyVault.rewrapManyDataKey
         * and
         * https://mongodb.github.io/mongo-java-driver/4.9/apidocs/mongodb-driver-core/com/mongodb/client/model/vault/RewrapManyDataKeyOptions.html
         * 
         * for the reference material on this API, which says:
         * 
         * """
         * If the kmsProvider is "aws" the master key is required and must
         * contain the following fields:
         *   region: a String containing the AWS region in which to locate the master key
         *   key: a String containing the Amazon Resource Name (ARN) to the AWS customer master key
         * """
         * 
         * If you rewrap keys, you'll see their original creation date stays
         * the same in the key vault, but their updated date changes.
         */

        RewrapManyDataKeyOptions options = new RewrapManyDataKeyOptions()
            .provider(Env.KEY_PROVIDER)
            .masterKey(new BsonDocument()
                .append("region", new BsonString(Env.AWS_KMS_KEY_REGION))
                .append("key", new BsonString(Env.AWS_KMS_KEY_ARN)
            )
        );

        DatabaseManagement.CLIENT_ENCRYPTION.rewrapManyDataKey(
            new BsonDocument(), options);
    }
}
