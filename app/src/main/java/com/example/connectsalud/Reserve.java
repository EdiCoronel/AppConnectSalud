package com.example.connectsalud;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.util.Log;
import java.util.Calendar;
import java.util.Locale;

public class Reserve extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_RESERVA = 1;
    private ReserveDatabaseHelper databaseHelper;
    private Spinner spinnerEspecialidades, spinnerProfesionales;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private long reservaId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        databaseHelper = new ReserveDatabaseHelper(this);

        spinnerEspecialidades = findViewById(R.id.spinner_especialidad);
        spinnerProfesionales = findViewById(R.id.spinner_profesionales);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.time_picker);

        ArrayAdapter<CharSequence> especialidadesAdapter = ArrayAdapter.createFromResource(
                this, R.array.item_esp, android.R.layout.simple_spinner_item);
        especialidadesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspecialidades.setAdapter(especialidadesAdapter);

        ArrayAdapter<CharSequence> profesionalesAdapter = ArrayAdapter.createFromResource(
                this, R.array.item_prof, android.R.layout.simple_spinner_item);
        profesionalesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProfesionales.setAdapter(profesionalesAdapter);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        datePicker.setMinDate(calendar.getTimeInMillis());

        datePicker.init(
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    // Manejar la fecha seleccionada si es necesario
                }
        );

        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            // Manejar la hora seleccionada si es necesario
        });
    }

    public void seleccionarReserva(View view) {
        Intent intent = new Intent(this, Turnos.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_RESERVA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_RESERVA && resultCode == RESULT_OK) {
            reservaId = data.getLongExtra("reserva_id", -1);
            if (reservaId != -1) {
                String especialidad = data.getStringExtra("especialidad");
                String profesional = data.getStringExtra("profesional");
                String fecha = data.getStringExtra("fecha");
                String hora = data.getStringExtra("hora");

                // Establecer los valores en los campos correspondientes
                setSpinnerSelection(spinnerEspecialidades, especialidad);
                setSpinnerSelection(spinnerProfesionales, profesional);
                setDatePicker(datePicker, fecha);
                setTimePicker(timePicker, hora);

                showToast("Reserva seleccionada para editar: " + reservaId);
            }
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        int position = adapter.getPosition(value);
        spinner.setSelection(position);
    }

    private void setDatePicker(DatePicker datePicker, String date) {
        String[] parts = date.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1;
        int day = Integer.parseInt(parts[2]);
        datePicker.updateDate(year, month, day);
    }

    private void setTimePicker(TimePicker timePicker, String time) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
    }

    public void confirmarReserva(View view) {
        String especialidad = spinnerEspecialidades.getSelectedItem().toString();
        String profesional = spinnerProfesionales.getSelectedItem().toString();
        String fecha = obtenerFechaSeleccionada();
        String hora = obtenerHoraSeleccionada();

        if (reservaId == -1) {
            long id = databaseHelper.insertReserva(especialidad, profesional, fecha, hora);
            if (id != -1) {
                showToast("Turno reservado");
            } else {
                showToast("Error al reservar el turno");
            }
        } else {
            boolean updated = databaseHelper.updateReserva(reservaId, especialidad, profesional, fecha, hora);
            if (updated) {
                showToast("Turno actualizado");
            } else {
                showToast("Error al actualizar el turno");
            }
        }
    }

    public void cancelarReserva(View view) {
        if (reservaId != -1) {
            boolean deleted = databaseHelper.cancelReserva(reservaId);
            if (deleted) {
                showToast("Turno cancelado");
                reservaId = -1; // Resetear el ID de la reserva
            } else {
                showToast("Error al cancelar el turno");
            }
        } else {
            showToast("No hay turno seleccionado para cancelar");
        }
    }

    private String obtenerFechaSeleccionada() {
        int year = datePicker.getYear();
        int month = datePicker.getMonth() + 1;
        int day = datePicker.getDayOfMonth();
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
    }

    private String obtenerHoraSeleccionada() {
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }
}