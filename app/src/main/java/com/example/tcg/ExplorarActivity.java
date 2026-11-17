package com.example.tcg;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ExplorarActivity extends AppCompatActivity {

    private enum Orden { RECIENTES, PRECIO_MENOR, PRECIO_MAYOR }

    private final List<Publicacion> publicaciones = Publicacion.ejemplos();

    private String textoBusqueda = "";
    /** null = "Todas". */
    private Publicacion.Intencion intencionSeleccionada = null;
    private Orden orden = Orden.RECIENTES;
    /** null = todas las provincias. */
    private String provinciaSeleccionada = null;

    private LinearLayout publicacionesLayout;
    private View sinPublicacionesLayout;
    private TextView filtrarButton;
    private TextView[] chips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_explorar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Navbar.configurar(this, R.id.explorarButton);

        publicacionesLayout = findViewById(R.id.publicacionesLayout);
        sinPublicacionesLayout = findViewById(R.id.sinPublicacionesLayout);
        filtrarButton = findViewById(R.id.filtrarButton);
        filtrarButton.setOnClickListener(v -> mostrarDialogoFiltros());

        EditText busquedaEditText = findViewById(R.id.busquedaPublicacionEditText);
        busquedaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                textoBusqueda = s.toString().trim();
                mostrarPublicaciones();
            }
        });

        configurarChips();
        mostrarPublicaciones();
    }

    private void configurarChips() {
        chips = new TextView[]{
                findViewById(R.id.chipTodas),
                findViewById(R.id.chipVendo),
                findViewById(R.id.chipCambio),
                findViewById(R.id.chipBusco)
        };
        Publicacion.Intencion[] valores = {
                null, Publicacion.Intencion.VENDO, Publicacion.Intencion.CAMBIO, Publicacion.Intencion.BUSCO
        };
        for (int i = 0; i < chips.length; i++) {
            Publicacion.Intencion valor = valores[i];
            chips[i].setTag(valor);
            chips[i].setOnClickListener(v -> {
                intencionSeleccionada = valor;
                actualizarChips();
                actualizarBotonFiltrar();
                mostrarPublicaciones();
            });
        }
        actualizarChips();
    }

    private void actualizarChips() {
        for (TextView chip : chips) {
            boolean seleccionado = chip.getTag() == intencionSeleccionada;
            chip.setBackgroundResource(seleccionado ? R.drawable.bg_chip_selected : R.drawable.bg_chip);
            chip.setTextColor(getColor(seleccionado ? R.color.white : R.color.text));
            chip.setTypeface(null, seleccionado ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        }
    }

    private void actualizarBotonFiltrar() {
        int activos = (provinciaSeleccionada != null ? 1 : 0)
                + (intencionSeleccionada != null ? 1 : 0)
                + (orden != Orden.RECIENTES ? 1 : 0);
        filtrarButton.setText(activos > 0
                ? getString(R.string.filtrar_con_cantidad, activos)
                : getString(R.string.filtrar));
    }

    private void mostrarDialogoFiltros() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_filtros, null);
        RadioGroup ordenRadioGroup = dialogView.findViewById(R.id.ordenRadioGroup);
        Spinner provinciaSpinner = dialogView.findViewById(R.id.provinciaSpinner);

        switch (orden) {
            case PRECIO_MENOR:
                ordenRadioGroup.check(R.id.ordenPrecioMenor);
                break;
            case PRECIO_MAYOR:
                ordenRadioGroup.check(R.id.ordenPrecioMayor);
                break;
            default:
                ordenRadioGroup.check(R.id.ordenRecientes);
        }

        List<String> opciones = new ArrayList<>();
        opciones.add(getString(R.string.todas_las_provincias));
        opciones.addAll(Arrays.asList(getResources().getStringArray(R.array.provincias)));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, opciones);
        provinciaSpinner.setAdapter(adapter);
        if (provinciaSeleccionada != null) {
            provinciaSpinner.setSelection(opciones.indexOf(provinciaSeleccionada));
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.filtrar_y_ordenar)
                .setView(dialogView)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.ver_resultados, (dialog, which) -> {
                    int checked = ordenRadioGroup.getCheckedRadioButtonId();
                    if (checked == R.id.ordenPrecioMenor) {
                        orden = Orden.PRECIO_MENOR;
                    } else if (checked == R.id.ordenPrecioMayor) {
                        orden = Orden.PRECIO_MAYOR;
                    } else {
                        orden = Orden.RECIENTES;
                    }
                    int posicion = provinciaSpinner.getSelectedItemPosition();
                    provinciaSeleccionada = posicion > 0 ? opciones.get(posicion) : null;

                    actualizarBotonFiltrar();
                    mostrarPublicaciones();
                })
                .show();
    }

    private List<Publicacion> filtrar() {
        String busqueda = textoBusqueda.toLowerCase(Locale.ROOT);
        List<Publicacion> resultado = new ArrayList<>();
        for (Publicacion p : publicaciones) {
            if (!busqueda.isEmpty() && !p.nombreCarta.toLowerCase(Locale.ROOT).contains(busqueda)) continue;
            if (intencionSeleccionada != null && p.intencion != intencionSeleccionada) continue;
            if (provinciaSeleccionada != null && !provinciaSeleccionada.equals(p.provincia)) continue;
            resultado.add(p);
        }

        switch (orden) {
            case PRECIO_MENOR:
                // Las publicaciones sin precio van al final
                resultado.sort(Comparator.comparing((Publicacion p) -> p.precio,
                        Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case PRECIO_MAYOR:
                resultado.sort(Comparator.comparing((Publicacion p) -> p.precio,
                        Comparator.nullsLast(Comparator.<Integer>reverseOrder())));
                break;
            default:
                resultado.sort((a, b) -> Integer.compare(b.orden, a.orden));
        }
        return resultado;
    }

    private void mostrarPublicaciones() {
        List<Publicacion> filtradas = filtrar();
        publicacionesLayout.removeAllViews();
        for (Publicacion p : filtradas) {
            publicacionesLayout.addView(crearItem(p));
        }
        sinPublicacionesLayout.setVisibility(filtradas.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private View crearItem(Publicacion p) {
        View item = LayoutInflater.from(this).inflate(R.layout.item_publicacion, publicacionesLayout, false);

        item.findViewById(R.id.intentFranja).setBackgroundColor(getColor(p.intencion.colorRes));

        ImageView imagen = item.findViewById(R.id.publicacionImagen);
        Glide.with(this).load(p.imageUrl).into(imagen);

        ((TextView) item.findViewById(R.id.publicacionNombre)).setText(p.nombreCarta);
        ((TextView) item.findViewById(R.id.publicacionMeta))
                .setText(getString(R.string.carta_meta, p.set, p.numero));

        TextView vendedor = item.findViewById(R.id.publicacionVendedor);
        vendedor.setText(p.vendedor + "  ");
        vendedor.append(Estrellas.crear(this, p.ratingVendedor));

        ((TextView) item.findViewById(R.id.publicacionProvincia))
                .setText(p.provincia != null ? p.provincia : getString(R.string.sin_provincia));

        TextView precio = item.findViewById(R.id.publicacionPrecio);
        if (p.precio != null) {
            precio.setText(getString(R.string.precio, p.precio));
            precio.setVisibility(View.VISIBLE);
        } else {
            precio.setVisibility(View.GONE);
        }

        item.setOnClickListener(v -> Toast.makeText(this,
                getString(R.string.publicacion_de, p.nombreCarta, p.vendedor), Toast.LENGTH_SHORT).show());

        return item;
    }
}
