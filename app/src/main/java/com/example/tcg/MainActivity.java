package com.example.tcg;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.gridlayout.widget.GridLayout;

import com.bumptech.glide.Glide;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int CANTIDAD_CARTAS_INICIALES = 12;

    private final PokemonTcgApi pokemonTcgApi = new PokemonTcgApi();
    private int cantidadDeCartas = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cargarAlbum();
    }

    private void cargarAlbum() {
        pokemonTcgApi.fetchCards(CANTIDAD_CARTAS_INICIALES, new PokemonTcgApi.CardsCallback() {
            @Override
            public void onSuccess(List<Card> cards) {
                mostrarCartas(cards);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MainActivity.this, "No se pudo cargar el álbum", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarCartas(List<Card> cards) {
        GridLayout gridLayout = findViewById(R.id.cardsGridLayout);
        gridLayout.removeAllViews();

        for (Card card : cards) {
            gridLayout.addView(crearImageViewDeCarta(card));
        }
        gridLayout.addView(crearSlotAgregarCarta());

        cantidadDeCartas = cards.size();
        actualizarContador();
    }

    private void mostrarBuscadorDeCartas() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_buscar_carta, null);
        EditText busquedaEditText = dialogView.findViewById(R.id.busquedaEditText);
        LinearLayout resultadosLayout = dialogView.findViewById(R.id.resultadosLayout);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.buscar_carta_titulo)
                .setView(dialogView)
                .setNegativeButton(R.string.cancelar, null)
                .create();

        View.OnClickListener buscar = v -> {
            String query = busquedaEditText.getText().toString();
            if (TextUtils.isEmpty(query.trim())) return;
            buscarCartas(query, resultadosLayout, dialog);
        };
        dialogView.findViewById(R.id.buscarButton).setOnClickListener(buscar);

        dialog.show();
    }

    private void buscarCartas(String query, LinearLayout resultadosLayout, AlertDialog dialog) {
        resultadosLayout.removeAllViews();
        pokemonTcgApi.searchCards(query, new PokemonTcgApi.CardsCallback() {
            @Override
            public void onSuccess(List<Card> cards) {
                if (cards.isEmpty()) {
                    TextView sinResultados = new TextView(MainActivity.this);
                    sinResultados.setText(R.string.sin_resultados);
                    resultadosLayout.addView(sinResultados);
                    return;
                }
                for (Card card : cards) {
                    resultadosLayout.addView(crearFilaResultado(card, dialog));
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MainActivity.this, R.string.error_buscar_carta, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private View crearFilaResultado(Card card, AlertDialog dialog) {
        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        int paddingVertical = getResources().getDimensionPixelSize(R.dimen.spacing_sm);
        fila.setPadding(0, paddingVertical, 0, paddingVertical);

        ImageView imagen = new ImageView(this);
        LinearLayout.LayoutParams imagenParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.search_result_image_width),
                getResources().getDimensionPixelSize(R.dimen.search_result_image_height));
        imagenParams.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.search_result_image_margin_end));
        imagen.setLayoutParams(imagenParams);
        imagen.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(this).load(card.imageUrl).into(imagen);

        TextView nombre = new TextView(this);
        nombre.setText(card.name);
        nombre.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.search_result_text_size));

        fila.addView(imagen);
        fila.addView(nombre);

        fila.setOnClickListener(v -> {
            agregarCartaAlAlbum(card);
            dialog.dismiss();
        });

        return fila;
    }

    private void agregarCartaAlAlbum(Card card) {
        GridLayout gridLayout = findViewById(R.id.cardsGridLayout);
        int indexDelSlotAgregar = gridLayout.getChildCount() - 1;
        gridLayout.addView(crearImageViewDeCarta(card), indexDelSlotAgregar);

        cantidadDeCartas++;
        actualizarContador();
    }

    private void actualizarContador() {
        TextView nroCartasTextview = findViewById(R.id.nroCartasTextview);
        nroCartasTextview.setText(getString(R.string.cantidad_cartas, cantidadDeCartas));
    }

    private ImageView crearImageViewDeCarta(Card card) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(crearLayoutParamsDeSlot());
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Glide.with(this)
                .load(card.imageUrl)
                .into(imageView);

        imageView.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, card.name, Toast.LENGTH_SHORT).show());

        return imageView;
    }

    private View crearSlotAgregarCarta() {
        TextView slotAgregar = new TextView(this);
        slotAgregar.setLayoutParams(crearLayoutParamsDeSlot());
        slotAgregar.setText(R.string.agregar_carta);
        slotAgregar.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.add_slot_text_size));
        slotAgregar.setTextColor(getColor(R.color.text_muted));
        slotAgregar.setGravity(Gravity.CENTER);
        slotAgregar.setBackgroundResource(R.drawable.add_slot_background);
        slotAgregar.setOnClickListener(v -> mostrarBuscadorDeCartas());
        return slotAgregar;
    }

    private GridLayout.LayoutParams crearLayoutParamsDeSlot() {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = getResources().getDimensionPixelSize(R.dimen.card_width);
        params.height = getResources().getDimensionPixelSize(R.dimen.card_height);
        int margin = getResources().getDimensionPixelSize(R.dimen.spacing_xs);
        params.setMargins(margin, margin, margin, margin);
        return params;
    }
}