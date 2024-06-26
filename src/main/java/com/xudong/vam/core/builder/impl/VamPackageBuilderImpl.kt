package com.xudong.vam.core.builder.impl

import com.xudong.vam.core.builder.VamPackageBuilder
import com.xudong.vam.core.model.VamPackage
import com.xudong.vam.core.model.domain.Package
import com.xudong.vam.core.utils.toJson
import org.springframework.stereotype.Component

@Component
class VamPackageBuilderImpl : VamPackageBuilder {
    override fun build(pack: Package, imagePath: String?): VamPackage {
        val vamPackage = VamPackage()
        val metadata = pack.metadata
        if (metadata != null) {
            vamPackage.description = metadata.description
            vamPackage.dependencies = toJson(metadata.dependencies!!)
        }

        var fileName = pack.path
            .fileName
            .toString()
        fileName = fileName.substring(0, fileName.lastIndexOf("."))

        val strings = fileName.split("\\.")
        val creatorName = strings[0]
        val name = strings[1]
        val version = strings[2]

        vamPackage.name = name
        vamPackage.version = version
        vamPackage.creatorName = creatorName
        vamPackage.path = pack.path.toString()
        vamPackage.imagePath = imagePath

        return vamPackage
    }
}
