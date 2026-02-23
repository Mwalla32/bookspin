package com.bookspin.ui.add;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bookspin.R;
import com.bookspin.data.entities.Book;
import com.bookspin.databinding.FragmentAddBookBinding;
import com.bookspin.network.OpenLibraryClient;
import com.bookspin.util.IsbnUtils;
import com.bookspin.util.Prefs;
import com.bookspin.viewmodel.BookViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddBookFragment extends Fragment {
    private FragmentAddBookBinding binding;
    private BookViewModel vm;
    private int mode = 0;
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final BarcodeScanner scanner = BarcodeScanning.getClient();

    private final ActivityResultLauncher<String> cameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) startCamera();
                else Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            });

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) binding.tvPhotoUri.setText(uri.toString());
            });

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddBookBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(requireActivity()).get(BookViewModel.class);

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Scan Barcode"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Enter ISBN"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Photo + Manual"));
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) { mode = tab.getPosition(); }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.btnOpenCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });
        binding.btnPickImage.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        binding.btnContinue.setOnClickListener(v -> proceedToConfirm());
    }

    private void proceedToConfirm() {
        Book draft = new Book();
        draft.isbn = binding.etIsbn.getText().toString().trim();
        draft.title = binding.etTitle.getText().toString().trim();
        draft.author = binding.etAuthor.getText().toString().trim();
        draft.photoUri = "No photo selected".contentEquals(binding.tvPhotoUri.getText()) ? null : binding.tvPhotoUri.getText().toString();

        if (mode == 1 && !draft.isbn.isEmpty() && !(IsbnUtils.isValidIsbn10(draft.isbn) || IsbnUtils.isValidIsbn13(draft.isbn))) {
            Toast.makeText(requireContext(), "Invalid ISBN", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mode == 2 && draft.title.isEmpty()) {
            Toast.makeText(requireContext(), "Title required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Prefs.isLookupEnabled(requireContext()) && draft.isbn != null && !draft.isbn.isEmpty()) {
            io.execute(() -> {
                OpenLibraryClient.LookupResult result = new OpenLibraryClient().lookupByIsbn(draft.isbn);
                if (result != null) {
                    if ((draft.title == null || draft.title.isEmpty()) && result.title != null) draft.title = result.title;
                    if ((draft.author == null || draft.author.isEmpty()) && result.author != null) draft.author = result.author;
                    draft.coverUrl = result.coverUrl;
                }
                ConfirmBookStore.setDraft(draft);
                requireActivity().runOnUiThread(() -> NavHostFragment.findNavController(this).navigate(R.id.action_add_to_confirm));
            });
        } else {
            ConfirmBookStore.setDraft(draft);
            NavHostFragment.findNavController(this).navigate(R.id.action_add_to_confirm);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> providerFuture = ProcessCameraProvider.getInstance(requireContext());
        providerFuture.addListener(() -> {
            try {
                ProcessCameraProvider provider = providerFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());
                ImageAnalysis analysis = new ImageAnalysis.Builder().build();
                analysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext()), imageProxy -> {
                    if (imageProxy.getImage() == null) {
                        imageProxy.close();
                        return;
                    }
                    InputImage img = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
                    scanner.process(img).addOnSuccessListener(barcodes -> {
                        for (Barcode barcode : barcodes) {
                            String raw = barcode.getRawValue();
                            if (raw != null && raw.matches("97[89]\\d{10}")) {
                                binding.etIsbn.setText(raw);
                                mode = 1;
                                imageProxy.close();
                                provider.unbindAll();
                                return;
                            }
                        }
                    }).addOnCompleteListener(task -> imageProxy.close());
                });
                provider.unbindAll();
                provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis);
            } catch (Exception ignored) {
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }
}
