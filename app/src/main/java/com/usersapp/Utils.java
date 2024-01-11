package com.usersapp;


import android.app.Activity;


import android.util.Log;

import com.google.gson.Gson;

import okhttp3.*;

import java.io.IOException;

public class Utils {
    static Gson gson = new Gson();
    private static OkHttpClient client = new OkHttpClient();
    static String url = "https://rest-full-for-edu.onrender.com/api/";

    public static final MediaType JSON = MediaType.get("application/json");

    public static void test() throws IOException {
        Payload payload = new Payload("John Doe", "jdoe666", "S@7@n9UL3Z");
        String json = gson.toJson(payload);
        Log.d("tag", json);
        get(url + "read", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("failure", "FAIL!!!XDXD");
                Log.e("ERROR", String.valueOf(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.i("success", responseStr);
                    Payload[] payloads = gson.fromJson(responseStr, Payload[].class);
                    Log.i("test", "test");
                } else {
                    Log.e("failure", "Error. Response code " + response.code());
                }
            }
        });
//        post(url + "create", json, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e("failure", "FAIL!!!XDXD");
//                Log.e("ERROR", String.valueOf(e));
//            }
//
//            @Override public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    String responseStr = response.body().string();
//                    Log.i("success", responseStr);
//                } else {
//                    Log.e("failure", "Error. Response code " + response.code());
//                }
//            }
//        });

    }


    static Call get(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
    static Call post(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
}
