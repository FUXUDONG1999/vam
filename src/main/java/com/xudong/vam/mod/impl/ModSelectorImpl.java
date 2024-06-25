package com.xudong.vam.mod.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xudong.vam.config.VamProperties;
import com.xudong.vam.mod.ModSelector;
import com.xudong.vam.model.VamPackage;
import com.xudong.vam.model.VamPackageUsage;
import com.xudong.vam.model.domain.Metadata;
import com.xudong.vam.repository.VamPackageRepository;
import com.xudong.vam.repository.VamPackageUsageRepository;
import com.xudong.vam.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class ModSelectorImpl implements ModSelector {
    private final VamPackageRepository vamPackageRepository;

    private final VamPackageUsageRepository vamPackageUsageRepository;

    private final VamProperties vamProperties;

    @Override
    public void select(String usageName, long rootId) throws IOException {
        Optional<VamPackage> optional = vamPackageRepository.findById(rootId);
        if (optional.isEmpty()) {
            return;
        }

        String uuid = UUID.randomUUID().toString();
        List<VamPackageUsage> packageUsages = vamPackageUsageRepository.findAllByRootId(rootId);
        if (packageUsages != null && !packageUsages.isEmpty()) {
            uuid = packageUsages.get(0).getUuid();
        }

        VamPackage rootPackage = optional.get();
        Map<Long, VamPackage> packages = new LinkedHashMap<>();
        selectPackages(rootPackage, packages);

        for (VamPackage vamPackage : packages.values()) {
            linkPackage(vamPackage, uuid);
            saveUsage(new VamPackageUsage(usageName, rootPackage.getId(), vamPackage.getId(), uuid));
        }

        symbolicLink(usagePath(uuid), Path.of(vamProperties.getModPath()));
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

    private void selectPackages(VamPackage rootPackage, Map<Long, VamPackage> vamPackages) {
        if (vamPackages.containsKey(rootPackage.getId())) {
            return;
        }

        vamPackages.put(rootPackage.getId(), rootPackage);
        String dependenciesJson = rootPackage.getDependencies();
        if (dependenciesJson == null) {
            return;
        }

        selectDependencies(
                JsonUtils.fromJson(dependenciesJson, new TypeReference<>() {
                }),
                vamPackages
        );
    }

    private void linkPackage(VamPackage rootPackage, String uuid) throws IOException {
        if (rootPackage == null) {
            return;
        }

        Path path = usagePath(uuid);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        symbolicLink(Path.of(rootPackage.getPath()), path);
    }

    private void selectDependencies(Map<String, Metadata> dependencies, Map<Long, VamPackage> vamPackages) {
        if (dependencies == null || dependencies.isEmpty()) {
            return;
        }

        Set<String> creators = new HashSet<>();
        Set<String> names = new HashSet<>();

        dependencies.forEach((key, value) -> {
            String[] strings = key.split("\\.");
            String creatorName = strings[0];
            String name = strings[1];

            creators.add(creatorName);
            names.add(name);

            selectDependencies(value.getDependencies(), vamPackages);
        });

        vamPackageRepository.findAllByCreatorNameInAndNameIn(creators, names)
                .forEach(vamPackage -> selectPackages(vamPackage, vamPackages));
    }

    private void symbolicLink(Path source, Path dest) throws IOException {
        dest = Path.of(dest.toString(), source.getFileName().toString());
        if (Files.exists(dest)) {
            return;
        }

        Files.createSymbolicLink(dest, source);
    }

    private Path usagePath(String uuid) {
        return Path.of(vamProperties.getGamePath(), "vam-packages-link", uuid);
    }

    private void saveUsage(VamPackageUsage usage) {
        VamPackageUsage savedUsage = vamPackageUsageRepository.findByUuidAndDependencyId(usage.getUuid(), usage.getDependencyId());
        if (savedUsage != null) {
            return;
        }

        vamPackageUsageRepository.save(usage);
    }
}
