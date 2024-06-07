package com.cloud.img.controller;

import com.cloud.img.dto.ImageUploadDto;
import com.cloud.img.entity.Image;
import com.cloud.img.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public Image uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        ImageUploadDto imageUploadDto = new ImageUploadDto();
        imageUploadDto.setFile(file);
        return imageService.uploadImage(imageUploadDto);
    }
}
