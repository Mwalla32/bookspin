package com.bookspin.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bookspin.data.entities.Book;

import java.util.List;

@Dao
public interface BookDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertBook(Book book);

    @Update
    void updateBook(Book book);

    @Delete
    void deleteBook(Book book);

    @Query("SELECT * FROM Book ORDER BY updatedAt DESC")
    LiveData<List<Book>> getAllBooks();

    @Query("SELECT * FROM Book WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    LiveData<List<Book>> searchBooks(String query);

    @Query("SELECT * FROM Book WHERE isbn = :isbn LIMIT 1")
    Book getBookByIsbn(String isbn);

    @Query("SELECT * FROM Book WHERE id = :id LIMIT 1")
    Book getById(long id);

    @Query("DELETE FROM Book")
    void clearBooks();
}
