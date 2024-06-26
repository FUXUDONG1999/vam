package com.xudong.vam.core.runner.enums

import lombok.AllArgsConstructor
import lombok.Getter

@Getter
@AllArgsConstructor
enum class GameMode(
    val code: String,

    val command: String,

    val description: String,
) {
    VR("Vr", "OpenVR", "VR启动"),
    PC("Pc", "None", "PC启动");
}
