package com.xudong.vam.core.selector.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.xudong.vam.core.config.VamProperties
import com.xudong.vam.core.helper.PathHelper
import com.xudong.vam.core.model.SelectDetail
import com.xudong.vam.core.model.VamPackage
import com.xudong.vam.core.model.domain.Metadata
import com.xudong.vam.core.repository.SelectDetailRepository
import com.xudong.vam.core.repository.SelectPackageRepository
import com.xudong.vam.core.repository.VamPackageRepository
import com.xudong.vam.core.selector.PackageSelector
import com.xudong.vam.core.utils.fromJson
import lombok.AllArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.BiConsumer
import java.util.function.Consumer

@Slf4j
@Component
@AllArgsConstructor
class PackageSelectorImpl(
    private val vamPackageRepository: VamPackageRepository,

    private val selectPackageRepository: SelectPackageRepository,

    private val selectDetailRepository: SelectDetailRepository,

    private val vamProperties: VamProperties,

    private val pathHelper: PathHelper,
) : PackageSelector {

    override fun select(selectId: Long, rootId: Long): List<Long>? {
        val selectPackageOptional = selectPackageRepository.findById(selectId)
        if (selectPackageOptional.isEmpty) {
            return null
        }

        val selectPackage = selectPackageOptional.get()
        val uuid = selectPackage.uuid

        val vamPackageOptional = vamPackageRepository.findById(rootId)
        if (vamPackageOptional.isEmpty) {
            return null
        }

        val packages: MutableMap<Long, VamPackage> = LinkedHashMap()
        selectPackages(vamPackageOptional.get(), packages)

        for (vamPackage in packages.values) {
            saveDetail(SelectDetail(null, selectId, rootId, vamPackage.id!!))
            linkPackage(vamPackage, uuid)
        }

        symbolicLink(pathHelper.getSelectPath(uuid), Path.of(vamProperties.modPath))

        return packages.keys
            .stream()
            .toList()
    }

    override fun unselect(selectDetailId: Long) {
        val detailOptional = selectDetailRepository.findById(selectDetailId)
        if (detailOptional.isEmpty) {
            return
        }

        val detail = detailOptional.get()
        val selectPackageOptional = selectPackageRepository.findById(detail.selectId)
        if (selectPackageOptional.isEmpty) {
            return
        }
        val selectPackage = selectPackageOptional.get()

        val vamPackageOptional = vamPackageRepository.findById(detail.childId)
        if (vamPackageOptional.isEmpty) {
            return
        }

        val vamPackage = vamPackageOptional.get()
        unlinkPackage(vamPackage, selectPackage.uuid)

        selectDetailRepository.deleteById(detail.id!!)
    }

    override fun clear() {
        val modPath = vamProperties.modPath
        val file = Path.of(modPath).toFile()
        val list = file.list() ?: return

        for (item in list) {
            Files.delete(Path.of(modPath, item))
        }
    }

    private fun selectPackages(rootPackage: VamPackage, vamPackages: MutableMap<Long, VamPackage>) {
        if (vamPackages.containsKey(rootPackage.id)) {
            return
        }

        vamPackages[rootPackage.id!!] = rootPackage
        val dependenciesJson = rootPackage.dependencies ?: return

        selectDependencies(
            fromJson(dependenciesJson, object : TypeReference<Map<String, Metadata>>() {
            }),
            vamPackages
        )
    }

    private fun linkPackage(rootPackage: VamPackage?, uuid: String) {
        if (rootPackage == null) {
            return
        }

        val path = pathHelper.getSelectPath(uuid)
        if (!Files.exists(path)) {
            Files.createDirectories(path)
        }

        symbolicLink(Path.of(rootPackage.path), path)
    }

    private fun unlinkPackage(vamPackage: VamPackage, uuid: String) {
        var path = pathHelper.getSelectPath(uuid)
        if (!Files.exists(path)) {
            return
        }

        path = Path.of(path.toString(), vamPackage.fileName)
        if (!Files.exists(path)) {
            return
        }

        Files.delete(path)
    }

    private fun selectDependencies(dependencies: Map<String, Metadata>?, vamPackages: MutableMap<Long, VamPackage>) {
        if (dependencies.isNullOrEmpty()) {
            return
        }

        val creators: MutableSet<String> = HashSet()
        val names: MutableSet<String> = HashSet()

        dependencies.forEach(BiConsumer<String, Metadata> { key: String, value: Metadata ->
            val strings = key.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val creatorName = strings[0]
            val name = strings[1]

            creators.add(creatorName)
            names.add(name)
            selectDependencies(value.dependencies, vamPackages)
        })

        vamPackageRepository.findAllByCreatorNameInAndNameIn(creators, names)
            .forEach(Consumer { vamPackage: VamPackage -> selectPackages(vamPackage, vamPackages) })
    }

    private fun symbolicLink(source: Path, dest: Path) {
        val destination = Path.of(dest.toString(), source.fileName.toString())
        if (Files.exists(destination)) {
            return
        }

        Files.createSymbolicLink(destination, source)
    }

    private fun saveDetail(selectDetail: SelectDetail) {
        val detail = selectDetailRepository.findBySelectIdAndChildId(selectDetail.selectId, selectDetail.childId)
        if (detail != null) {
            return
        }

        selectDetailRepository.save(selectDetail)
    }
}
