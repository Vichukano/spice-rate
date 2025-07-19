package ru.vichukano.spicerate.gui

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import org.slf4j.LoggerFactory

class App : Application() {

    override fun start(stage: Stage) {
        val root = FXMLLoader.load<Parent>(App::class.java.classLoader.getResource("spice-rate-main.fxml"))
        val scene = Scene(root)
        stage.title = "Spice Rate"
        stage.scene = scene
        stage.show()
        log.info("Application has been started")
    }

    private companion object {
        private val log = LoggerFactory.getLogger(App::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            launch(App::class.java, *args)
        }
    }

}
