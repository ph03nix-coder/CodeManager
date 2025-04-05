package com.ph03nix.functionalproject;

import android.util.Base64;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPublicKey;
import java.util.UUID;

public class UniqueCode {
    private final String base64;
    private final ECPublicKey publicKey;

    public UniqueCode(String base64, ECPublicKey pk) {
        this.base64 = base64;
        publicKey = pk;
    }

    public String getBase64() {
        return this.base64;
    }

    public ECPublicKey getPublicKey() {
        return this.publicKey;
    }

    public static UniqueCode createNew() {
        try {
            // Paso 1: Generar UUID único
            String uniqueUuid = UUID.randomUUID().toString();

            // Paso 2: Obtener timestamp en nanosegundos
            long timestampNs = System.nanoTime();

            // Combinar datos con identidad, nombre y dirección
            String combinedData = String.format("%s:%s",
                    uniqueUuid, timestampNs);
            byte[] dataBytes = combinedData.getBytes(StandardCharsets.UTF_8);

            // Paso 3: Generar clave ECDSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            keyGen.initialize(256); // Equivalente a SECP256R1
            java.security.KeyPair keyPair = keyGen.generateKeyPair();
            ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();

            // Firmar los datos
            Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
            ecdsaSign.initSign(keyPair.getPrivate());
            ecdsaSign.update(dataBytes);
            byte[] signature = ecdsaSign.sign();

            // Empaquetar datos: longitud (4 bytes) + datos + firma
            ByteBuffer buffer = ByteBuffer.allocate(4 + dataBytes.length + signature.length);
            buffer.putInt(dataBytes.length);
            buffer.put(dataBytes);
            buffer.put(signature);

            // Codificar en Base64
            return new UniqueCode(Base64.encodeToString(buffer.array(), Base64.NO_WRAP), publicKey);

        } catch (NoSuchAlgorithmException | java.security.InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean verify() {
        try {
            byte[] decodedBytes = Base64.decode(this.base64, Base64.DEFAULT);
            ByteBuffer buffer = ByteBuffer.wrap(decodedBytes);

            // Leer longitud de datos
            int dataLength = buffer.getInt();

            // Leer datos y firma
            byte[] dataBytes = new byte[dataLength];
            buffer.get(dataBytes);

            byte[] signature = new byte[buffer.remaining()];
            buffer.get(signature);

            // Verificar firma
            Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(dataBytes);

            return ecdsaVerify.verify(signature);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
