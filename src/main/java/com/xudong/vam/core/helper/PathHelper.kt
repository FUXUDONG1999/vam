package com.xudong.vam.core.helper

import com.xudong.vam.core.config.VamProperties
import lombok.AllArgsConstructor
import org.springframework.stereotype.Component
import java.nio.file.Path

@Component
@AllArgsConstructor
class PathHelper(
    private val vamProperties: VamProperties
) {
    fun getSelectPath(uuid: String): Path {
        return Path.of(vamProperties.gamePath, "vam-packages-link", uuid)
    }
}
