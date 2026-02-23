package com.bookspin.ui.add;

import com.bookspin.data.entities.Book;

public class ConfirmBookStore {
    private static Book draft;
    public static void setDraft(Book book) { draft = book; }
    public static Book consume() {
        Book b = draft;
        draft = null;
        return b;
    }
}
