package com.example.tcg;

public class Conversacion {

    private final String nombre;
    private final String mensaje;
    private final String categoria;
    private final String hora;
    private final int mensajesSinLeer;

    public Conversacion(String nombre, String mensaje,
                        String categoria, String hora,
                        int mensajesSinLeer) {
        this.nombre = nombre;
        this.mensaje = mensaje;
        this.categoria = categoria;
        this.hora = hora;
        this.mensajesSinLeer = mensajesSinLeer;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getHora() {
        return hora;
    }

    public int getMensajesSinLeer() {
        return mensajesSinLeer;
    }
}
