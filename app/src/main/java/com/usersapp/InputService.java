package com.usersapp;

import android.widget.EditText;
import android.widget.TextView;

public class InputService {

    public static boolean fillViewFromEdit(TextView textView, EditText editText) {
        String string = editText.getText().toString();
        if (isStringValidInput(string)) {
            textView.setText(string);
            return true;
        }
        return false;
    }

    public static boolean isStringValidInput(String string) {
        return !string.isEmpty();
    }
}
