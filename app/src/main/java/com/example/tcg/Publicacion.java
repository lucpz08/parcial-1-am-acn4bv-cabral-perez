package com.example.tcg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Publicación del marketplace (pantalla Explorar). */
public class Publicacion {

    public enum Intencion {
        VENDO(R.color.primary),
        CAMBIO(R.color.accent),
        BUSCO(R.color.gold);

        public final int colorRes;

        Intencion(int colorRes) {
            this.colorRes = colorRes;
        }
    }

    public final String nombreCarta;
    public final String set;
    public final String numero;
    public final String imageUrl;
    public final Intencion intencion;
    /** null cuando la publicación no tiene precio (cambio / búsqueda). */
    public final Integer precio;
    public final String vendedor;
    public final float ratingVendedor;
    public final String provincia;
    /** Orden de publicación: mayor = más reciente. */
    public final int orden;

    public Publicacion(String nombreCarta, String set, String numero, String imageUrl,
                       Intencion intencion, Integer precio, String vendedor,
                       float ratingVendedor, String provincia, int orden) {
        this.nombreCarta = nombreCarta;
        this.set = set;
        this.numero = numero;
        this.imageUrl = imageUrl;
        this.intencion = intencion;
        this.precio = precio;
        this.vendedor = vendedor;
        this.ratingVendedor = ratingVendedor;
        this.provincia = provincia;
        this.orden = orden;
    }

    /** Publicaciones de ejemplo hasta que haya un backend. */
    public static List<Publicacion> ejemplos() {
        String base = "https://images.pokemontcg.io/base1/";
        return new ArrayList<>(Arrays.asList(
                new Publicacion("Charizard", "Base", "4", base + "4.png",
                        Intencion.VENDO, 85000, "Ash", 4.8f, "Buenos Aires", 8),
                new Publicacion("Blastoise", "Base", "2", base + "2.png",
                        Intencion.CAMBIO, null, "Misty", 4.2f, "CABA", 7),
                new Publicacion("Pikachu", "Base", "58", base + "58.png",
                        Intencion.VENDO, 3500, "Brock", 3.9f, "Córdoba", 6),
                new Publicacion("Mewtwo", "Base", "10", base + "10.png",
                        Intencion.BUSCO, null, "Gary", 4.5f, "Santa Fe", 5),
                new Publicacion("Venusaur", "Base", "15", base + "15.png",
                        Intencion.VENDO, 42000, "Erika", 5f, "Mendoza", 4),
                new Publicacion("Alakazam", "Base", "1", base + "1.png",
                        Intencion.CAMBIO, null, "Sabrina", 4.0f, "Buenos Aires", 3),
                new Publicacion("Gyarados", "Base", "6", base + "6.png",
                        Intencion.VENDO, 18000, "Misty", 4.2f, "CABA", 2),
                new Publicacion("Machamp", "Base", "8", base + "8.png",
                        Intencion.BUSCO, null, "Bruno", 3.5f, "Tucumán", 1)
        ));
    }
}
