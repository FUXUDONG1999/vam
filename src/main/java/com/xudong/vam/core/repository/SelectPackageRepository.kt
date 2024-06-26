package com.xudong.vam.core.repository

import com.xudong.vam.core.model.SelectPackage
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SelectPackageRepository : CrudRepository<SelectPackage, Long> {
    fun findByName(name: String): SelectPackage?
}
