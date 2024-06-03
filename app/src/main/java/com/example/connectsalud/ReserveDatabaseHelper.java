package com.example.connectsalud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ReserveDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "reservas.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_RESERVAS = "reservas";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_ESPECIALIDAD = "especialidad";
    private static final String COLUMN_PROFESIONAL = "profesional";
    private static final String COLUMN_FECHA = "fecha";
    private static final String COLUMN_HORA = "hora";

    public ReserveDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String createTable = "CREATE TABLE " + TABLE_RESERVAS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ESPECIALIDAD + " TEXT, " +
                    COLUMN_PROFESIONAL + " TEXT, " +
                    COLUMN_FECHA + " DATE, " +
                    COLUMN_HORA + " TIME ) ";
            db.execSQL(createTable);

            Log.d("Database", "Database created successfully");
        } catch (SQLException e) {
            Log.e("Database", "Error creating the database: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL("ALTER TABLE " + TABLE_RESERVAS + " ADD COLUMN " + COLUMN_HORA + " TIME");
        }
    }

    public long insertReserva(String especialidad, String profesional, String fecha, String hora) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ESPECIALIDAD, especialidad);
        values.put(COLUMN_PROFESIONAL, profesional);
        values.put(COLUMN_FECHA, fecha);
        values.put(COLUMN_HORA, hora);

        long reservaId = db.insert(TABLE_RESERVAS, null, values);
        db.close();
        return reservaId;
    }

    public Cursor getAllReservas() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RESERVAS, null, null, null, null, null, null);
    }

    public boolean cancelReserva(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_RESERVAS, COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean updateReserva(long id, String especialidad, String profesional, String fecha, String hora) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ESPECIALIDAD, especialidad);
        values.put(COLUMN_PROFESIONAL, profesional);
        values.put(COLUMN_FECHA, fecha);
        values.put(COLUMN_HORA, hora);
        return db.update(TABLE_RESERVAS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }
}