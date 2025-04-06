package com.ph03nix.functionalproject.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "gestores.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla GestorVentas
    public static final String TABLE_GESTORES = "gestor_ventas";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CI = "ci";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_BASE64 = "unique_code_base64";

    // SQL para crear la tabla
    private static final String CREATE_TABLE_GESTORES =
            "CREATE TABLE " + TABLE_GESTORES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CI + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_BASE64 + " TEXT UNIQUE);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GESTORES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GESTORES);
        onCreate(db);
    }
}