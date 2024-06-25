package com.xudong.vam.runner;

import com.xudong.vam.runner.enums.GameMode;

import java.io.IOException;

public interface GameRunner {
    void run(GameMode gameMode) throws IOException;
}
