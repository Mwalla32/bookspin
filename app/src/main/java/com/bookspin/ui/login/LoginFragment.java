package com.bookspin.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bookspin.R;
import com.bookspin.databinding.FragmentLoginBinding;
import com.bookspin.util.Prefs;
import com.bookspin.viewmodel.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    authViewModel.refresh();
                    goHome();
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        if (authViewModel.account.getValue() != null || Prefs.skippedLogin(requireContext())) {
            goHome();
            return;
        }

        binding.btnGoogle.setOnClickListener(v -> signInLauncher.launch(authViewModel.getAuthManager().signInIntent()));
        binding.btnSkip.setOnClickListener(v -> {
            Prefs.setSkippedLogin(requireContext(), true);
            goHome();
        });
    }

    private void goHome() {
        NavHostFragment.findNavController(this).navigate(R.id.action_login_to_home);
    }
}
