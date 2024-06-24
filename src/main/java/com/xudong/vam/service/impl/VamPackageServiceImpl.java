package com.xudong.vam.service.impl;

import com.xudong.vam.builder.VamPackageBuilder;
import com.xudong.vam.config.VamProperties;
import com.xudong.vam.extractor.VarExtractor;
import com.xudong.vam.model.VamPackage;
import com.xudong.vam.model.domain.Image;
import com.xudong.vam.model.domain.Package;
import com.xudong.vam.progress.ProgressBar;
import com.xudong.vam.repository.VamPackageRepository;
import com.xudong.vam.service.VamPackageService;
import com.xudong.vam.storage.Storage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class VamPackageServiceImpl implements VamPackageService {
    private final Storage<Image> imageStorage;

    private final VarExtractor varExtractor;

    private final VamPackageBuilder vamPackageBuilder;

    private final VamProperties vamProperties;

    private final VamPackageRepository vamPackageRepository;

    @Override
    public void generate(Path path, ProgressBar<VamPackage> progressBar) {
        try {
            List<CompletableFuture<Package>> packages = varExtractor.extractAll(path);
            if (packages == null || packages.isEmpty()) {
                return;
            }

            int totalPack = packages.size();
            int currentPack = 0;

            for (CompletableFuture<Package> packFuture : packages) {
                Package pack = packFuture.get();

                String imagePath = imageStorage.store(vamProperties.getImagePath(), pack.getImage());
                VamPackage vamPackage = vamPackageBuilder.build(pack, imagePath);

                create(vamPackage);

                if (progressBar != null) {
                    progressBar.progress(totalPack, ++currentPack, vamPackage);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void create(VamPackage vamPackage) {
        if (vamPackage == null) {
            return;
        }

        VamPackage pack = vamPackageRepository.findByNameAndVersionAndCreatorName(vamPackage.getName(), vamPackage.getVersion(), vamPackage.getCreatorName());
        if (pack != null) {
            vamPackage.setId(pack.getId());
        }

        vamPackageRepository.save(vamPackage);
    }
}
