package com.xudong.vam.core.storage.impl

import com.xudong.vam.core.model.domain.Image
import com.xudong.vam.core.storage.Storage
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path

@Component
class ImageStorage : Storage<Image> {
    override fun store(path: String?, obj: Image?): String? {
        if (path == null || obj == null) {
            return null
        }

        val imagePath = obj.path
        if (imagePath.isEmpty()) {
            return null
        }

        try {
            val name = imagePath.substring(imagePath.lastIndexOf("/") + 1)
            val content = obj.content

            val dest = Path.of(path, name)
            if (Files.exists(dest)) {
                return dest.toString()
            }
            Files.write(dest, content)

            return dest.toString()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
