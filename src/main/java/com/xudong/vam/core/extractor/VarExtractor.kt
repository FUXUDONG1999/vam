package com.xudong.vam.core.extractor

import com.xudong.vam.core.model.domain.Package
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

interface VarExtractor {
    fun extract(packagePath: Path): Package

    fun extractAll(packagePath: Path): List<CompletableFuture<Package>>
}
