package com.xudong.vam.runner.impl;

import com.xudong.vam.config.VamProperties;
import com.xudong.vam.runner.GameRunner;
import com.xudong.vam.runner.enums.GameMode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Component
@AllArgsConstructor
public class GameRunnerImpl implements GameRunner {
    private final VamProperties vamProperties;

    @Override
    public void run(GameMode gameMode) throws IOException {
        String gamePath = vamProperties.getGamePath();
        String exec = Path.of(gamePath, "VaM.exe").toString();

        new ProcessBuilder(exec, "-vrmode", gameMode.getCommand())
                .start();
    }
}
