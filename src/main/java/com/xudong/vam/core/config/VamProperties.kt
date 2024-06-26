package com.xudong.vam.core.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("vam")
data class VamProperties(
    var imagePath: String = "E:\\GAME\\vam1.22.0.3\\images",

    var gamePath: String = "E:\\GAME\\vam1.22.0.3",

    var modPath: String = "E:\\GAME\\vam1.22.0.3\\AddonPackages",
)
