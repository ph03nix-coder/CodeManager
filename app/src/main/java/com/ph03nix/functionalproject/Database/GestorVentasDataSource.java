package com.ph03nix.functionalproject.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ph03nix.functionalproject.GestorVentas;

import java.util.ArrayList;
import java.util.List;

public class GestorVentasDataSource {
    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;

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
        values.put(DatabaseHelper.COLUMN_CI, gestor.getCi());
        values.put(DatabaseHelper.COLUMN_BASE64, gestor.getUniqueCode());
        return database.insert(DatabaseHelper.TABLE_GESTORES, null, values);
    }

    public boolean ciExists(String ci) {
        for(GestorVentas gestor : getAllGestores()) {
            if(gestor.getCi().equals(ci)) {
                return true;
            }
        }
        return false;
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

    public int getNextID() {
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_GESTORES,
                null, // Todas las columnas
                null, // Sin WHERE
                null, // Sin argumentos WHERE
                null, // Sin GROUP BY
                null, // Sin HAVING
                null
        );

        if(!cursor.moveToLast()) {
            return 1;
        }
        GestorVentas g = cursorToGestor(cursor);
        cursor.close();
        return g.getId() + 1;
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
        String ci = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CI));
        String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
        String base64 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BASE64));

        return new GestorVentas(id, ci, name, base64);
    }

    // Actualizar un GestorVentas
    public int updateGestor(GestorVentas gestor) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, gestor.getName());
        values.put(DatabaseHelper.COLUMN_BASE64, gestor.getUniqueCode());

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
}
