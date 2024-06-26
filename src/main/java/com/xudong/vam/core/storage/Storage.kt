package com.xudong.vam.core.storage

interface Storage<T> {
    fun store(path: String?, obj: T?): String?
}
