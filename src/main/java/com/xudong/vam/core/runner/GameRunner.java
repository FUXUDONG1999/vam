package com.xudong.vam.core.runner;

import com.xudong.vam.core.runner.enums.GameMode;

import java.io.IOException;

public interface GameRunner {
    void run(GameMode gameMode) throws IOException;
}
