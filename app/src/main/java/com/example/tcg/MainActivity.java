package com.example.tcg;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.gridlayout.widget.GridLayout;

import com.bumptech.glide.Glide;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int CANTIDAD_CARTAS = 12;

    private final PokemonTcgApi pokemonTcgApi = new PokemonTcgApi();

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
        pokemonTcgApi.fetchCards(CANTIDAD_CARTAS, new PokemonTcgApi.CardsCallback() {
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
        TextView nroCartasTextview = findViewById(R.id.nroCartasTextview);

        gridLayout.removeAllViews();

        for (Card card : cards) {
            gridLayout.addView(crearImageViewDeCarta(card));
        }

        nroCartasTextview.setText(getString(R.string.cantidad_cartas, cards.size()));
    }

    private ImageView crearImageViewDeCarta(Card card) {
        ImageView imageView = new ImageView(this);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = dpToPx(105);
        params.height = dpToPx(147);
        params.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Glide.with(this)
                .load(card.imageUrl)
                .into(imageView);

        imageView.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, card.name, Toast.LENGTH_SHORT).show());

        return imageView;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}