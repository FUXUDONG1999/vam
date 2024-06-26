package com.xudong.vam.core.selector

interface PackageSelector {
    fun select(selectId: Long, rootId: Long): List<Long>?

    fun unselect(selectDetailId: Long)

    fun clear()
}
