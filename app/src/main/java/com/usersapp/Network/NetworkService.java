package com.usersapp.Network;

import com.google.gson.Gson;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NetworkService {

    public enum RequestType {
        PUT,
        GET,
        POST,
        DELETE
    }

    public final OkHttpClient client;
    public static final MediaType JSON = MediaType.get("application/json");
    private final String url;

    private Gson gson = new Gson();
    private EntryRepository repository;



    public NetworkService(String url, EntryRepository repository) {
        this.url = url;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.SECONDS)
                .writeTimeout(0, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .build();
        this.repository = repository;
    }

    public Call request(RequestType requestType, String path, String json, Callback callback) {
        String url = this.url + path;
        Request.Builder requestBuilder = new Request.Builder().url(url);
        RequestBody body = RequestBody.create(json, JSON);
        switch (requestType) {
            case PUT:
                requestBuilder.put(body);
                break;
            case POST:
                requestBuilder.post(body);
                break;
            case GET:
                break;
            case DELETE:
                requestBuilder.delete();
                break;
        }
        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call request(RequestType requestType, String path, Callback callback) {
        switch (requestType) {
            case PUT:
            case POST:
                throw new RuntimeException("This request requires a body.");
        }
        return request(requestType, path, "", callback);
    }

    public void updateRepository() {
        repository.clearEntries();
        CompletableFuture<Void> future = new CompletableFuture<>();
        request(NetworkService.RequestType.GET, "read", new SimpleCallback(response -> {;
            Entry[] entries = gson.fromJson(response, Entry[].class);
            for (Entry entry : entries) {
                repository.add(entry);
            }
            future.complete(null);
        }));

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
