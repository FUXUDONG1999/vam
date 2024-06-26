package com.xudong.vam

import javafx.application.Application
import javafx.application.Application.launch
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import lombok.extern.slf4j.Slf4j
import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ApplicationContext

@Slf4j
@SpringBootApplication
open class VamApplication : Application() {
    override fun start(stage: Stage) {
        val scene = Scene(loadFXML(), 320.0, 240.0)
        stage.title = "Vam"
        stage.scene = scene
        stage.show()
    }

    private fun loadFXML(): Parent {
        return FXMLLoader(javaClass.getResource("vam-application.fxml")).load()
    }
}

fun main(args: Array<String>) {
    val context: ApplicationContext = SpringApplicationBuilder(VamApplication::class.java)
        .bannerMode(Banner.Mode.OFF)
        .web(WebApplicationType.NONE)
        .run(*args)

    launch(VamApplication::class.java, *args)
}