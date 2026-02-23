package com.bookspin.network;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OpenLibraryClient {
    private final OkHttpClient client = new OkHttpClient();

    @Nullable
    public LookupResult lookupByIsbn(String isbn) {
        String url = "https://openlibrary.org/isbn/" + isbn + ".json";
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) return null;
            JSONObject obj = new JSONObject(response.body().string());
            LookupResult result = new LookupResult();
            result.title = obj.optString("title", null);
            result.coverUrl = "https://covers.openlibrary.org/b/isbn/" + isbn + "-L.jpg";
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static class LookupResult {
        public String title;
        public String author;
        public String coverUrl;
    }
}
