package com.joelodom;

import java.util.HashMap;
import java.util.Map;

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
         * One thing you'll notice is that this version of this demonstration
         * uses a secrets-based access pattern for the AWS KMS. I need to
         * show how to use a more modern and secure role-based access pattern.
         * The fun never ends.
         */
        
        Map<String, Object> awsProvider = new HashMap<>();
        awsProvider.put("accessKeyId", Env.AWS_KMS_ACCESS_KEY_ID);
        awsProvider.put("secretAccessKey", Env.AWS_KMS_ACCESS_KEY_SECRET);
        KMS_PROVIDER_CREDS.put("aws", awsProvider);
    }
}
