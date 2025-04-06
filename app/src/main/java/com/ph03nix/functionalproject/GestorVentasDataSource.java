package com.ph03nix.functionalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.Serializable;
import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.List;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;

public class GestorVentasDataSource {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public GestorVentasDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Insertar un GestorVentas
    public long insertGestor(GestorVentas gestor) {
        ContentValues values = new ContentValues();
//        values.put(DatabaseHelper.COLUMN_ID, gestor.getId());
        values.put(DatabaseHelper.COLUMN_NAME, gestor.getName());
        values.put(DatabaseHelper.COLUMN_BASE64, gestor.getUniqueCode().getBase64());
        values.put(DatabaseHelper.COLUMN_PK_BYTES, gestor.getUniqueCode().getPublicKey().getEncoded()); // Guardamos ECPublicKey como bytes

        return database.insert(DatabaseHelper.TABLE_GESTORES, null, values);
    }

    // Obtener todos los GestorVentas
    public List<GestorVentas> getAllGestores() {
        List<GestorVentas> gestores = new ArrayList<>();
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_GESTORES,
                null, // Todas las columnas
                null, // Sin WHERE
                null, // Sin argumentos WHERE
                null, // Sin GROUP BY
                null, // Sin HAVING
                null  // Sin ORDER BY
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            GestorVentas gestor = cursorToGestor(cursor);
            gestores.add(gestor);
            cursor.moveToNext();
        }
        cursor.close();
        return gestores;
    }

    public GestorVentas getGestorById(int id) {
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_GESTORES,
                null,
                "id = " + id,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        GestorVentas gestor = cursorToGestor(cursor);
        cursor.close();
        return gestor;
    }

    // Convertir Cursor a GestorVentas
    private GestorVentas cursorToGestor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
        String base64 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BASE64));
        byte[] pkBytes = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_PK_BYTES));

        // Convertir bytes a ECPublicKey (requiere implementación)
        ECPublicKey pk = convertBytesToECPublicKey(pkBytes);
        UniqueCode uc = new UniqueCode(base64, pk);

        return new GestorVentas(id, name, uc);
    }

    // Actualizar un GestorVentas
    public int updateGestor(GestorVentas gestor) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, gestor.getName());
        values.put(DatabaseHelper.COLUMN_BASE64, gestor.getUniqueCode().getBase64());
        values.put(DatabaseHelper.COLUMN_PK_BYTES, gestor.getUniqueCode().getPublicKey().getEncoded());

        return database.update(
                DatabaseHelper.TABLE_GESTORES,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(gestor.getId())}
        );
    }

    // Eliminar un GestorVentas
    public int deleteGestor(int id) {
        return database.delete(
                DatabaseHelper.TABLE_GESTORES,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Método auxiliar para convertir bytes a ECPublicKey (ejemplo simplificado)
    private ECPublicKey convertBytesToECPublicKey(byte[] pkBytes) {
        if (pkBytes == null || pkBytes.length == 0) {
            return null;
        }

        try {
            // 1. Obtener la instancia de KeyFactory para ECDSA (o el algoritmo EC que uses)
            KeyFactory keyFactory = KeyFactory.getInstance("EC");

            // 2. Crear un X509EncodedKeySpec con los bytes crudos
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pkBytes);

            // 3. Generar la clave pública
            return (ECPublicKey) keyFactory.generatePublic(keySpec);

        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyConversion", "Algoritmo EC no soportado", e);
        } catch (InvalidKeySpecException e) {
            Log.e("KeyConversion", "Especificación de clave inválida", e);
        }

        return null;
    }
}
