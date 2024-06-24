package com.xudong.vam.mod.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xudong.vam.concurrent.ConcurrentExecutor;
import com.xudong.vam.config.VamProperties;
import com.xudong.vam.mod.ModSelector;
import com.xudong.vam.model.VamPackage;
import com.xudong.vam.model.domain.Metadata;
import com.xudong.vam.repository.VamPackageRepository;
import com.xudong.vam.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@AllArgsConstructor
public class ModSelectorImpl implements ModSelector {
    private final VamPackageRepository vamPackageRepository;

    private final VamProperties vamProperties;

    private final ConcurrentExecutor concurrentExecutor;

    @Override
    public void select(long id) throws IOException {
        Optional<VamPackage> optional = vamPackageRepository.findById(id);
        if (optional.isEmpty()) {
            return;
        }

        VamPackage vamPackage = optional.get();
        selectPackage(vamPackage);
    }

    @Override
    public void clear() throws IOException {
        String modPath = vamProperties.getModPath();
        File file = Path.of(modPath).toFile();
        String[] list = file.list();
        if (list == null) {
            return;
        }

        for (String item : list) {
            Files.delete(Path.of(modPath, item));
        }
    }

    private void selectPackage(VamPackage vamPackage) throws IOException {
        if (vamPackage == null) {
            return;
        }

        String modPath = vamProperties.getModPath();
        Path source = Path.of(vamPackage.getPath());
        Path target = Path.of(modPath, source.getFileName().toString());
        if (Files.exists(target)) {
            return;
        }

        Files.createSymbolicLink(target, source);

        String dependenciesJson = vamPackage.getDependencies();
        if (dependenciesJson == null) {
            return;
        }

        Map<String, Metadata> dependencies = JsonUtils.fromJson(dependenciesJson, new TypeReference<>() {
        });
        selectDependencies(dependencies);
    }

    private void selectDependencies(Map<String, Metadata> dependencies) {
        if (dependencies == null || dependencies.isEmpty()) {
            return;
        }

        List<CompletableFuture<Void>> futures = new LinkedList<>();

        for (Map.Entry<String, Metadata> metadataEntry : dependencies.entrySet()) {
            futures.add(concurrentExecutor.execute(() -> {
                try {
                    String key = metadataEntry.getKey();
                    String[] strings = key.split("\\.");
                    String creatorName = strings[0];
                    String name = strings[1];

                    List<VamPackage> vamPackages = vamPackageRepository.findAllByCreatorNameAndName(creatorName, name);
                    for (VamPackage vamPackage : vamPackages) {
                        selectPackage(vamPackage);
                    }

                    Metadata metadata = metadataEntry.getValue();
                    if (metadata == null) {
                        return null;
                    }

                    selectDependencies(metadata.getDependencies());

                    return null;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        concurrentExecutor.wait(futures);
    }
}
