package com.bookspin.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bookspin.data.entities.Book;
import com.bookspin.data.model.PickWithBookId;
import com.bookspin.repository.BookRepository;
import com.bookspin.util.RandomPicker;

import java.util.ArrayList;
import java.util.List;

public class BookViewModel extends AndroidViewModel {
    private final BookRepository repo;
    private final LiveData<List<Book>> allBooks;
    private final MutableLiveData<Book> pickedBook = new MutableLiveData<>();

    public BookViewModel(@NonNull Application application) {
        super(application);
        repo = new BookRepository(application);
        allBooks = repo.getAllBooks();
    }

    public LiveData<List<Book>> getAllBooks() { return allBooks; }
    public LiveData<Book> getPickedBook() { return pickedBook; }

    public void saveBook(Book book, BookRepository.DuplicateDecision decision, Runnable onComplete) {
        repo.upsertBook(book, decision, onComplete);
    }

    public void search(String query, SearchCallback callback) {
        callback.onResult(repo.searchBooks(query));
    }

    public void pickRandom(int avoidRecentCount) {
        List<Book> books = allBooks.getValue();
        if (books == null || books.isEmpty()) {
            pickedBook.postValue(null);
            return;
        }
        repo.getRecentPicks(10, picks -> {
            List<Long> ids = new ArrayList<>();
            for (PickWithBookId p : picks) ids.add(p.bookId);
            Book selected = RandomPicker.pickRandomBook(books, ids, avoidRecentCount);
            pickedBook.postValue(selected);
        });
    }

    public void markPicked(Book book) {
        if (book != null) repo.insertPick(book.id);
    }

    public BookRepository getRepo() { return repo; }

    public interface SearchCallback { void onResult(LiveData<List<Book>> data); }
}
