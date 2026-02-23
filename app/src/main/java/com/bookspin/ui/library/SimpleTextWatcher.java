package com.bookspin.ui.library;

import android.text.Editable;
import android.text.TextWatcher;

public class SimpleTextWatcher implements TextWatcher {
    private final Callback callback;

    public SimpleTextWatcher(Callback callback) {
        this.callback = callback;
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
    @Override public void afterTextChanged(Editable s) { callback.onChange(s.toString()); }

    interface Callback { void onChange(String value); }
}
