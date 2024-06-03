package com.example.connectsalud;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.content.Context;

public class Home extends AppCompatActivity {

    private long pacienteId;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        pacienteId = sharedPreferences.getLong("PACIENTE_ID", -1);
    }

    public void launchProfile(View view) {
        Intent intent = new Intent(this, Profile.class);
        intent.putExtra("PACIENTE_ID", pacienteId);
        startActivity(intent);
    }

    public void launchReserve(View view) {
        Intent intent = new Intent(this, Reserve.class);
        startActivity(intent);
    }

    public void launchProfesionales(View view) {
        Intent intent = new Intent(this, Profesionales.class);
        startActivity(intent);
    }

    public void launchTurnos(View view) {
        Intent intent = new Intent(this, Turnos.class);
        startActivity(intent);
    }

    public void launchCerrarSesion(View view) {
        // Limpiar SharedPreferences al cerrar sesión
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Cierra la actividad actual
    }

    // Método para lanzar la actividad de contacto
    public void launchContacto(View view) {
        Intent intent = new Intent(this, ContactoActivity.class);
        startActivity(intent);
    }
}