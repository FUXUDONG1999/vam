package com.xudong.vam.core.extractor.impl

import com.xudong.vam.core.concurrent.ConcurrentExecutor
import com.xudong.vam.core.extractor.VarExtractor
import com.xudong.vam.core.model.domain.Image
import com.xudong.vam.core.model.domain.Metadata
import com.xudong.vam.core.model.domain.Package
import com.xudong.vam.core.utils.fromJson
import lombok.AllArgsConstructor
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipFile
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

private const val METADATA = "meta.json"

private val log = LoggerFactory.getLogger(VarExtractor::class.java)

@Component
@AllArgsConstructor
class VarExtractorImpl(
    private val concurrentExecutor: ConcurrentExecutor
) : VarExtractor {
    override fun extract(packagePath: Path): Package {
        try {
            ZipFile.builder().setPath(packagePath).get().use { zipFile ->
                val metadata = extractMetadata(zipFile)
                return Package(packagePath, metadata, extractImage(zipFile, metadata))
            }
        } catch (e: Exception) {
            log.error("Extract package error:{}, {}", packagePath, e.message, e)
            throw RuntimeException(e)
        }
    }

    override fun extractAll(packagePath: Path): List<CompletableFuture<Package>> {
        val paths = getPackagesPath(packagePath, ArrayList())

        return concurrentExecutor.executeAll(paths) { path ->
            this.extract(path)
        }
    }

    private fun extractZip(packagePath: Path): Path? {
        try {
            ZipFile.builder().setPath(packagePath).get().use { zipFile ->
                val pathString = packagePath.toString()
                val dest = Path.of(pathString.substring(0, pathString.lastIndexOf(".")))
                if (!Files.exists(dest)) {
                    Files.createDirectories(dest)
                }

                val iterator = zipFile.entries.asIterator()
                while (iterator.hasNext()) {
                    unzipFile(zipFile, iterator.next(), dest.toString())
                }

                return dest
            }
        } catch (e: Exception) {
            log.error("Extract zip error:{}, {}", packagePath, e.message, e)
            return null
        }
    }

    private fun getPackagesPath(packagePath: Path, packages: MutableList<Path>): List<Path> {
        if (Files.isDirectory(packagePath)) {
            val fileNames = packagePath.toFile().list() ?: return packages

            for (fileName in fileNames) {
                val path = Path.of(packagePath.toString(), fileName)
                getPackagesPath(path, packages)
            }

            return packages
        }

        val fileName = packagePath.fileName.toString()
        if (fileName.endsWith(".zip")) {
            val path = extractZip(packagePath)
            if (path != null) {
                getPackagesPath(path, packages)
            }
        }

        if (!fileName.endsWith(".var")) {
            return packages
        }

        packages.add(packagePath)
        return packages
    }

    private fun extractMetadata(zipFile: ZipFile): Metadata? {
        val bytes = extractContent(zipFile, METADATA) ?: return null

        val metadataJson = String(bytes, StandardCharsets.UTF_8).replace("\uFEFF", "")

        return fromJson(metadataJson, Metadata::class.java)
    }

    private fun extractImage(zipFile: ZipFile, metadata: Metadata?): Image? {
        if (metadata == null) {
            return null
        }

        val contentList = metadata.contentList
        if (contentList.isNullOrEmpty()) {
            return null
        }

        val imagePaths = contentList.stream()
            .filter { name: String -> name.endsWith(".jpg") }
            .toList()
        if (imagePaths.isEmpty()) {
            return null
        }

        for (imagePath in imagePaths) {
            val content = extractContent(zipFile, imagePath) ?: continue

            return Image(imagePath, content)
        }

        return null
    }

    private fun extractContent(zipFile: ZipFile, path: String): ByteArray? {
        val entry = zipFile.getEntry(path) ?: return null

        val inputStream = zipFile.getInputStream(entry)
        return inputStream.readAllBytes()
    }

    private fun unzipFile(zipFile: ZipFile, entry: ZipArchiveEntry, dest: String) {
        val entryPath = File(dest + File.separator + entry.name)
        if (entry.isDirectory) {
            if (!entryPath.exists()) {
                entryPath.mkdir()
            }

            return
        }

        val inputStream = zipFile.getInputStream(entry)
        if (entryPath.exists()) {
            return
        }

        Files.write(entryPath.toPath(), inputStream.readAllBytes())
    }
}
