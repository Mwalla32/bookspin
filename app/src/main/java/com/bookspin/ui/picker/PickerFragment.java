package com.bookspin.ui.picker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bookspin.databinding.FragmentPickerBinding;
import com.bookspin.util.Prefs;
import com.bookspin.viewmodel.BookViewModel;

public class PickerFragment extends Fragment {
    private FragmentPickerBinding binding;
    private BookViewModel vm;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPickerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(requireActivity()).get(BookViewModel.class);
        vm.getPickedBook().observe(getViewLifecycleOwner(), book -> {
            if (book == null) binding.tvResult.setText("No books yet. Add one first.");
            else binding.tvResult.setText("Tonight's book: " + book.title + (book.author == null ? "" : " by " + book.author));
        });

        binding.btnPickAgain.setOnClickListener(v -> {
            int avoid = binding.switchAvoid.isChecked() ? Prefs.avoidRecentCount(requireContext()) : 0;
            vm.pickRandom(avoid);
        });

        binding.btnMarkRead.setOnClickListener(v -> {
            if (vm.getPickedBook().getValue() == null) {
                Toast.makeText(requireContext(), "Pick first", Toast.LENGTH_SHORT).show();
                return;
            }
            vm.markPicked(vm.getPickedBook().getValue());
            Toast.makeText(requireContext(), "Marked read tonight", Toast.LENGTH_SHORT).show();
        });
    }
}
