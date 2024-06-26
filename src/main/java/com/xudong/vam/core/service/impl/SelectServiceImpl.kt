package com.xudong.vam.core.service.impl

import com.xudong.vam.core.model.SelectPackage
import com.xudong.vam.core.repository.SelectPackageRepository
import com.xudong.vam.core.service.SelectService
import lombok.AllArgsConstructor
import org.springframework.stereotype.Service
import java.util.*

@Service
@AllArgsConstructor
class SelectServiceImpl : SelectService {
    private val selectPackageRepository: SelectPackageRepository? = null

    override fun createSelect(name: String): Long {
        var selectPackage = selectPackageRepository!!.findByName(name)
        if (selectPackage != null) {
            return selectPackage.id!!
        }

        selectPackage = SelectPackage(name = name, uuid = UUID.randomUUID().toString())
        selectPackage = selectPackageRepository.save(selectPackage)

        return selectPackage.id!!
    }
}
