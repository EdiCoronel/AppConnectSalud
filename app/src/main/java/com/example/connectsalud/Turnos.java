package com.example.connectsalud;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AppCompatActivity;

public class Turnos extends AppCompatActivity {

    private ReserveDatabaseHelper databaseHelper;
    private ListView listViewReservas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turnos);

        databaseHelper = new ReserveDatabaseHelper(this);
        listViewReservas = findViewById(R.id.listViewReservas);

        loadReservas();

        listViewReservas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String especialidad = cursor.getString(cursor.getColumnIndexOrThrow("especialidad"));
                String profesional = cursor.getString(cursor.getColumnIndexOrThrow("profesional"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                String hora = cursor.getString(cursor.getColumnIndexOrThrow("hora"));

                Intent intent = new Intent();
                intent.putExtra("reserva_id", id);
                intent.putExtra("especialidad", especialidad);
                intent.putExtra("profesional", profesional);
                intent.putExtra("fecha", fecha);
                intent.putExtra("hora", hora);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void loadReservas() {
        Cursor cursor = databaseHelper.getAllReservas();
        String[] from = { "especialidad", "profesional", "fecha", "hora" };
        int[] to = { R.id.textEspecialidad, R.id.textProfesional, R.id.textFecha, R.id.textHora };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.reserva_item, cursor, from, to, 0);

        listViewReservas.setAdapter(adapter);
    }
}