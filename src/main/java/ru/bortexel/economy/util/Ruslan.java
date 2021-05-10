package ru.bortexel.economy.util;

import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import ru.ruscalworld.bortexel4j.core.Callback;

import java.io.IOException;

public class Ruslan {
    public static void getAnswer(String text, Callback<Response> callback, OkHttpClient client) {
        RequestBody body = RequestBody.create(new Request(text).getAsJSON(), MediaType.parse("application/json"));
        okhttp3.Request builder = new okhttp3.Request.Builder()
                .url("https://ruslan.bortexel.ru/getAnswer")
                .header("Content-Type", "application/json")
                .post(body).build();
        client.newCall(builder).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.handle(null);
            }

            @Override
            public void onResponse(@NotNull Call call, okhttp3.@NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    callback.handle(null);
                    return;
                }

                callback.handle(Response.getFromJSON(responseBody.string()));
                responseBody.close();
            }
        });
        new OkHttpClient().dispatcher().executorService().shutdown();
    }

    public static class Request {
        private final String text;

        public Request(String message) {
            this.text = message;
        }

        public String getAsJSON() {
            return new Gson().toJson(this);
        }

        public String getText() {
            return text;
        }
    }

    public static class Response {
        private final String response;

        public Response(String response) {
            this.response = response;
        }

        public static Response getFromJSON(String json) {
            return new Gson().fromJson(json, Response.class);
        }

        public String getResponse() {
            return response;
        }
    }
}
