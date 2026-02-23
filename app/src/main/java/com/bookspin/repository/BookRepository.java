package com.bookspin.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.bookspin.data.dao.BookDao;
import com.bookspin.data.dao.PickDao;
import com.bookspin.data.db.AppDatabase;
import com.bookspin.data.entities.Book;
import com.bookspin.data.entities.Pick;
import com.bookspin.data.model.PickWithBookId;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookRepository {
    private final BookDao bookDao;
    private final PickDao pickDao;
    private final AppDatabase db;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    public BookRepository(Context context) {
        db = AppDatabase.get(context);
        bookDao = db.bookDao();
        pickDao = db.pickDao();
    }

    public LiveData<List<Book>> getAllBooks() { return bookDao.getAllBooks(); }
    public LiveData<List<Book>> searchBooks(String q) { return bookDao.searchBooks(q); }

    public void insertBook(Book book, Runnable onComplete) {
        io.execute(() -> {
            long now = System.currentTimeMillis();
            if (book.createdAt == 0) book.createdAt = now;
            book.updatedAt = now;
            bookDao.insertBook(book);
            if (onComplete != null) onComplete.run();
        });
    }

    public void upsertBook(Book book, DuplicateDecision decision, Runnable onComplete) {
        io.execute(() -> {
            long now = System.currentTimeMillis();
            Book existing = (book.isbn == null || book.isbn.trim().isEmpty()) ? null : bookDao.getBookByIsbn(book.isbn);
            if (existing != null && decision != DuplicateDecision.SAVE_NEW) {
                existing.title = book.title;
                existing.author = book.author;
                existing.coverUrl = book.coverUrl;
                existing.photoUri = book.photoUri;
                existing.barcode = book.barcode;
                existing.updatedAt = now;
                bookDao.updateBook(existing);
            } else {
                if (book.createdAt == 0) book.createdAt = now;
                book.updatedAt = now;
                bookDao.insertBook(book);
            }
            if (onComplete != null) onComplete.run();
        });
    }

    public void deleteBook(Book book) { io.execute(() -> bookDao.deleteBook(book)); }

    public void insertPick(long bookId) {
        io.execute(() -> {
            Pick pick = new Pick();
            pick.bookId = bookId;
            pick.pickedAt = System.currentTimeMillis();
            pickDao.insertPick(pick);
        });
    }

    public void getRecentPicks(int limit, RecentCallback callback) {
        io.execute(() -> callback.onLoaded(pickDao.getRecentPicks(limit)));
    }

    public void getBookByIsbn(String isbn, BookCallback callback) {
        io.execute(() -> callback.onLoaded(bookDao.getBookByIsbn(isbn)));
    }

    public void clearPicks() { io.execute(pickDao::clearPicks); }
    public void clearAll() { io.execute(db::clearAllBooksAndPicks); }
    public void getLastPick(PickCallback callback) { io.execute(() -> callback.onLoaded(pickDao.getLastPick())); }
    public void getBookById(long id, BookCallback callback) { io.execute(() -> callback.onLoaded(bookDao.getById(id))); }

    public interface RecentCallback { void onLoaded(List<PickWithBookId> picks); }
    public interface BookCallback { void onLoaded(Book book); }
    public interface PickCallback { void onLoaded(Pick pick); }

    public enum DuplicateDecision { UPDATE_EXISTING, SAVE_NEW, CANCEL }
}
