package com.xudong.vam.core.model.domain

import java.nio.file.Path

data class Package(
    var path: Path,

    var metadata: Metadata?,

    var image: Image?,
)
