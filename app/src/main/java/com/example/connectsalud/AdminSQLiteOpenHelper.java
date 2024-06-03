package com.example.connectsalud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db1";
    private static final int DATABASE_VERSION = 1;

    public AdminSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE usuarios (" +
                "dni INTEGER PRIMARY KEY," +
                "nombre TEXT," +
                "apellido TEXT," +
                "telefono INTEGER," +
                "fecha_nacimiento TEXT," +
                "email TEXT," +
                "pass TEXT," +
                "passagain TEXT" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Aquí puedes implementar la lógica de actualización si es necesario.
    }

    public Cursor obtenerPacientePorId(long pacienteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = { "dni", "nombre", "apellido", "telefono", "fecha_nacimiento", "email" };
        String selection = "dni = ?";
        String[] selectionArgs = { String.valueOf(pacienteId) };

        return db.query(
                "usuarios",   // Nombre de la tabla
                projection,   // Columnas que quieres recuperar
                selection,    // Clausula WHERE
                selectionArgs,// Valores de la clausula WHERE
                null,         // No agrupar las filas
                null,         // No filtrar por grupos de filas
                null          // No ordenar las filas
        );
    }

    public long insertarUsuario(String dni, String nombre, String apellido, String telefono, String fecha_nacimiento, String email, String pass, String passagain) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dni", dni);
        values.put("nombre", nombre);
        values.put("apellido", apellido);
        values.put("telefono", telefono);
        values.put("fecha_nacimiento", fecha_nacimiento);
        values.put("email", email);
        values.put("pass", pass);
        values.put("passagain", passagain);
        long newRowId = db.insert("usuarios", null, values);
        db.close();
        return newRowId;
    }

    public long obtenerPacienteIdDesdeBaseDeDatos(String email, String pass) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = { "dni" };
        String selection = "email = ? AND pass = ?";
        String[] selectionArgs = { email, pass };

        Cursor cursor = db.query(
                "usuarios",   // Nombre de la tabla
                projection,   // Columnas que quieres recuperar
                selection,    // Clausula WHERE
                selectionArgs,// Valores de la clausula WHERE
                null,         // No agrupar las filas
                null,         // No filtrar por grupos de filas
                null          // No ordenar las filas
        );

        long pacienteId = -1; // Valor predeterminado si no se encuentra el paciente

        if (cursor.moveToFirst()) {
            pacienteId = cursor.getLong(cursor.getColumnIndexOrThrow("dni"));
        }

        cursor.close();
        db.close();

        return pacienteId;
    }

    public boolean actualizarUsuario(String dni, String nombre, String apellido, String telefono, String fecha_nacimiento, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("apellido", apellido);
        values.put("telefono", telefono);
        values.put("fecha_nacimiento", fecha_nacimiento);
        values.put("email", email);

        int rows = db.update("usuarios", values, "dni = ?", new String[]{dni});
        db.close();
        return rows > 0;
    }
}