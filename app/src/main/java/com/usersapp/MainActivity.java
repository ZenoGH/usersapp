package com.usersapp;


import static com.usersapp.Network.NetworkService.RequestType.DELETE;
import static com.usersapp.Network.NetworkService.RequestType.PUT;

import static java.lang.invoke.VarHandle.AccessMode.GET;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.usersapp.Network.NetworkService;
import com.usersapp.Network.Payload;
import com.usersapp.Network.SimpleCallback;

public class MainActivity extends AppCompatActivity {
    BottomSheetDialog bottomSheet;
    Payload[] currentData;
    //    int[] userIds;
    View[] userCards;
//    Integer[] editButtonIds;
//    Integer[] deleteButtonIds;
//    int[] passwordIds;
//    int[] loginIds;
//    int[] nameIds;
//    int[] dateIds;

    private final NetworkService networkService = new NetworkService("https://rest-full-for-edu.onrender.com/api/");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        EditText editTextSearch = findViewById(R.id.editTextSearch);
        bottomSheet = new BottomSheetDialog(this, R.style.NewDialog);
        bottomSheet.setContentView(R.layout.edit_slider_layout);
        recievePayloads();

        editTextSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

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
        int dataLength = currentData.length;
        int n = -1;
        userCards = new View[dataLength];

        for (Payload payload : payloads) {
            n++;
            ConstraintLayout newCard = (ConstraintLayout) getLayoutInflater()
                    .inflate(R.layout.user_card_layout, userLayout, false);
            userLayout.addView(newCard);

            View userCard = findViewById(R.id.userCard);
            TextView textViewName = userCard.findViewById(R.id.textViewName);
            TextView textViewPassword = userCard.findViewById(R.id.textViewPassword);
            TextView textViewLogin = userCard.findViewById(R.id.textViewLogin);
            TextView textViewDate = userCard.findViewById(R.id.textViewDate);
            Button deleteButton = userCard.findViewById(R.id.deleteButton);
            Button editButton = userCard.findViewById(R.id.editButton);

            textViewName.setText(payload.getInfo().getName());
            textViewPassword.setText(payload.getInfo().getPassword());
            textViewLogin.setText(payload.getInfo().getLogin());
            textViewDate.setText(payload.getDate());

            userCard.setId(payload.getId());
            userCards[n] = userCard;

            deleteButton.setOnClickListener((View v) -> {
                String path = "delete/" + v.getId();
                networkService.request(DELETE, path, new SimpleCallback(response -> {
                    runOnUiThread(() -> {
                        ViewGroup parent = (ViewGroup) userCard.getParent();
                        parent.removeView(userCard);
                    });
                }
                ));
            });
            editButton.setOnClickListener((View v) -> runOnUiThread(() -> {
                ((TextView) bottomSheet.findViewById(R.id.editTextLogin))
                        .setText(payload.getInfo().getLogin());
                ((TextView) bottomSheet.findViewById(R.id.editTextName))
                        .setText(payload.getInfo().getName());
                ((TextView) bottomSheet.findViewById(R.id.editTextPassword))
                        .setText(payload.getInfo().getPassword());
                ((TextView) bottomSheet.findViewById(R.id.textViewId))
                        .setText(String.valueOf(payload.getId()));

                openEditMenu((View) v.getParent().getParent());
            }));
        }
    }

    private void recievePayloads() {
        networkService.request(NetworkService.RequestType.GET, "read", new SimpleCallback(response -> {
            runOnUiThread(() -> {
                fillLayoutFromArray(Utils.gson.fromJson(response, Payload[].class));
            });
        }));
    }

    private void openEditMenu(View view) {
        bottomSheet.setDismissWithAnimation(true);
        BottomSheetBehavior behavior = bottomSheet.getBehavior();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);
        behavior.setHideable(true);
        Button buttonCancel = bottomSheet.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener((View v) -> runOnUiThread(this::closeEditMenu));
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                runOnUiThread(() -> closeEditMenu());
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
        Button buttonSave = bottomSheet.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener((View v) -> {
            TextView nameView = view.findViewById(R.id.textViewName);
            TextView loginView = view.findViewById(R.id.textViewLogin);
            TextView passwordView = view.findViewById(R.id.textViewPassword);

            EditText nameEdit = bottomSheet.findViewById(R.id.editTextName);
            EditText loginEdit = bottomSheet.findViewById(R.id.editTextLogin);
            EditText passwordEdit = bottomSheet.findViewById(R.id.editTextPassword);
            if (!InputService.fillViewFromEdit(nameView, nameEdit)
                    || !InputService.fillViewFromEdit(loginView, loginEdit)
                    || !InputService.fillViewFromEdit(passwordView, passwordEdit)) {
                return;
            }
            Payload newPayload =
                    new Payload(
                            nameEdit.getText().toString(),
                            loginEdit.getText().toString(),
                            passwordEdit.getText().toString()
                    );
            networkService.request(PUT,
                    "update/" + view.getId(),
                    Utils.gson.toJson(newPayload, Payload.class),
                    new SimpleCallback(response -> {
                        runOnUiThread(this::closeEditMenu);
                    }));
        });
        bottomSheet.show();
    }

    private void closeEditMenu() {
        bottomSheet.dismiss();
    }

    private void search(String query) {
        if (userCards == null) {
            return;
        }
        for (View userCard : userCards) {
            TextView nameView = userCard.findViewById(R.id.textViewName);
            TextView loginView = userCard.findViewById(R.id.textViewLogin);
            TextView passwordView = userCard.findViewById(R.id.textViewPassword);
            TextView dateView = userCard.findViewById(R.id.textViewDate);
            String name = nameView.getText().toString().toLowerCase();
            String login = loginView.getText().toString().toLowerCase();
            String password = passwordView.getText().toString().toLowerCase();
            String date = dateView.getText().toString().toLowerCase();
            runOnUiThread(() -> {
                if (name.contains(query)
                        || login.contains(query)
                        || password.contains(query)
                        || date.contains(query)
                ) {
                    userCard.setVisibility(View.VISIBLE);
                } else {
                    userCard.setVisibility(View.GONE);
                }
            });
        }
    }

}
