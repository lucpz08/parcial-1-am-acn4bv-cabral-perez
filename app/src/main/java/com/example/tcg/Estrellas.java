package com.example.tcg;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

/** Arma el texto "★★★★★" con las estrellas llenas en dorado y las vacías en gris. */
public final class Estrellas {

    private Estrellas() {
    }

    public static CharSequence crear(Context context, float rating) {
        int llenas = Math.round(rating);
        int colorLlena = context.getColor(R.color.gold);
        int colorVacia = context.getColor(R.color.border);

        SpannableStringBuilder texto = new SpannableStringBuilder();
        for (int i = 1; i <= 5; i++) {
            int inicio = texto.length();
            texto.append('★');
            texto.setSpan(new ForegroundColorSpan(i <= llenas ? colorLlena : colorVacia),
                    inicio, texto.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return texto;
    }
}
