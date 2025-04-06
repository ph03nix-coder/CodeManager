package com.ph03nix.functionalproject.Security;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.security.*;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SecureKeyManager {
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private final String keyAlias;

    public SecureKeyManager() {
        this.keyAlias = "hmac_gestores";
    }

    public SecretKey getOrCreateHmacKey() {

        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);

            if (keyStore.containsAlias(keyAlias)) {
                KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry(keyAlias, null);
                return entry.getSecretKey();
            }

            return generateNewHmacKey();

        } catch (Exception e) {
            Log.e("SecureKeyManager", "Error general: " + e.getMessage(), e);
            return null;
        }
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
    private SecretKey generateNewHmacKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_HMAC_SHA256,
                    ANDROID_KEYSTORE
            );

            KeyGenParameterSpec keySpec = new KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY
            )
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .build();

            keyGenerator.init(keySpec);
            return keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException | NoSuchProviderException |
                InvalidAlgorithmParameterException e) {
            Log.e("SecureKeyManager", "Error al generar clave: " + e.getMessage(), e);
            return null;
        }
    }
}