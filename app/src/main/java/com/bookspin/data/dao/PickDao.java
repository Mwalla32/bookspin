package com.bookspin.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.bookspin.data.entities.Pick;
import com.bookspin.data.model.PickWithBookId;

import java.util.List;

@Dao
public interface PickDao {
    @Insert
    long insertPick(Pick pick);

    @Query("SELECT bookId, pickedAt FROM Pick ORDER BY pickedAt DESC LIMIT :limit")
    List<PickWithBookId> getRecentPicks(int limit);

    @Query("DELETE FROM Pick")
    void clearPicks();

    @Query("SELECT * FROM Pick ORDER BY pickedAt DESC LIMIT 1")
    Pick getLastPick();
}
