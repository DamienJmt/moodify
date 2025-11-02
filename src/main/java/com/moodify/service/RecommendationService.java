package com.moodify.service;

import com.moodify.model.Song;
import java.util.*;

public class RecommendationService {
    private final Random random = new Random();
    private final Deque<String> recent = new ArrayDeque<>();

    public Optional<Song> pickNext(Collection<Song> candidates) {
        if (candidates == null || candidates.isEmpty()) return Optional.empty();
        List<Song> filtered = new ArrayList<>();
        for (Song s : candidates) if (!recent.contains(s.getId())) filtered.add(s);
        if (filtered.isEmpty()) filtered.addAll(candidates);
        Song pick = filtered.get(random.nextInt(filtered.size()));
        remember(pick);
        return Optional.of(pick);
    }

    private void remember(Song s) {
        recent.addLast(s.getId());
        while (recent.size() > 10) recent.removeFirst();
    }
}
