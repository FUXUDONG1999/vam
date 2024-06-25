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

import java.io.IOException;
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
    public void generate(Path path, ProgressBar progressBar) throws IOException {
        List<CompletableFuture<Package>> packages = varExtractor.extractAll(path);
        if (packages == null || packages.isEmpty()) {
            return;
        }

        int totalPack = packages.size();
        int currentPack = 0;

        for (CompletableFuture<Package> packFuture : packages) {
            Package pack = null;
            try {
                pack = packFuture.get();
                if (pack == null) {
                    continue;
                }

                String imagePath = imageStorage.store(vamProperties.getImagePath(), pack.getImage());
                VamPackage vamPackage = vamPackageBuilder.build(pack, imagePath);

                create(vamPackage);

                if (progressBar != null) {
                    progressBar.progress(totalPack, ++currentPack);
                }
            } catch (Exception e) {
                if (pack != null) {
                    log.error("Generate fail,path: {}", pack.getPath().toString(), e);
                }
            }

        }
    }

    private void create(VamPackage vamPackage) {
        if (vamPackage == null) {
            return;
        }

        VamPackage pack = vamPackageRepository.findByNameAndVersionAndCreatorName(vamPackage.getName(), vamPackage.getVersion(), vamPackage.getCreatorName());
        if (pack != null) {
            return;
        }

        vamPackageRepository.save(vamPackage);
    }
}
