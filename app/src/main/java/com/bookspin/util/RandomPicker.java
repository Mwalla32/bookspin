package com.bookspin.util;

import com.bookspin.data.entities.Book;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomPicker {
    private static final Random RAND = new Random();

    public static Book pickRandomBook(List<Book> allBooks, List<Long> recentBookIds, int avoidRecentCount) {
        if (allBooks == null || allBooks.isEmpty()) return null;
        List<Book> candidates = new ArrayList<>(allBooks);
        if (avoidRecentCount > 0 && recentBookIds != null && !recentBookIds.isEmpty()) {
            Set<Long> excluded = new HashSet<>(recentBookIds.subList(0, Math.min(avoidRecentCount, recentBookIds.size())));
            List<Book> filtered = new ArrayList<>();
            for (Book b : allBooks) {
                if (!excluded.contains(b.id)) filtered.add(b);
            }
            if (!filtered.isEmpty()) {
                candidates = filtered;
            }
        }
        return candidates.get(RAND.nextInt(candidates.size()));
    }
}
