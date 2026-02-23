package com.bookspin.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Book.class, parentColumns = "id", childColumns = "bookId", onDelete = ForeignKey.CASCADE))
public class Pick {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long bookId;
    public long pickedAt;
}
