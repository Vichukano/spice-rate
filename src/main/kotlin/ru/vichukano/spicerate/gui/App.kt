package ru.vichukano.spicerate.gui

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import ru.vichukano.spicerate.core.calculations.CalculateProfit
import ru.vichukano.spicerate.core.calculations.ReplenishDeposit
import ru.vichukano.spicerate.core.storage.ExposedCalculationRepository
import ru.vichukano.spicerate.gui.controller.DepositCalculationsController
import java.util.Properties
import java.io.FileInputStream

class App : Application() {

    override fun start(stage: Stage) {
        val properties = Properties()
        val configPath = parameters.named["config"]
        if (configPath != null) {
            properties.load(FileInputStream(configPath))
        } else {
            properties.load(App::class.java.classLoader.getResourceAsStream("config.properties"))
        }

        val dbPath = properties.getProperty("db.path")
        val db = Database.connect("jdbc:sqlite:$dbPath", driver = "org.sqlite.JDBC")
        val repository = ExposedCalculationRepository(db)
        val calculateProfit = CalculateProfit()
        val replenishDeposit = ReplenishDeposit(repository)
        val calculationsController = DepositCalculationsController(calculateProfit, repository, replenishDeposit)
        val view = MainView(calculationsController)
        val scene = Scene(view)
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