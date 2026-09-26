package com.example.tcg;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PokemonTcgApi {

    private static final String CARDS_URL = "https://api.pokemontcg.io/v2/cards";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainThread = new Handler(Looper.getMainLooper());

    public interface CardsCallback {
        void onSuccess(List<Card> cards);
        void onError(Exception e);
    }

    public void fetchCards(int pageSize, CardsCallback callback) {
        execute("?pageSize=" + pageSize, callback);
    }

    public void searchCards(String query, CardsCallback callback) {
        try {
            String q = URLEncoder.encode("name:" + query.trim() + "*", StandardCharsets.UTF_8.name());
            execute("?q=" + q + "&pageSize=20", callback);
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    private void execute(String querySuffix, CardsCallback callback) {
        executor.execute(() -> {
            try {
                List<Card> cards = requestCards(querySuffix);
                mainThread.post(() -> callback.onSuccess(cards));
            } catch (Exception e) {
                mainThread.post(() -> callback.onError(e));
            }
        });
    }

    private List<Card> requestCards(String querySuffix) throws Exception {
        URL url = new URL(CARDS_URL + querySuffix);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } finally {
            connection.disconnect();
        }

        JSONArray data = new JSONObject(response.toString()).getJSONArray("data");
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject cardJson = data.getJSONObject(i);
            String name = cardJson.getString("name");
            String imageUrl = cardJson.getJSONObject("images").getString("small");
            cards.add(new Card(name, imageUrl));
        }
        return cards;
    }
}
