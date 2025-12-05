package com.example.demo.services;

import com.example.demo.models.Song;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Picks a random song among candidates, avoiding the last 10 picks.
 */
@Service
public class RecommendationService {
    private final Random random = new Random();
    private final Deque<String> recent = new ArrayDeque<>();

    public Optional<Song> pickNext(Collection<Song> candidates) {
        if (candidates == null || candidates.isEmpty()) return Optional.empty();
        List<Song> filtered = new ArrayList<>();
        for (Song s : candidates) {
            if (s != null && s.getId() != null && !recent.contains(s.getId())) {
                filtered.add(s);
            }
        }
        if (filtered.isEmpty()) filtered.addAll(candidates);
        Song pick = filtered.get(random.nextInt(filtered.size()));
        remember(pick);
        return Optional.ofNullable(pick);
    }

    private void remember(Song s) {
        if (s == null || s.getId() == null) return;
        recent.addLast(s.getId());
        while (recent.size() > 10) recent.removeFirst();
    }
}
