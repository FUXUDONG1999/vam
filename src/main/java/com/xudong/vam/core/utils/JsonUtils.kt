package com.xudong.vam.core.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

private val MAPPER: ObjectMapper = ObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

fun toJson(obj: Any): String {
    try {
        return MAPPER.writeValueAsString(obj)
    } catch (e: JsonProcessingException) {
        throw RuntimeException(e)
    }
}

fun <T> fromJson(json: String, clazz: Class<T>): T {
    try {
        return MAPPER.readValue(json, clazz)
    } catch (e: JsonProcessingException) {
        throw RuntimeException(e)
    }
}

fun <T> fromJson(json: String, typeReference: TypeReference<T>): T {
    try {
        return MAPPER.readValue(json, typeReference)
    } catch (e: JsonProcessingException) {
        throw RuntimeException(e)
    }
}