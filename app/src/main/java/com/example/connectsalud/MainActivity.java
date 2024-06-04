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
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

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

        // Ocultar campos de email y contraseña hasta que la autenticación biométrica falle
        emailEditText.setVisibility(View.GONE);
        passwordEditText.setVisibility(View.GONE);

        // Verificar el soporte de biometría y mostrar el prompt
        checkBiometricSupport();
    }

    private void checkBiometricSupport() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                // El dispositivo puede autenticar con biometría, mostrar el prompt
                showBiometricPrompt();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "El dispositivo no tiene hardware biométrico", Toast.LENGTH_SHORT).show();
                showEmailPasswordFields();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "El hardware biométrico no está disponible actualmente", Toast.LENGTH_SHORT).show();
                showEmailPasswordFields();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No hay biometría enrollada", Toast.LENGTH_SHORT).show();
                showEmailPasswordFields();
                break;
        }
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this, "Error de autenticación: " + errString, Toast.LENGTH_SHORT).show();
                showEmailPasswordFields();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                // La autenticación fue exitosa, proceder al inicio de sesión automático
                proceedWithLogin();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
                showEmailPasswordFields();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación Biométrica")
                .setSubtitle("Inicia sesión usando tu huella digital o reconocimiento facial")
                .setNegativeButtonText("Cancelar")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void showEmailPasswordFields() {
        emailEditText.setVisibility(View.VISIBLE);
        passwordEditText.setVisibility(View.VISIBLE);
    }

    private void proceedWithLogin() {
        long pacienteId = getPacienteIdFromPreferences();

        if (pacienteId != -1) {
            Intent intent = new Intent(this, Home.class);
            intent.putExtra("PACIENTE_ID", pacienteId);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "No se encontró un ID de paciente válido", Toast.LENGTH_SHORT).show();
            showEmailPasswordFields();
        }
    }

    private long getPacienteIdFromPreferences() {
        return sharedPreferences.getLong("PACIENTE_ID", -1);
    }

    public void launchHome(View view) {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (isValidEmail(email) && isValidPassword(password)) {
            long pacienteId = obtenerPacienteIdDesdeBaseDeDatos(email, password);

            if (pacienteId != -1) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("PACIENTE_ID", pacienteId);
                editor.apply();

                Intent intent = new Intent(this, Home.class);
                intent.putExtra("PACIENTE_ID", pacienteId);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Credenciales inválidas. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Credenciales inválidas. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8;
    }

    private long obtenerPacienteIdDesdeBaseDeDatos(String email, String password) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = { "dni" };
        String selection = "email = ? AND pass = ?";
        String[] selectionArgs = { email, password };

        Cursor cursor = db.query(
                "usuarios",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        long pacienteId = -1;

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
        String url = "https://connectsalud.netlify.app/";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}