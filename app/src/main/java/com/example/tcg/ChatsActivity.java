package com.example.tcg;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {

    private LinearLayout conversationsContainer;

    private Button filterAllButton;
    private Button filterTradesButton;
    private Button filterClaimsButton;
    private Button filterSalesButton;

    private final List<Conversacion> conversaciones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        // Referencias a los elementos de la interfaz
        conversationsContainer = findViewById(R.id.conversationsContainer);

        filterAllButton = findViewById(R.id.filterAllButton);
        filterTradesButton = findViewById(R.id.filterTradesButton);
        filterClaimsButton = findViewById(R.id.filterClaimsButton);
        filterSalesButton = findViewById(R.id.filterSalesButton);

        // Inicialización
        cargarConversaciones();
        configurarFiltros();

        // Navegación centralizada
        Navbar.configurar(this, R.id.chatsButton);

        // Botón para nueva conversación
        configurarNuevaConversacion();

        // Mostrar todas las conversaciones inicialmente
        mostrarConversaciones("Todos");
    }

    private void cargarConversaciones() {

        conversaciones.add(new Conversacion(
                "Martina Gómez",
                "¿Te sirve la Charizard por mi Umbreon?",
                "Intercambios",
                "15:42",
                2
        ));

        conversaciones.add(new Conversacion(
                "Juanpi_TCG",
                "Dale, te la guardo hasta el sábado",
                "Claims",
                "14:10",
                1
        ));

        conversaciones.add(new Conversacion(
                "Sofía Rivas",
                "Perfecto, te paso el alias para transferir",
                "Ventas",
                "Ayer",
                0
        ));

        conversaciones.add(new Conversacion(
                "Nico Ferreyra",
                "¿Nos vemos en el local de Palermo?",
                "Intercambios",
                "Ayer",
                0
        ));

        conversaciones.add(new Conversacion(
                "Lucía Paz",
                "Buenísimo, ya la marqué como reclamada",
                "Claims",
                "Lun",
                0
        ));
    }

    private void configurarFiltros() {

        filterAllButton.setOnClickListener(v ->
                mostrarConversaciones("Todos"));

        filterTradesButton.setOnClickListener(v ->
                mostrarConversaciones("Intercambios"));

        filterClaimsButton.setOnClickListener(v ->
                mostrarConversaciones("Claims"));

        filterSalesButton.setOnClickListener(v ->
                mostrarConversaciones("Ventas"));
    }

    private void mostrarConversaciones(String filtro) {

        // Limpiar la lista anterior
        conversationsContainer.removeAllViews();

        int cantidadMostrada = 0;

        for (Conversacion conversacion : conversaciones) {

            // Mostrar solamente la categoría seleccionada
            if (!filtro.equals("Todos")
                    && !conversacion.getCategoria().equals(filtro)) {
                continue;
            }

            agregarConversacion(conversacion);
            cantidadMostrada++;
        }

        // Mensaje si no hay conversaciones
        if (cantidadMostrada == 0) {

            TextView vacio = new TextView(this);
            vacio.setText(R.string.chats_empty);
            vacio.setGravity(Gravity.CENTER);
            vacio.setTextColor(getColor(R.color.text_muted));
            vacio.setPadding(16, 32, 16, 32);

            conversationsContainer.addView(vacio);
        }

        actualizarBotones(filtro);
    }


    private void agregarConversacion(Conversacion conversacion) {

        // Cargar el diseño XML de cada conversación
        View fila = getLayoutInflater().inflate(
                R.layout.item_conversacion,
                conversationsContainer,
                false
        );

        TextView avatar = fila.findViewById(R.id.chatAvatar);
        TextView nombre = fila.findViewById(R.id.chatName);
        TextView categoria = fila.findViewById(R.id.chatCategory);
        TextView mensaje = fila.findViewById(R.id.chatLastMessage);
        TextView hora = fila.findViewById(R.id.chatTime);
        TextView noLeidos = fila.findViewById(R.id.chatUnreadCount);

        // Cargar los datos
        nombre.setText(conversacion.getNombre());
        mensaje.setText(conversacion.getMensaje());
        hora.setText(conversacion.getHora());

        // Categoría
        String tipo = conversacion.getCategoria();

        if (tipo.equals("Intercambios")) {
            categoria.setText("Intercambio");
        } else if (tipo.equals("Claims")) {
            categoria.setText("Claim");
        } else {
            categoria.setText("Venta");
        }

        // Iniciales del avatar
        String[] partes = conversacion.getNombre().split(" ");

        String iniciales = partes[0].substring(0, 1);

        if (partes.length > 1) {
            iniciales += partes[1].substring(0, 1);
        } else if (partes[0].length() > 1) {
            iniciales += partes[0].substring(1, 2);
        }

        avatar.setText(iniciales.toUpperCase());

        // Colores diferentes según el usuario
        int colorAvatar;

        switch (conversacion.getNombre()) {
            case "Martina Gómez":
                colorAvatar = 0xFFF5A14F;
                break;
            case "Juanpi_TCG":
                colorAvatar = 0xFF5C8FF0;
                break;
            case "Sofía Rivas":
                colorAvatar = 0xFFE45F9C;
                break;
            case "Nico Ferreyra":
                colorAvatar = 0xFFB57A45;
                break;
            default:
                colorAvatar = 0xFF9773E8;
                break;
        }

        android.graphics.drawable.GradientDrawable fondoAvatar =
                new android.graphics.drawable.GradientDrawable();

        fondoAvatar.setShape(
                android.graphics.drawable.GradientDrawable.OVAL
        );

        fondoAvatar.setColor(colorAvatar);
        avatar.setBackground(fondoAvatar);

        // Contador de mensajes sin leer
        int cantidad = conversacion.getMensajesSinLeer();

        if (cantidad > 0) {
            noLeidos.setVisibility(View.VISIBLE);
            noLeidos.setText(String.valueOf(cantidad));
            hora.setTextColor(getColor(R.color.primary));
        } else {
            noLeidos.setVisibility(View.GONE);
            hora.setTextColor(getColor(R.color.text_muted));
        }

        // Abrir conversación
        fila.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ChatsActivity.this,
                    ConversationActivity.class
            );

            intent.putExtra("nombre", conversacion.getNombre());
            intent.putExtra("categoria", conversacion.getCategoria());

            startActivity(intent);
        });

        conversationsContainer.addView(fila);

        // Separador
        View separador = new View(this);
        separador.setBackgroundColor(getColor(R.color.border));

        LinearLayout.LayoutParams separadorParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                );

        separadorParams.setMargins(64, 0, 12, 0);

        conversationsContainer.addView(separador, separadorParams);
    }


    private void actualizarBotones(String filtro) {

        Button[] botones = {
                filterAllButton,
                filterTradesButton,
                filterClaimsButton,
                filterSalesButton
        };

        String[] categorias = {
                "Todos",
                "Intercambios",
                "Claims",
                "Ventas"
        };

        for (int i = 0; i < botones.length; i++) {

            boolean seleccionado = categorias[i].equals(filtro);

            // Color de fondo según el filtro activo
            botones[i].setBackgroundTintList(
                    ColorStateList.valueOf(
                            getColor(seleccionado
                                    ? R.color.primary
                                    : R.color.surface)
                    )
            );

            // Color del texto según el filtro activo
            botones[i].setTextColor(
                    getColor(seleccionado
                            ? R.color.white
                            : R.color.text_muted)
            );
        }
    }


    private void configurarNuevaConversacion() {

        findViewById(R.id.newChatButton).setOnClickListener(v ->
                Toast.makeText(
                        this,
                        R.string.chats_new,
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

}
