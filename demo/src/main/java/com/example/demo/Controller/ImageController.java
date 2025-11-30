package com.example.demo.Controller;

import com.example.demo.services.ImageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Endpoint pour récupérer toutes les images
     */
    @GetMapping("/api/images")
    public List<String> getImages() {
        return imageService.getAllImageUrls();
    }
}
