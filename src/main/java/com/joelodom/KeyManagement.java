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

        
        // !!!!!!
        // https://www.mongodb.com/docs/manual/core/csfle/tutorials/aws/aws-automatic/#grant-permissions
        // https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_common-scenarios_non-aws.html
        // !!!!!!
        
        // https://www.mongodb.com/docs/manual/core/queryable-encryption/qe-create-cmk/
        // https://jira.mongodb.org/browse/DRIVERS-2280
        // https://github.com/mongodb/specifications/blob/master/source/auth/auth.md
        // https://github.com/mongodb/mongo-c-driver/commit/3ed55ed9b01a22e8208f9f382c9a976645bdbe4a#diff-6611c8b7be663c2fc9c7942692d314e8464f9398d0275e7ce691905fd5eeeeb0R700

        Map<String, Object> awsProvider = new HashMap<>();
        awsProvider.put("accessKeyId", Env.AWS_KMS_ACCESS_KEY_ID);
        awsProvider.put("secretAccessKey", Env.AWS_KMS_ACCESS_KEY_SECRET);
        KMS_PROVIDER_CREDS.put("aws", awsProvider);
    }
}
