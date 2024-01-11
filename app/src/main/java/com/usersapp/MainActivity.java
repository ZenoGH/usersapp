package com.usersapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RelativeLayout;


import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout relativeLayout = findViewById(R.id.blur);
        relativeLayout.getBackground().setAlpha(200);

        try {
            Utils.test();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}