package com.bookspin.ui.settings;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.bookspin.databinding.FragmentSettingsBinding;
import com.bookspin.util.Prefs;
import com.bookspin.viewmodel.AuthViewModel;
import com.bookspin.viewmodel.BookViewModel;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private AuthViewModel authVm;
    private BookViewModel bookVm;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        authVm = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        bookVm = new ViewModelProvider(requireActivity()).get(BookViewModel.class);

        renderAuth();
        authVm.account.observe(getViewLifecycleOwner(), account -> renderAuth());

        binding.switchLookup.setChecked(Prefs.isLookupEnabled(requireContext()));
        binding.switchLookup.setOnCheckedChangeListener((b, checked) -> Prefs.setLookupEnabled(requireContext(), checked));

        binding.seekAvoid.setProgress(Prefs.avoidRecentCount(requireContext()));
        binding.tvAvoidCount.setText("Avoid recent count: " + Prefs.avoidRecentCount(requireContext()));
        binding.seekAvoid.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Prefs.setAvoidRecentCount(requireContext(), progress);
                binding.tvAvoidCount.setText("Avoid recent count: " + progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        binding.btnClearPicks.setOnClickListener(v -> bookVm.getRepo().clearPicks());
        binding.btnClearAll.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Clear all local data")
                .setMessage("This cannot be undone")
                .setPositiveButton("Clear", (d, w) -> bookVm.getRepo().clearAll())
                .setNegativeButton("Cancel", null)
                .show());
    }

    private void renderAuth() {
        if (authVm.account.getValue() != null) {
            binding.tvAuthStatus.setText("Signed in as " + authVm.account.getValue().getDisplayName());
            binding.btnAuthAction.setText("Sign out");
            binding.btnAuthAction.setOnClickListener(v -> authVm.getAuthManager().signOut(requireActivity(), authVm::refresh));
        } else {
            binding.tvAuthStatus.setText("Not signed in");
            binding.btnAuthAction.setText("Sign in");
            binding.btnAuthAction.setOnClickListener(v -> {
                // return to login screen
                requireActivity().recreate();
            });
        }
    }
}
