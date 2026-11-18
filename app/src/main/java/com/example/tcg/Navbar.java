package com.example.tcg;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

/**
 * Configura la barra de navegación inferior (layout/navbar.xml) que comparten
 * Álbum, Explorar y Perfil.
 */
public final class Navbar {

    private static final int[] BOTONES = {
            R.id.albumButton, R.id.explorarButton, R.id.chatsButton, R.id.perfilButton
    };

    private Navbar() {
    }


    public static void configurar(Activity activity, int botonActivoId) {
        for (int id : BOTONES) {
            Button boton = activity.findViewById(id);
            boolean activo = id == botonActivoId;

            boton.setTextColor(activity.getColor(
                    activo ? R.color.primary : R.color.text_muted
            ));

            if (activo) {
                boton.setOnClickListener(null);

            } else if (id == R.id.albumButton) {
                boton.setOnClickListener(v ->
                        navegar(activity, MainActivity.class));

            } else if (id == R.id.explorarButton) {
                boton.setOnClickListener(v ->
                        navegar(activity, ExplorarActivity.class));

            } else if (id == R.id.chatsButton) {
                boton.setOnClickListener(v ->
                        navegar(activity, ChatsActivity.class));

            } else if (id == R.id.perfilButton) {
                boton.setOnClickListener(v ->
                        navegar(activity, PerfilActivity.class));

            } else {
                boton.setOnClickListener(v ->
                        Toast.makeText(
                                activity,
                                R.string.proximamente,
                                Toast.LENGTH_SHORT
                        ).show());
            }
        }
    }


    private static void navegar(Activity origen, Class<? extends Activity> destino) {
        Intent intent = new Intent(origen, destino);
        // Reutiliza la pantalla si ya estaba abierta (el álbum no se vuelve a cargar)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        origen.startActivity(intent);
    }
}
