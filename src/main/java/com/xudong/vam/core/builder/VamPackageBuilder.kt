package com.xudong.vam.core.builder

import com.xudong.vam.core.model.VamPackage
import com.xudong.vam.core.model.domain.Package

interface VamPackageBuilder {
    fun build(pack: Package, imagePath: String?): VamPackage
}
