package com.example.tcg;

import android.graphics.Typeface;
import android.widget.ImageButton;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ConversationActivity extends AppCompatActivity {

    private LinearLayout messagesContainer;
    private ScrollView messagesScrollView;
    private EditText messageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        TextView conversationName = findViewById(R.id.conversationName);
        TextView conversationCategory = findViewById(R.id.conversationCategory);

        messagesContainer = findViewById(R.id.messagesContainer);
        messagesScrollView = findViewById(R.id.messagesScrollView);
        messageEditText = findViewById(R.id.messageEditText);

        Button sendMessageButton = findViewById(R.id.sendMessageButton);
        ImageButton backButton = findViewById(R.id.backButton);

        // Recuperar datos enviados desde ChatsActivity
        String nombre = getIntent().getStringExtra("nombre");
        String categoria = getIntent().getStringExtra("categoria");

        if (nombre == null) nombre = "Conversación";
        if (categoria == null) categoria = "";

        conversationName.setText(nombre);
        conversationCategory.setText(categoria);

        // Mensajes de ejemplo
        cargarMensajesEjemplo(nombre);

        // Volver a la lista de chats
        backButton.setOnClickListener(v -> finish());

        // Enviar mensaje
        sendMessageButton.setOnClickListener(v -> enviarMensaje());
    }

    private void cargarMensajesEjemplo(String nombre) {

        agregarMensaje("¡Hola! ¿Cómo estás?", false);
        agregarMensaje("¡Buenas! Todo bien, ¿vos?", true);

        if (nombre.equals("Martina Gómez")) {
            agregarMensaje(
                    "¿Te sirve la Charizard por mi Umbreon?",
                    false
            );
        } else if (nombre.equals("Juanpi_TCG")) {
            agregarMensaje(
                    "Dale, te la guardo hasta el sábado",
                    false
            );
        } else if (nombre.equals("Sofía Rivas")) {
            agregarMensaje(
                    "Perfecto, te paso el alias para transferir",
                    false
            );
        } else if (nombre.equals("Nico Ferreyra")) {
            agregarMensaje(
                    "¿Nos vemos en el local de Palermo?",
                    false
            );
        } else if (nombre.equals("Lucía Paz")) {
            agregarMensaje(
                    "Buenísimo, ya la marqué como reclamada",
                    false
            );
        }
    }

    private void agregarMensaje(String texto, boolean propio) {

        // Contenedor de la burbuja
        LinearLayout fila = new LinearLayout(this);
        fila.setGravity(propio ? Gravity.END : Gravity.START);
        fila.setPadding(0, 8, 0, 8);

        LinearLayout.LayoutParams filaParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        fila.setLayoutParams(filaParams);

        // Texto del mensaje
        TextView mensaje = new TextView(this);
        mensaje.setText(texto);
        mensaje.setTextSize(14);
        mensaje.setTextColor(
                getColor(propio ? R.color.white : R.color.text)
        );
        mensaje.setPadding(32, 24, 32, 24);

        // Fondo redondeado
        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(
                getColor(propio ? R.color.primary : R.color.surface)
        );
        fondo.setCornerRadius(32);

        mensaje.setBackground(fondo);

        LinearLayout.LayoutParams mensajeParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        mensajeParams.width = getResources()
                .getDisplayMetrics().widthPixels * 3 / 4;

        mensaje.setLayoutParams(mensajeParams);

        fila.addView(mensaje);
        messagesContainer.addView(fila);

        // Desplazar hasta el último mensaje
        messagesScrollView.post(() ->
                messagesScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        );
    }

    private void enviarMensaje() {

        String texto = messageEditText.getText().toString().trim();

        // Evitar mensajes vacíos
        if (texto.isEmpty()) {
            messageEditText.setError("Escribí un mensaje");
            return;
        }

        agregarMensaje(texto, true);

        // Limpiar el campo
        messageEditText.setText("");
    }
}
