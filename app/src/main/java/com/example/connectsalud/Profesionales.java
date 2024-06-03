package com.example.connectsalud;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;


public class Profesionales extends AppCompatActivity {

    AdminSQLiteOpenHelper admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesionales);
    }

    public void consultarProfesionales(View view) {
        SQLiteDatabase db = admin.getWritableDatabase();

    }
}