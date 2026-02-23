package com.bookspin.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.bookspin.auth.AuthManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class AuthViewModel extends AndroidViewModel {
    private final AuthManager authManager;
    public final MutableLiveData<GoogleSignInAccount> account = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authManager = new AuthManager(application);
        account.setValue(authManager.getCurrent(application));
    }

    public AuthManager getAuthManager() { return authManager; }
    public void refresh() { account.postValue(authManager.getCurrent(getApplication())); }
}
