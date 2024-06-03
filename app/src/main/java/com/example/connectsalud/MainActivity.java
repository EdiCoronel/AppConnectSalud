package com.example.connectsalud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private AdminSQLiteOpenHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.input_email);
        passwordEditText = findViewById(R.id.input_pass1);
        databaseHelper = new AdminSQLiteOpenHelper(this);
        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
    }

    public void launchHome(View view) {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (isValidEmail(email) && isValidPassword(password)) {
            // Obtener el ID del paciente desde la base de datos
            long pacienteId = obtenerPacienteIdDesdeBaseDeDatos(email, password);

            if (pacienteId != -1) {
                // Guardar el ID del paciente en SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("PACIENTE_ID", pacienteId);
                editor.apply();

                // Credenciales válidas, iniciar la actividad Home con el ID del paciente como extra
                Intent intent = new Intent(this, Home.class);
                intent.putExtra("PACIENTE_ID", pacienteId);
                startActivity(intent);
                finish(); // Para evitar que el usuario vuelva atrás presionando el botón de retroceso
            } else {
                // Credenciales inválidas, mostrar un mensaje de error
                Toast.makeText(this, "Credenciales inválidas. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Credenciales inválidas, mostrar un mensaje de error
            Toast.makeText(this, "Credenciales inválidas. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isValidPassword(String password) {
        // Implementa tu lógica de validación de contraseña aquí
        // Por ejemplo, puedes verificar si la contraseña tiene una longitud mínima
        return password.length() >= 8;
    }

    private long obtenerPacienteIdDesdeBaseDeDatos(String email, String password) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = { "dni" };
        String selection = "email = ? AND pass = ?";
        String[] selectionArgs = { email, password };

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

    public void launchRegister(View view) {
        Intent intent = new Intent(this, RegistroPaciente.class);
        startActivity(intent);
    }
    public void launchWebsite(View view) {
        String url = "https://connectsalud.netlify.app/"; // Cambia esto a tu URL real
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

}