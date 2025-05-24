package com.koreandubai.handubi.controller;

import com.koreandubai.handubi.controller.dto.GetUploadedImage;
import com.koreandubai.handubi.global.common.StatusEnum;
import com.koreandubai.handubi.global.common.SuccessResponse;
import com.koreandubai.handubi.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    public SuccessResponse uploadImage(@RequestParam("file") MultipartFile file) throws IOException {

        String url = imageService.uploadImage(file);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .data(url)
                .message("Successfully upload image")
                .build();
    }

    @GetMapping("/{imageName}")
    public SuccessResponse getImage(@PathVariable String imageName) {

        GetUploadedImage image = imageService.getImage(imageName);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .data(image)
                .message("Successfully get image")
                .build();
    }
}
