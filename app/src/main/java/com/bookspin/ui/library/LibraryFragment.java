package com.bookspin.ui.library;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bookspin.data.entities.Book;
import com.bookspin.databinding.FragmentLibraryBinding;
import com.bookspin.viewmodel.BookViewModel;

import java.util.List;

public class LibraryFragment extends Fragment {
    private FragmentLibraryBinding binding;
    private BookViewModel vm;
    private final BookAdapter adapter = new BookAdapter();
    private LiveData<List<Book>> currentSource;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(requireActivity()).get(BookViewModel.class);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        observe(vm.getAllBooks());

        binding.etSearch.addTextChangedListener(new SimpleTextWatcher(s -> {
            vm.search(s, this::observe);
        }));

        adapter.setOnItemClick(book -> new AlertDialog.Builder(requireContext())
                .setTitle(book.title)
                .setMessage("Delete this book?")
                .setPositiveButton("Delete", (d, w) -> vm.getRepo().deleteBook(book))
                .setNegativeButton("Cancel", null)
                .show());
    }

    private void observe(LiveData<List<Book>> source) {
        if (currentSource != null) currentSource.removeObservers(getViewLifecycleOwner());
        currentSource = source;
        source.observe(getViewLifecycleOwner(), adapter::submit);
    }
}
