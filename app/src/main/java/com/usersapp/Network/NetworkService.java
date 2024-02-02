package com.usersapp.Network;

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

    public static final OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json");
    private final String url;



    public NetworkService(String url) {
        this.url = url;
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
}
