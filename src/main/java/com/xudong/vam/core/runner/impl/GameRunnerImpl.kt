package com.xudong.vam.core.runner.impl

import com.xudong.vam.core.config.VamProperties
import com.xudong.vam.core.runner.GameRunner
import com.xudong.vam.core.runner.enums.GameMode
import lombok.AllArgsConstructor
import org.springframework.stereotype.Component
import java.nio.file.Path

@Component
@AllArgsConstructor
class GameRunnerImpl(
    private val vamProperties: VamProperties
) : GameRunner {
    override fun run(gameMode: GameMode) {
        val gamePath = vamProperties.gamePath
        val exec = Path.of(gamePath, "VaM.exe").toString()

        ProcessBuilder(exec, "-vrmode", gameMode.command).start()
    }
}
