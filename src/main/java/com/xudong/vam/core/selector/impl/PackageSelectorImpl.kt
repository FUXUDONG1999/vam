package com.xudong.vam.core.selector.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xudong.vam.core.config.VamProperties;
import com.xudong.vam.core.helper.PathHelper;
import com.xudong.vam.core.model.SelectDetail;
import com.xudong.vam.core.model.SelectPackage;
import com.xudong.vam.core.model.VamPackage;
import com.xudong.vam.core.model.domain.Metadata;
import com.xudong.vam.core.repository.SelectDetailRepository;
import com.xudong.vam.core.repository.SelectPackageRepository;
import com.xudong.vam.core.repository.VamPackageRepository;
import com.xudong.vam.core.selector.PackageSelector;
import com.xudong.vam.core.utils.JsonUtils;
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

@Slf4j
@Component
@AllArgsConstructor
public class PackageSelectorImpl implements PackageSelector {
    private final VamPackageRepository vamPackageRepository;

    private final SelectPackageRepository selectPackageRepository;

    private final SelectDetailRepository selectDetailRepository;

    private final VamProperties vamProperties;

    private final PathHelper pathHelper;

    @Override
    public List<Long> select(long selectId, long rootId) throws IOException {
        Optional<SelectPackage> selectPackageOptional = selectPackageRepository.findById(selectId);
        if (selectPackageOptional.isEmpty()) {
            return null;
        }

        SelectPackage selectPackage = selectPackageOptional.get();
        String uuid = selectPackage.getUuid();

        Optional<VamPackage> vamPackageOptional = vamPackageRepository.findById(rootId);
        if (vamPackageOptional.isEmpty()) {
            return null;
        }

        Map<Long, VamPackage> packages = new LinkedHashMap<>();
        selectPackages(vamPackageOptional.get(), packages);

        for (VamPackage vamPackage : packages.values()) {
            saveDetail(new SelectDetail(selectId, rootId, vamPackage.getId()));
            linkPackage(vamPackage, uuid);
        }

        symbolicLink(pathHelper.getSelectPath(uuid), Path.of(vamProperties.getModPath()));

        return packages.keySet()
                .stream()
                .toList();
    }

    @Override
    public void unselect(long selectDetailId) throws IOException {
        Optional<SelectDetail> detailOptional = selectDetailRepository.findById(selectDetailId);
        if (detailOptional.isEmpty()) {
            return;
        }

        SelectDetail detail = detailOptional.get();
        Optional<SelectPackage> selectPackageOptional = selectPackageRepository.findById(detail.getSelectId());
        if (selectPackageOptional.isEmpty()) {
            return;
        }
        SelectPackage selectPackage = selectPackageOptional.get();

        Optional<VamPackage> vamPackageOptional = vamPackageRepository.findById(detail.getChildId());
        if (vamPackageOptional.isEmpty()) {
            return;
        }

        VamPackage vamPackage = vamPackageOptional.get();
        unlinkPackage(vamPackage, selectPackage.getUuid());

        selectDetailRepository.deleteById(detail.getId());
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

        Path path = pathHelper.getSelectPath(uuid);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        symbolicLink(Path.of(rootPackage.getPath()), path);
    }

    private void unlinkPackage(VamPackage vamPackage, String uuid) throws IOException {
        Path path = pathHelper.getSelectPath(uuid);
        if (!Files.exists(path)) {
            return;
        }

        path = Path.of(path.toString(), vamPackage.getFileName());
        if (!Files.exists(path)) {
            return;
        }

        Files.delete(path);
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

    private void saveDetail(SelectDetail selectDetail) {
        SelectDetail detail = selectDetailRepository.findBySelectIdAndChildId(selectDetail.getSelectId(), selectDetail.getChildId());
        if (detail != null) {
            return;
        }

        selectDetailRepository.save(selectDetail);
    }
}
