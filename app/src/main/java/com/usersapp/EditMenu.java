package com.usersapp;

import static com.usersapp.Network.NetworkService.RequestType.PUT;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.usersapp.Network.NetworkService;
import com.usersapp.Network.Entry;
import com.usersapp.Network.SimpleCallback;

public class EditMenu {
    BottomSheetDialog bottomSheet;
    private Context context;
    private NetworkService networkService;
    private  LifecycleOwner lifecycleOwner;
    private Gson gson = new Gson();
    EditMenu(Context context, LifecycleOwner lifecycleOwner, NetworkService networkService) {
        bottomSheet = new BottomSheetDialog(context, R.style.NewDialog);
        bottomSheet.setContentView(R.layout.edit_slider_layout);
        this.context = context;
        this.networkService = networkService;
        this.lifecycleOwner = lifecycleOwner;
    }
    void openEditMenu(View view) {
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
        getOnBackPressedDispatcher().addCallback(lifecycleOwner, callback);
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
            Entry newEntry =
                    new Entry(
                            nameEdit.getText().toString(),
                            loginEdit.getText().toString(),
                            passwordEdit.getText().toString()
                    );
            networkService.request(PUT,
                    "update/" + view.getId(),
                    gson.toJson(newEntry, Entry.class),
                    new SimpleCallback(response -> {
                        runOnUiThread(this::closeEditMenu);
                    }));
        });
        bottomSheet.show();
    }

    void closeEditMenu() {
        bottomSheet.dismiss();
    }

    void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }
    OnBackPressedDispatcher getOnBackPressedDispatcher() {
        return ((AppCompatActivity) context).getOnBackPressedDispatcher();
    }
}
