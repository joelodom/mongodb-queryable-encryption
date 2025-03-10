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
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", DEMO_KEY);
        KMS_PROVIDER_CREDS.put("local", keyMap);
    }
}
