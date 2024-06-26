package com.xudong.vam.core.repository

import com.xudong.vam.core.model.SelectDetail
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SelectDetailRepository : CrudRepository<SelectDetail, Long> {
    fun findBySelectIdAndChildId(selectId: Long, childId: Long): SelectDetail?
}
