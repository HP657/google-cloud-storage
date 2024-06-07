package com.cloud.img.service;

import com.cloud.img.dto.ImageUploadDto;
import com.cloud.img.entity.Image;
import com.cloud.img.repository.ImageRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    private final ImageRepository imageRepository;
    private final Storage storage;
    private final ResourceLoader resourceLoader;

    public ImageService(ImageRepository imageRepository, ResourceLoader resourceLoader, @Value("${spring.cloud.gcp.storage.credentials.location}") String credentialsLocation) throws IOException {
        this.imageRepository = imageRepository;
        this.resourceLoader = resourceLoader;

        Resource resource = resourceLoader.getResource(credentialsLocation);
        GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream());

        this.storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }

    public Image uploadImage(ImageUploadDto imageUploadDto) throws IOException {
        MultipartFile file = imageUploadDto.getFile();
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        storage.create(blobInfo, file.getBytes());

        String fileUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);

        Image image = new Image();
        image.setUrl(fileUrl);

        return imageRepository.save(image);
    }
}
