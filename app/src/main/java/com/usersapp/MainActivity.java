package com.usersapp;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Payload[] currentData;
    int[] userIds;
    View[] userCards;
    Integer[] editButtonIds;
    Integer[] deleteButtonIds;
    int[] passwordIds;
    int[] loginIds;
    int[] nameIds;
    int[] dateIds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout relativeLayout = findViewById(R.id.darkBackground);
        Button buttonCancel = findViewById(R.id.buttonCancel);
        EditText editTextSearch = findViewById(R.id.editTextSearch);

        relativeLayout.getBackground().setAlpha(50);

        try {
            Utils.test();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        recievePayloads();

        buttonCancel.setOnClickListener((View v) -> {
            runOnUiThread(this::closeEditMenu);
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                runOnUiThread(() -> {
                    closeEditMenu();
                });
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        editTextSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String query = String.valueOf(editable);
                search(query.toLowerCase());
            }
        });
    }

    public void fillLayoutFromArray(Payload[] payloads) {
        LinearLayout userLayout = (LinearLayout) findViewById(R.id.userLayout);
        userLayout.removeAllViews();
        currentData = payloads;
        int data_length = currentData.length;
        int n = -1;
        userIds = new int[data_length];
        deleteButtonIds = new Integer[data_length];
        editButtonIds = new Integer[data_length];
        passwordIds = new int[data_length];
        loginIds = new int[data_length];
        nameIds = new int[data_length];
        dateIds = new int[data_length];
        userCards = new View[data_length];
        for (Payload payload : payloads) {
            n++;
            ConstraintLayout newCard = (ConstraintLayout) getLayoutInflater()
                    .inflate(R.layout.user_card_layout, userLayout, false);
            userLayout.addView(newCard);

            View userCard = findViewById(R.id.userCard);
            TextView textViewName = findViewById(R.id.textViewName);
            TextView textViewPassword = findViewById(R.id.textViewPassword);
            TextView textViewLogin = findViewById(R.id.textViewLogin);
            TextView textViewDate = findViewById(R.id.textViewDate);
            Button deleteButton = findViewById(R.id.deleteButton);
            Button editButton = findViewById(R.id.editButton);

            textViewName.setText(payload.getInfo().getName());
            textViewPassword.setText(payload.getInfo().getPassword());
            textViewLogin.setText(payload.getInfo().getLogin());
            textViewDate.setText(payload.getDate());

            userCard.setId(View.generateViewId());
            textViewPassword.setId(View.generateViewId());
            textViewLogin.setId(View.generateViewId());
            textViewDate.setId(View.generateViewId());
            textViewName.setId(View.generateViewId());
            deleteButton.setId(View.generateViewId());
            editButton.setId(View.generateViewId());


            userIds[n] = n + 1;
            passwordIds[n] = textViewPassword.getId();
            loginIds[n] = textViewLogin.getId();
            nameIds[n] = textViewName.getId();
            dateIds[n] = textViewDate.getId();
            editButtonIds[n] = editButton.getId();
            deleteButtonIds[n] = deleteButton.getId();
            userCards[n] = userCard;

            deleteButton.setOnClickListener((View v) -> {
                int index = Utils.getIndex(deleteButtonIds, v.getId());
                String path = "delete/" + String.valueOf(index + 1);
                Utils.delete(Utils.url + path, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("failure", "FAIL!!!XDXD");
                        Log.e("ERROR", String.valueOf(e));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            runOnUiThread(() -> {
                                ViewGroup parent = (ViewGroup) userCard.getParent();
                                parent.removeView(userCard);
                            });
                        } else {
                            Log.e("failure", "Error. Response code " + response.code());
                        }
                    }
                });
            });
            editButton.setOnClickListener((View v) -> {
                runOnUiThread(() -> {
                    ((TextView)findViewById(R.id.editTextLogin)).setText(payload.getInfo().getLogin());
                    ((TextView)findViewById(R.id.editTextName)).setText(payload.getInfo().getName());
                    ((TextView)findViewById(R.id.editTextPassword)).setText(payload.getInfo().getPassword());
                    ((TextView)findViewById(R.id.textViewId)).setText(String.valueOf(payload.getId()));
                    openEditMenu(Utils.getIndex(editButtonIds, v.getId()));
                });
            });
        }
    }

    private void recievePayloads() {
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

    private void openEditMenu(int index) {
        ConstraintLayout editMenu = findViewById(R.id.editMenuBackground);
        editMenu.setVisibility(View.VISIBLE);
        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener((View v) -> {
            TextView nameView = findViewById(nameIds[index]);
            TextView loginView = findViewById(loginIds[index]);
            TextView passwordView = findViewById(passwordIds[index]);
            EditText nameEdit = findViewById(R.id.editTextName);
            EditText loginEdit = findViewById(R.id.editTextLogin);
            EditText passwordEdit = findViewById(R.id.editTextPassword);
            nameView.setText(nameEdit.getText().toString());
            loginView.setText(loginEdit.getText().toString());
            passwordView.setText(passwordEdit.getText().toString());
            Payload newPayload = new Payload(nameEdit.getText().toString(), loginEdit.getText().toString(), passwordEdit.getText().toString());
            Utils.put(Utils.url + "update/" + String.valueOf(index + 1), Utils.gson.toJson(newPayload, Payload.class), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("failure", "FAIL!!!XDXD");
                    Log.e("ERROR", String.valueOf(e));
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        Log.i("success", responseStr);
                        runOnUiThread(() -> {
                            closeEditMenu();
                        });
                    } else {
                        Log.e("failure", "Error. Response code " + response.code());
                    }
                }
            });
        });
    }

    private void closeEditMenu() {
        ConstraintLayout editMenu = findViewById(R.id.editMenuBackground);
        editMenu.setVisibility(View.GONE);
    }

    private void search(String query) {
        if (userCards == null) {
            return;
        }
        int index = -1;
        for (View userCard : userCards) {
            index++;
            TextView nameView = findViewById(nameIds[index]);
            TextView loginView = findViewById(loginIds[index]);
            TextView passwordView = findViewById(passwordIds[index]);
            TextView dateView = findViewById(dateIds[index]);
            String name = nameView.getText().toString().toLowerCase();
            String login = loginView.getText().toString().toLowerCase();
            String password = passwordView.getText().toString().toLowerCase();
            String date = dateView.getText().toString().toLowerCase();
            runOnUiThread(() -> {
                if (name.contains(query) || login.contains(query) || password.contains(query) || date.contains(query)) {
                    userCard.setVisibility(View.VISIBLE);
                }
                else {
                    userCard.setVisibility(View.GONE);
                }
            });
        }
    }
}
