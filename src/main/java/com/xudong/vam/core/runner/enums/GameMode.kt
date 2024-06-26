package com.xudong.vam.core.runner.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameMode {
    VR("Vr", "OpenVR", "VR启动"),
    PC("Pc", "None", "PC启动");

    private final String code;

    private final String command;

    private final String description;
}
