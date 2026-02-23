package com.bookspin.ui.library;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bookspin.R;
import com.bookspin.data.entities.Book;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.VH> {
    private final List<Book> books = new ArrayList<>();
    private OnItemClick onItemClick;

    public void submit(List<Book> data) {
        books.clear();
        if (data != null) books.addAll(data);
        notifyDataSetChanged();
    }

    public void setOnItemClick(OnItemClick onItemClick) { this.onItemClick = onItemClick; }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int p) {
        Book b = books.get(p);
        h.title.setText(b.title);
        h.author.setText(b.author == null || b.author.isEmpty() ? "Unknown author" : b.author);
        if (b.photoUri != null && !b.photoUri.isEmpty()) {
            h.thumb.setImageURI(Uri.parse(b.photoUri));
        } else {
            h.thumb.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        h.itemView.setOnClickListener(v -> {
            if (onItemClick != null) onItemClick.onClick(b);
        });
    }

    @Override public int getItemCount() { return books.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, author;
        ImageView thumb;
        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            author = itemView.findViewById(R.id.tvAuthor);
            thumb = itemView.findViewById(R.id.ivThumb);
        }
    }

    interface OnItemClick { void onClick(Book book); }
}
