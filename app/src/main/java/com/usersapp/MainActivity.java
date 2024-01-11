package com.usersapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout relativeLayout = findViewById(R.id.blur);
        relativeLayout.getBackground().setAlpha(50);

        try {
            Utils.test();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        recievePayloads();
    }

    public void fillLayoutFromArray(Payload[] payloads) {
        for (Payload payload : payloads) {
            LinearLayout userLayout = (LinearLayout) findViewById(R.id.userLayout);
            ConstraintLayout newCard = (ConstraintLayout) getLayoutInflater().inflate(R.layout.user_card_layout, userLayout, false);
            userLayout.addView(newCard);
            View userCard = findViewById(R.id.userCard);
            TextView textViewName = findViewById(R.id.textViewName);
            TextView textViewPassword = findViewById(R.id.textViewPassword);
            TextView textViewLogin = findViewById(R.id.textViewLogin);
            TextView textViewDate = findViewById(R.id.textViewDate);
            textViewName.setText(payload.getInfo().getName());
            textViewPassword.setText(payload.getInfo().getPassword());
            textViewLogin.setText(payload.getInfo().getLogin());
            textViewDate.setText(payload.getDate());
            userCard.setId(View.generateViewId());
            textViewPassword.setId(View.generateViewId());
            textViewLogin.setId(View.generateViewId());
            textViewDate.setId(View.generateViewId());
            textViewName.setId(View.generateViewId());
        }
    }

    private void recievePayloads() {
        Payload[] payloads;
        Utils.get(Utils.url + "read", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ERROR", String.valueOf(e));
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.i("success", responseStr);
                    runOnUiThread(() -> {
                        fillLayoutFromArray(Utils.gson.fromJson(responseStr, Payload[].class));
                    });
                } else {
                    Log.e("failure", "Error. Response code " + response.code());
                }
            }
        });
    }
}