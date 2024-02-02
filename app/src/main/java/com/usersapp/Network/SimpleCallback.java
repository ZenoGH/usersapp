package com.usersapp.Network;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SimpleCallback implements Callback {
    private final ResponseHandler onSuccess;

    public SimpleCallback(ResponseHandler onSuccess) {
        this.onSuccess = onSuccess;
    }
    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        Log.e("NetworkFailure", "Something failed. Check error.");
        Log.e("NetworkError", String.valueOf(e));
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        if (response.isSuccessful()) {
            String responseStr;
            try {
                responseStr = response.body().string();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Log.i("NetworkSuccess", responseStr);
            onSuccess.handleResponse(responseStr);

        } else {
            Log.e("NetworkFailure", "Response code " + response.code());
        }
    }
}
