package com.example.tcg;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

    private static final int CANTIDAD_CARTAS_INICIALES = 8;
    private static final String TAG = "PokemonTCG";

    private final PokemonTcgApi pokemonTcgApi = new PokemonTcgApi();

    private int cantidadDeCartas = 0;
    private boolean cargandoAlbum = false;

    private GridLayout cardsGridLayout;
    private TextView nroCartasTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // Referencias de la interfaz
        cardsGridLayout = findViewById(R.id.cardsGridLayout);
        nroCartasTextview = findViewById(R.id.nroCartasTextview);

        // Navegación centralizada
        Navbar.configurar(this, R.id.albumButton);

        // Cargar cartas iniciales
        cargarAlbum();
    }

    private void cargarAlbum() {

        // Evitar varias solicitudes simultáneas
        if (cargandoAlbum) {
            return;
        }

        cargandoAlbum = true;

        nroCartasTextview.setText(R.string.cargando_cartas);
        nroCartasTextview.setTextColor(getColor(R.color.text_muted));
        nroCartasTextview.setOnClickListener(null);

        pokemonTcgApi.fetchCards(
                CANTIDAD_CARTAS_INICIALES,
                new PokemonTcgApi.CardsCallback() {

                    @Override
                    public void onSuccess(List<Card> cards) {

                        runOnUiThread(() -> {

                            if (isFinishing() || isDestroyed()) {
                                return;
                            }

                            cargandoAlbum = false;

                            if (cards == null || cards.isEmpty()) {
                                mostrarErrorCarga(
                                        "No se encontraron cartas. Tocá para reintentar"
                                );
                                return;
                            }

                            mostrarCartas(cards);
                        });
                    }

                    @Override
                    public void onError(Exception e) {

                        Log.e(
                                TAG,
                                "Error al cargar el álbum desde la API",
                                e
                        );

                        runOnUiThread(() -> {

                            if (isFinishing() || isDestroyed()) {
                                return;
                            }

                            cargandoAlbum = false;

                            mostrarErrorCarga(
                                    "Error al cargar. Tocá para reintentar"
                            );
                        });
                    }
                }
        );
    }

    private void mostrarErrorCarga(String mensaje) {

        nroCartasTextview.setText(mensaje);
        nroCartasTextview.setTextColor(getColor(R.color.primary));

        // Permitir volver a consultar la API
        nroCartasTextview.setOnClickListener(v -> cargarAlbum());

        Toast.makeText(
                this,
                "No se pudo cargar el álbum",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void mostrarCartas(List<Card> cards) {

        // Limpiar cartas anteriores
        cardsGridLayout.removeAllViews();

        // Agregar cartas recibidas
        for (Card card : cards) {
            cardsGridLayout.addView(crearImageViewDeCarta(card));
        }

        // Agregar el botón para buscar nuevas cartas
        cardsGridLayout.addView(crearSlotAgregarCarta());

        cantidadDeCartas = cards.size();

        actualizarContador();
    }

    private void mostrarBuscadorDeCartas() {

        View dialogView = LayoutInflater.from(this).inflate(
                R.layout.dialog_buscar_carta,
                null
        );

        EditText busquedaEditText =
                dialogView.findViewById(R.id.busquedaEditText);

        LinearLayout resultadosLayout =
                dialogView.findViewById(R.id.resultadosLayout);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.buscar_carta_titulo)
                .setView(dialogView)
                .setNegativeButton(R.string.cancelar, null)
                .create();

        View.OnClickListener buscar = v -> {

            String query = busquedaEditText
                    .getText()
                    .toString()
                    .trim();

            if (TextUtils.isEmpty(query)) {
                busquedaEditText.setError(
                        "Ingresá el nombre de una carta"
                );
                return;
            }

            buscarCartas(query, resultadosLayout, dialog);
        };

        dialogView.findViewById(R.id.buscarButton)
                .setOnClickListener(buscar);

        dialog.show();
    }

    private void buscarCartas(
            String query,
            LinearLayout resultadosLayout,
            AlertDialog dialog
    ) {

        resultadosLayout.removeAllViews();

        TextView cargando = new TextView(this);
        cargando.setText("Buscando cartas...");
        cargando.setPadding(16, 16, 16, 16);

        resultadosLayout.addView(cargando);

        pokemonTcgApi.searchCards(
                query,
                new PokemonTcgApi.CardsCallback() {

                    @Override
                    public void onSuccess(List<Card> cards) {

                        runOnUiThread(() -> {

                            if (isFinishing()
                                    || isDestroyed()
                                    || !dialog.isShowing()) {
                                return;
                            }

                            resultadosLayout.removeAllViews();

                            if (cards == null || cards.isEmpty()) {

                                TextView sinResultados =
                                        new TextView(MainActivity.this);

                                sinResultados.setText(
                                        R.string.sin_resultados
                                );

                                sinResultados.setPadding(
                                        16, 16, 16, 16
                                );

                                resultadosLayout.addView(sinResultados);
                                return;
                            }

                            for (Card card : cards) {

                                resultadosLayout.addView(
                                        crearFilaResultado(card, dialog)
                                );
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {

                        Log.e(
                                TAG,
                                "Error al buscar cartas",
                                e
                        );

                        runOnUiThread(() -> {

                            if (isFinishing()
                                    || isDestroyed()
                                    || !dialog.isShowing()) {
                                return;
                            }

                            resultadosLayout.removeAllViews();

                            TextView error =
                                    new TextView(MainActivity.this);

                            error.setText(
                                    "No se pudo realizar la búsqueda. Intentá nuevamente."
                            );

                            error.setTextColor(
                                    getColor(R.color.primary)
                            );

                            error.setPadding(16, 16, 16, 16);

                            resultadosLayout.addView(error);
                        });
                    }
                }
        );
    }

    private View crearFilaResultado(Card card, AlertDialog dialog) {

        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);

        int paddingVertical = getResources()
                .getDimensionPixelSize(R.dimen.spacing_sm);

        fila.setPadding(
                0,
                paddingVertical,
                0,
                paddingVertical
        );

        // Imagen de la carta
        ImageView imagen = new ImageView(this);

        LinearLayout.LayoutParams imagenParams =
                new LinearLayout.LayoutParams(
                        getResources().getDimensionPixelSize(
                                R.dimen.search_result_image_width
                        ),
                        getResources().getDimensionPixelSize(
                                R.dimen.search_result_image_height
                        )
                );

        imagenParams.setMarginEnd(
                getResources().getDimensionPixelSize(
                        R.dimen.search_result_image_margin_end
                )
        );

        imagen.setLayoutParams(imagenParams);
        imagen.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Glide.with(this)
                .load(card.imageUrl)
                .into(imagen);

        // Nombre de la carta
        TextView nombre = new TextView(this);

        nombre.setText(card.name);

        nombre.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(
                        R.dimen.search_result_text_size
                )
        );

        fila.addView(imagen);
        fila.addView(nombre);

        // Agregar carta seleccionada
        fila.setOnClickListener(v -> {

            agregarCartaAlAlbum(card);

            dialog.dismiss();
        });

        return fila;
    }

    private void agregarCartaAlAlbum(Card card) {

        int indexDelSlotAgregar =
                cardsGridLayout.getChildCount() - 1;

        // Evitar errores si todavía no existe el botón +
        if (indexDelSlotAgregar < 0) {

            cardsGridLayout.addView(
                    crearSlotAgregarCarta()
            );

            indexDelSlotAgregar = 0;
        }

        cardsGridLayout.addView(
                crearImageViewDeCarta(card),
                indexDelSlotAgregar
        );

        cantidadDeCartas++;

        actualizarContador();
    }

    private void actualizarContador() {

        nroCartasTextview.setTextColor(
                getColor(R.color.text_muted)
        );

        nroCartasTextview.setText(
                getString(
                        R.string.cantidad_cartas,
                        cantidadDeCartas
                )
        );

        // Desactivar reintento cuando ya cargó correctamente
        nroCartasTextview.setOnClickListener(null);

        Sesion.setCantidadCartas(cantidadDeCartas);
    }

    private ImageView crearImageViewDeCarta(Card card) {

        ImageView imageView = new ImageView(this);

        imageView.setLayoutParams(
                crearLayoutParamsDeSlot()
        );

        imageView.setScaleType(
                ImageView.ScaleType.FIT_CENTER
        );

        Glide.with(this)
                .load(card.imageUrl)
                .into(imageView);

        imageView.setOnClickListener(v ->
                Toast.makeText(
                        MainActivity.this,
                        card.name,
                        Toast.LENGTH_SHORT
                ).show()
        );

        return imageView;
    }

    private View crearSlotAgregarCarta() {

        TextView slotAgregar = new TextView(this);

        slotAgregar.setLayoutParams(
                crearLayoutParamsDeSlot()
        );

        slotAgregar.setText(
                R.string.agregar_carta
        );

        slotAgregar.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(
                        R.dimen.add_slot_text_size
                )
        );

        slotAgregar.setTextColor(
                getColor(R.color.text_muted)
        );

        slotAgregar.setGravity(Gravity.CENTER);

        slotAgregar.setBackgroundResource(
                R.drawable.add_slot_background
        );

        slotAgregar.setOnClickListener(v ->
                mostrarBuscadorDeCartas()
        );

        return slotAgregar;
    }

    private GridLayout.LayoutParams crearLayoutParamsDeSlot() {

        GridLayout.LayoutParams params =
                new GridLayout.LayoutParams();

        params.width = getResources()
                .getDimensionPixelSize(R.dimen.card_width);

        params.height = getResources()
                .getDimensionPixelSize(R.dimen.card_height);

        params.columnSpec = GridLayout.spec(
                GridLayout.UNDEFINED,
                1f
        );

        int margin = getResources()
                .getDimensionPixelSize(R.dimen.spacing_xs);

        params.setMargins(
                margin,
                margin,
                margin,
                margin
        );

        params.setGravity(Gravity.CENTER);

        return params;
    }
}
