package com.bookspin.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.bookspin.data.dao.BookDao;
import com.bookspin.data.dao.PickDao;
import com.bookspin.data.entities.Book;
import com.bookspin.data.entities.Pick;

@Database(entities = {Book.class, Pick.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract BookDao bookDao();
    public abstract PickDao pickDao();

    public static AppDatabase get(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "bookspin.db").build();
                }
            }
        }
        return INSTANCE;
    }

    public void clearAllBooksAndPicks() {
        runInTransaction(() -> {
            pickDao().clearPicks();
            bookDao().clearBooks();
        });
    }
}
