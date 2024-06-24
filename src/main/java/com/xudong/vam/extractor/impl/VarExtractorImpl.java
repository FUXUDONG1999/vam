package com.xudong.vam.extractor.impl;

import com.xudong.vam.concurrent.ConcurrentExecutor;
import com.xudong.vam.extractor.VarExtractor;
import com.xudong.vam.model.domain.Image;
import com.xudong.vam.model.domain.Metadata;
import com.xudong.vam.model.domain.Package;
import com.xudong.vam.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@AllArgsConstructor
public class VarExtractorImpl implements VarExtractor {
    private static final String METADATA = "meta.json";

    private final ConcurrentExecutor concurrentExecutor;

    @Override
    public Package extract(Path packagePath) {
        if (packagePath == null || !Files.exists(packagePath)) {
            throw new IllegalArgumentException("Package path is null or empty");
        }
        try (ZipFile zipFile = ZipFile.builder().setPath(packagePath).get()) {
            Metadata metadata = extractMetadata(zipFile);

            return new Package(packagePath, metadata, extractImage(zipFile, metadata));
        } catch (Exception e) {
            log.error("Extract package error:{}, {}", packagePath, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CompletableFuture<Package>> extractAll(Path packagePath) {
        if (packagePath == null || !Files.exists(packagePath)) {
            throw new IllegalArgumentException("Package path is null or empty");
        }

        List<Path> paths = getPackagesPath(packagePath, new ArrayList<>());
        return concurrentExecutor.executeAll(paths, this::extract);
    }

    private List<Path> getPackagesPath(Path packagePath, List<Path> packages) {
        if (Files.isDirectory(packagePath)) {
            String[] fileNames = packagePath.toFile()
                    .list();
            if (fileNames == null) {
                return packages;
            }

            for (String fileName : fileNames) {
                Path path = Path.of(packagePath.toString(), fileName);
                getPackagesPath(path, packages);
            }

            return packages;
        }

        String fileName = packagePath.getFileName().toString();
        if (!fileName.endsWith(".zip") && !fileName.endsWith(".var")) {
            return packages;
        }

        packages.add(packagePath);
        return packages;
    }

    private Metadata extractMetadata(ZipFile zipFile) throws IOException {
        byte[] bytes = extractContent(zipFile, METADATA);
        if (bytes == null) {
            return null;
        }

        String metadataJson = new String(bytes, StandardCharsets.UTF_8).replace("\uFEFF", "");

        return JsonUtils.fromJson(metadataJson, Metadata.class);
    }

    private Image extractImage(ZipFile zipFile, Metadata metadata) throws IOException {
        if (metadata == null) {
            return null;
        }

        List<String> contentList = metadata.getContentList();
        if (contentList == null || contentList.isEmpty()) {
            return null;
        }

        List<String> imagePaths = contentList.stream()
                .filter(name -> name.endsWith(".jpg"))
                .toList();
        if (imagePaths.isEmpty()) {
            return null;
        }

        for (String imagePath : imagePaths) {
            byte[] content = extractContent(zipFile, imagePath);
            if (content == null) {
                continue;
            }

            return new Image(imagePath, content);
        }

        return null;
    }

    private byte[] extractContent(ZipFile zipFile, String path) throws IOException {
        ZipArchiveEntry entry = zipFile.getEntry(path);
        if (entry == null) {
            return null;
        }

        InputStream inputStream = zipFile.getInputStream(entry);
        return inputStream.readAllBytes();
    }
}
