package com.bookspin.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class AuthManager {
    private final GoogleSignInClient client;

    public AuthManager(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(context, gso);
    }

    public Intent signInIntent() {
        return client.getSignInIntent();
    }

    public GoogleSignInAccount getCurrent(Context context) {
        return GoogleSignIn.getLastSignedInAccount(context);
    }

    public void signOut(Activity activity, Runnable onDone) {
        client.signOut().addOnCompleteListener(activity, task -> onDone.run());
    }
}
