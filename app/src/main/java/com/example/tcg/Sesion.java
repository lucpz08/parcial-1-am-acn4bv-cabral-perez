package com.example.tcg;

import java.util.Calendar;

/**
 * Datos del usuario logueado, compartidos entre pantallas mientras la app está abierta.
 * No hay backend todavía, así que todo vive en memoria.
 */
public final class Sesion {

    private static String email;
    private static String nombre;
    private static String provincia;
    private static int anioAlta;
    private static int cantidadCartas;
    private static int cantidadResenas;
    private static float rating;

    private Sesion() {
    }

    public static void iniciar(String emailUsuario) {
        email = emailUsuario;
        nombre = getUsername();
        provincia = null;
        anioAlta = Calendar.getInstance().get(Calendar.YEAR);
        cantidadCartas = 0;
        cantidadResenas = 0;
        rating = 0f;
    }

    public static void cerrar() {
        email = null;
        nombre = null;
        provincia = null;
    }

    public static String getUsername() {
        if (email == null) return "";
        int arroba = email.indexOf('@');
        return arroba > 0 ? email.substring(0, arroba) : email;
    }

    public static String getNombre() {
        return nombre;
    }

    public static void setNombre(String nuevoNombre) {
        nombre = nuevoNombre;
    }

    public static String getProvincia() {
        return provincia;
    }

    public static void setProvincia(String nuevaProvincia) {
        provincia = nuevaProvincia;
    }

    public static int getAnioAlta() {
        return anioAlta;
    }

    public static int getCantidadCartas() {
        return cantidadCartas;
    }

    public static void setCantidadCartas(int cantidad) {
        cantidadCartas = cantidad;
    }

    public static int getCantidadResenas() {
        return cantidadResenas;
    }

    public static float getRating() {
        return rating;
    }
}
