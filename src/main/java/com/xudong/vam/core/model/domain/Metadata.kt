package com.xudong.vam.core.model.domain

data class Metadata(
    var licenseType: String? = null,

    var creatorName: String? = null,

    var packageName: String? = null,

    var description: String? = null,

    var credits: String? = null,

    var instructions: String? = null,

    var promotonalLink: String? = null,

    var programVersion: String? = null,

    var standardReferenceVersionOption: String? = null,

    var contentList: List<String>? = null,

    var dependencies: Map<String, Metadata>? = null,
)
