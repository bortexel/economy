package ru.ruscalworld.bortexel.economy;

import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Consumer;

public class Ruslan {
    public static void getAnswer(String text, Consumer<Response> callback) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Request(text).getAsJSON());
        okhttp3.Request builder = new okhttp3.Request.Builder()
                .url("https://ruslan.bortexel.ru/getAnswer")
                .header("Content-Type", "application/json")
                .post(body).build();
        new OkHttpClient().newCall(builder).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.accept(null);
            }

            @Override
            public void onResponse(@NotNull Call call, okhttp3.@NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    callback.accept(null);
                    return;
                }

                callback.accept(Response.getFromJSON(responseBody.string()));
                responseBody.close();
            }
        });
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
