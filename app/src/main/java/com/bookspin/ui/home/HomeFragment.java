package com.bookspin.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bookspin.R;
import com.bookspin.databinding.FragmentHomeBinding;
import com.bookspin.viewmodel.BookViewModel;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private BookViewModel vm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(requireActivity()).get(BookViewModel.class);
        vm.getAllBooks().observe(getViewLifecycleOwner(), books -> {
            int count = books == null ? 0 : books.size();
            binding.tvStats.setText("Total books: " + count);
        });
        vm.getRepo().getLastPick(pick -> {
            if (getActivity() == null) return;
            requireActivity().runOnUiThread(() -> {
                if (pick == null) binding.tvLastPicked.setText("Last picked: none");
                else vm.getRepo().getBookById(pick.bookId, book -> requireActivity().runOnUiThread(() ->
                        binding.tvLastPicked.setText(book == null ? "Last picked: unknown" : "Last picked: " + book.title)));
            });
        });

        binding.btnAdd.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.action_home_to_add));
        binding.btnPick.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.action_home_to_picker));
    }
}
