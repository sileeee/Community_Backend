package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.GetUploadedImage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${upload.directory}")
    private String uploadDir;

    @Value("${upload.user}")
    private String user;


    public String uploadImage(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new EntityNotFoundException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image file can be uploaded.");
        }

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Unable to create upload directory.");
            }
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID() + fileExtension;

        File targetFile = new File(dir, uniqueFileName);

        file.transferTo(targetFile);

        targetFile.setReadable(true, false);

        Path path = targetFile.toPath();
        UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
        UserPrincipal owner = lookupService.lookupPrincipalByName(user);
        Files.setOwner(path, owner);

        return "https://handubi.com/api/posts/images/" + uniqueFileName;
    }

    public GetUploadedImage getImage(String imageName){

        Path imagePath = Paths.get(uploadDir).resolve(imageName);

        Resource resource = new FileSystemResource(imagePath);

        if (!resource.exists()) {
            throw new EntityNotFoundException("image not found");
        }

        String fileExtension = imageName.substring(imageName.lastIndexOf(".") + 1).toLowerCase();
        MediaType mediaType = switch (fileExtension) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };

        return GetUploadedImage.builder()
                .contentType(mediaType)
                .body(resource)
                .build();
    }
}
