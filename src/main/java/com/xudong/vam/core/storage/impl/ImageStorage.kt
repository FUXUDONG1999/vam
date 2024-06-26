package com.xudong.vam.core.storage.impl;

import com.xudong.vam.core.model.domain.Image;
import com.xudong.vam.core.storage.Storage;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class ImageStorage implements Storage<Image> {
    @Override
    public String store(String path, Image image) {
        if (image == null || path == null) {
            return null;
        }

        String imagePath = image.getPath();
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }

        try {
            String name = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            byte[] content = image.getContent();

            Path dest = Path.of(path, name);
            if (Files.exists(dest)) {
                return dest.toString();
            }
            Files.write(dest, content);

            return dest.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
