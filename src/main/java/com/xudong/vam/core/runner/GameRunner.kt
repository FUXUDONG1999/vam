package com.xudong.vam.core.runner

import com.xudong.vam.core.runner.enums.GameMode

interface GameRunner {
    fun run(gameMode: GameMode)
}
