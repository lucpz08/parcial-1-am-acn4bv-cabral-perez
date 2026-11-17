package com.example.tcg;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Navbar.configurar(this, R.id.perfilButton);

        findViewById(R.id.editarPerfilButton).setOnClickListener(v -> mostrarDialogoEditar());
        findViewById(R.id.cerrarSesionButton).setOnClickListener(v -> confirmarCerrarSesion());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Se refresca al volver, por ejemplo si se agregaron cartas en el álbum
        mostrarDatos();
    }

    private void mostrarDatos() {
        ((TextView) findViewById(R.id.perfilNombre)).setText(Sesion.getNombre());
        ((TextView) findViewById(R.id.perfilUsername))
                .setText(getString(R.string.username_formato, Sesion.getUsername()));

        String provincia = Sesion.getProvincia();
        ((TextView) findViewById(R.id.perfilProvincia))
                .setText(provincia != null ? provincia : getString(R.string.sin_provincia_declarada));

        ((TextView) findViewById(R.id.perfilEstrellas)).setText(Estrellas.crear(this, Sesion.getRating()));

        ((TextView) findViewById(R.id.statCartas)).setText(String.valueOf(Sesion.getCantidadCartas()));
        ((TextView) findViewById(R.id.statResenas)).setText(String.valueOf(Sesion.getCantidadResenas()));
        ((TextView) findViewById(R.id.statMiembroDesde)).setText(String.valueOf(Sesion.getAnioAlta()));

        findViewById(R.id.sinResenasTextView)
                .setVisibility(Sesion.getCantidadResenas() == 0 ? View.VISIBLE : View.GONE);
    }

    private void mostrarDialogoEditar() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_editar_perfil, null);
        EditText nombreEditText = dialogView.findViewById(R.id.nombreEditText);
        Spinner provinciaSpinner = dialogView.findViewById(R.id.provinciaSpinner);

        nombreEditText.setText(Sesion.getNombre());

        List<String> opciones = new ArrayList<>();
        opciones.add(getString(R.string.sin_provincia_declarada));
        opciones.addAll(Arrays.asList(getResources().getStringArray(R.array.provincias)));
        provinciaSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, opciones));
        if (Sesion.getProvincia() != null) {
            provinciaSpinner.setSelection(opciones.indexOf(Sesion.getProvincia()));
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.editar_perfil)
                .setView(dialogView)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.guardar, null)
                .create();

        // El listener se asigna después de show() para que el diálogo no se cierre si hay error
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nombre = nombreEditText.getText().toString().trim();
            if (nombre.isEmpty()) {
                nombreEditText.setError(getString(R.string.error_nombre_vacio));
                return;
            }
            int posicion = provinciaSpinner.getSelectedItemPosition();
            Sesion.setNombre(nombre);
            Sesion.setProvincia(posicion > 0 ? opciones.get(posicion) : null);

            mostrarDatos();
            Toast.makeText(this, R.string.perfil_actualizado, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }));

        dialog.show();
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.cerrar_sesion_confirmar)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.cerrar_sesion, (d, which) -> {
                    Sesion.cerrar();
                    Intent intent = new Intent(this, LoginActivity.class);
                    // Borra Álbum/Explorar/Perfil del back stack
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .show();
    }
}
