package com.usersapp;


import static com.usersapp.Network.NetworkService.RequestType.DELETE;

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


import com.usersapp.Network.EntryRepository;
import com.usersapp.Network.NetworkService;
import com.usersapp.Network.Entry;
import com.usersapp.Network.SimpleCallback;

import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    Entry[] currentData;
    //    int[] userIds;
    View[] userCards;
    private final EntryRepository entryRepository = new EntryRepository();
    private final NetworkService networkService = new NetworkService("https://rest-full-for-edu.onrender.com/api/", entryRepository);
    private EditMenu editMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EditText editTextSearch = findViewById(R.id.editTextSearch);
        networkService.updateRepository();
        fillLayoutFromArray(entryRepository.getEntries());
        editMenu = new EditMenu(this, this, networkService);

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

    public void fillLayoutFromArray(Entry[] entries) {
        LinearLayout userLayout = (LinearLayout) findViewById(R.id.userLayout);
        userLayout.removeAllViews();
        currentData = entries;
        int dataLength = currentData.length;
        int n = -1;
        userCards = new View[dataLength];

        for (Entry entry : entries) {
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

            textViewName.setText(entry.getInfo().getName());
            textViewPassword.setText(entry.getInfo().getPassword());
            textViewLogin.setText(entry.getInfo().getLogin());
            textViewDate.setText(entry.getDate());

            userCard.setId(entry.getId());
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
                ((TextView) editMenu.bottomSheet.findViewById(R.id.editTextLogin))
                        .setText(entry.getInfo().getLogin());
                ((TextView) editMenu.bottomSheet.findViewById(R.id.editTextName))
                        .setText(entry.getInfo().getName());
                ((TextView) editMenu.bottomSheet.findViewById(R.id.editTextPassword))
                        .setText(entry.getInfo().getPassword());
                ((TextView) editMenu.bottomSheet.findViewById(R.id.textViewId))
                        .setText(String.valueOf(entry.getId()));

                editMenu.openEditMenu((View) v.getParent().getParent());
            }));
        }
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
