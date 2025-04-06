package com.ph03nix.functionalproject.Security;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class UniqueCode {
    private final SecureKeyManager secureKeyManager;
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    public UniqueCode() {
        this.secureKeyManager = new SecureKeyManager();
    }

    public String generateUniqueCode(String ci) {
        if (ci == null) {
            throw new IllegalArgumentException("CI cannot be null");
        }

        try {
            SecretKey hmacKey = secureKeyManager.getOrCreateHmacKey();
            if (hmacKey == null) return null;

            Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
            hmac.init(hmacKey);

            byte[] hashBytes = hmac.doFinal(ci.getBytes(StandardCharsets.UTF_8));
            String codigoBase64 = Base64.encodeToString(hashBytes,
                    Base64.NO_WRAP | Base64.URL_SAFE | Base64.NO_PADDING);
            Log.d("DEBUG", "generateUniqueCode: " + codigoBase64.substring(0, 8));
            return codigoBase64.substring(0, 8);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean verificarCodigo(String codigoIngresado, String ciGestor) {
        String codigoGenerado = generateUniqueCode(ciGestor);
        return constantTimeEquals(codigoGenerado, codigoIngresado);
    }

    // Comparación segura contra ataques de temporización
    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }

        if (a.length() != b.length()) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}