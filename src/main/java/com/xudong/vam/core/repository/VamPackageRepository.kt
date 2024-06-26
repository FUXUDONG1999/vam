package com.xudong.vam.core.repository

import com.xudong.vam.core.model.VamPackage
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VamPackageRepository : CrudRepository<VamPackage, Long> {
    fun findByNameAndVersionAndCreatorName(name: String, version: String, creatorName: String): VamPackage?

    fun findAllByCreatorNameInAndNameIn(creators: Set<String>, names: Set<String>): List<VamPackage>
}
