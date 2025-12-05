package com.example.demo.services;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {

    private static final String IMAGE_DIR = "src/main/resources/static/images/";

    /**
     * Retourne la liste des URLs d'images disponibles dans le dossier static/images
     */
    public List<String> getAllImageUrls() {
        File folder = new File(IMAGE_DIR);
        File[] files = folder.listFiles((dir, name) -> {
            String lc = name.toLowerCase();
            return lc.endsWith(".jpg") || lc.endsWith(".jpeg") || lc.endsWith(".png") || lc.endsWith(".webp");
        });

        if (files == null) return Collections.emptyList();

        return Arrays.stream(files)
                .map(f -> "/images/" + f.getName()) // URL accessible depuis le navigateur
                .collect(Collectors.toList());
    }
}
