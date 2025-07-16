package ru.vichukano.spicerate.gui

import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.slf4j.LoggerFactory

class App : Application() {

    override fun start(stage: Stage) {
        log.info("Start application")
        with(stage) {
            scene = Scene(
                BorderPane().apply {
                    top = Label("Spice Rate Calculator").apply {
                        alignment = Pos.CENTER
                        textFill = javafx.scene.paint.Color.BLUE
                        BorderPane.setAlignment(this, Pos.CENTER)
                    }
                    left = VBox().apply {
                        spacing = 20.0
                        alignment = Pos.CENTER
                        children.addAll(
                            Button("One").apply {
                                setOnAction {
                                    log.info("One button clicked")
                                }
                            },
                            Button("Two").apply {
                                setOnAction {
                                    log.info("Two button clicked")
                                }
                            },
                            Button("Three").apply {
                                setOnAction {
                                    log.info("Three button clicked")
                                }
                            },
                        )
                    }
                    center = Button("Exit").apply {
                        setOnAction {
                            log.info("Exit button clicked")
                            Platform.exit()
                        }
                    }
                    bottom = Label("Copyright © 2025 Vichukano").apply {
                        alignment = Pos.CENTER
                        BorderPane.setAlignment(this, Pos.CENTER)
                    }
                },
                600.0,
                400.0
            )
            setOnCloseRequest {
                Platform.exit()
            }
            show()
        }
    }

    private companion object {
        private val log = LoggerFactory.getLogger(App::class.java)
    }

}
