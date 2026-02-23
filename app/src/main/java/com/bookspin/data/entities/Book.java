package com.bookspin.data.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"isbn"}, unique = true)})
public class Book {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String title;
    public String author;
    public String isbn;
    public String barcode;
    public String coverUrl;
    public String photoUri;
    public long createdAt;
    public long updatedAt;
}
