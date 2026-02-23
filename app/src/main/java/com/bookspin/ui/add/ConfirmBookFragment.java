package com.bookspin.ui.add;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bookspin.data.entities.Book;
import com.bookspin.databinding.FragmentConfirmBookBinding;
import com.bookspin.repository.BookRepository;
import com.bookspin.viewmodel.BookViewModel;

public class ConfirmBookFragment extends Fragment {
    private FragmentConfirmBookBinding binding;
    private BookViewModel vm;
    private Book draft;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentConfirmBookBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(requireActivity()).get(BookViewModel.class);
        draft = ConfirmBookStore.consume();
        if (draft == null) draft = new Book();

        binding.etTitle.setText(draft.title);
        binding.etAuthor.setText(draft.author);
        binding.etIsbn.setText(draft.isbn);
        binding.etBarcode.setText(draft.barcode);
        binding.etCover.setText(draft.coverUrl);
        binding.etPhotoUri.setText(draft.photoUri);

        binding.btnSave.setOnClickListener(v -> saveFlow());
    }

    private void saveFlow() {
        draft.title = binding.etTitle.getText().toString().trim();
        draft.author = binding.etAuthor.getText().toString().trim();
        draft.isbn = binding.etIsbn.getText().toString().trim();
        draft.barcode = binding.etBarcode.getText().toString().trim();
        draft.coverUrl = binding.etCover.getText().toString().trim();
        draft.photoUri = binding.etPhotoUri.getText().toString().trim();

        if (draft.title == null || draft.title.isEmpty()) {
            Toast.makeText(requireContext(), "Title required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (draft.isbn != null && !draft.isbn.isEmpty()) {
            vm.getRepo().getBookByIsbn(draft.isbn, existing -> requireActivity().runOnUiThread(() -> {
                if (existing != null) {
                    showDuplicateDialog();
                } else {
                    persist(BookRepository.DuplicateDecision.SAVE_NEW);
                }
            }));
        } else {
            persist(BookRepository.DuplicateDecision.SAVE_NEW);
        }
    }

    private void showDuplicateDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Already in your library")
                .setMessage("Update existing / Save as new / Cancel")
                .setPositiveButton("Update existing", (d, w) -> persist(BookRepository.DuplicateDecision.UPDATE_EXISTING))
                .setNegativeButton("Save as new", (d, w) -> persist(BookRepository.DuplicateDecision.SAVE_NEW))
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void persist(BookRepository.DuplicateDecision decision) {
        vm.saveBook(draft, decision, () -> {
            if (getActivity() == null) return;
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
                NavHostFragment.findNavController(this).popBackStack();
            });
        });
    }
}
