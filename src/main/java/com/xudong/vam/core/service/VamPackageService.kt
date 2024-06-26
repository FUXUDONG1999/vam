package com.xudong.vam.core.service

import java.nio.file.Path

interface VamPackageService {
    fun generate(path: Path, progressBar: ((total: Int, curret: Int) -> Unit)? = null)
}
