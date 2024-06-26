package com.xudong.vam.core.service.impl

import com.xudong.vam.core.builder.VamPackageBuilder
import com.xudong.vam.core.config.VamProperties
import com.xudong.vam.core.extractor.VarExtractor
import com.xudong.vam.core.model.VamPackage
import com.xudong.vam.core.model.domain.Image
import com.xudong.vam.core.model.domain.Package
import com.xudong.vam.core.repository.VamPackageRepository
import com.xudong.vam.core.service.VamPackageService
import com.xudong.vam.core.storage.Storage
import lombok.AllArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

private val log = LoggerFactory.getLogger(VamPackageService::class.java)

@Slf4j
@Service
@AllArgsConstructor
class VamPackageServiceImpl(
    private val imageStorage: Storage<Image>,

    private val varExtractor: VarExtractor,

    private val vamPackageBuilder: VamPackageBuilder,

    private val vamProperties: VamProperties,

    private val vamPackageRepository: VamPackageRepository
) : VamPackageService {
    override fun generate(path: Path, progressBar: ((total: Int, curret: Int) -> Unit)?) {
        val packages: List<CompletableFuture<Package>> = varExtractor.extractAll(path)
        if (packages.isEmpty()) {
            return
        }

        val totalPack = packages.size
        var currentPack = 0

        for (packFuture in packages) {
            var pack: Package? = null
            try {
                pack = packFuture.get()
                if (pack == null) {
                    continue
                }

                val imagePath = imageStorage.store(vamProperties.imagePath, pack.image)
                val vamPackage: VamPackage = vamPackageBuilder.build(pack, imagePath)

                create(vamPackage)

                if (progressBar != null) {
                    progressBar(totalPack, ++currentPack)
                }
            } catch (e: Exception) {
                if (pack != null) {
                    log.error("Generate fail,path: {}", pack.path.toString(), e)
                }
            }
        }
    }

    private fun create(vamPackage: VamPackage?) {
        if (vamPackage == null) {
            return
        }

        val pack = vamPackageRepository.findByNameAndVersionAndCreatorName(vamPackage.name, vamPackage.version, vamPackage.creatorName)
        if (pack != null) {
            return
        }

        vamPackageRepository.save(vamPackage)
    }
}
