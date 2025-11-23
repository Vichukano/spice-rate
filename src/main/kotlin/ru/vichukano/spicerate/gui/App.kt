package ru.vichukano.spicerate.gui

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import org.slf4j.LoggerFactory
import ru.vichukano.spicerate.core.calculations.CalculateProfit
import ru.vichukano.spicerate.core.calculations.DailyCapitalizationDepositCalculator
import ru.vichukano.spicerate.core.calculations.MonthlyCapitalizationDepositCalculator
import ru.vichukano.spicerate.core.calculations.SimpleDepositCalculator
import ru.vichukano.spicerate.core.calculations.YearlyCapitalizationDepositCalculator

class App : Application() {

    override fun start(stage: Stage) {
        val calculateProfit = CalculateProfit(
            simple = SimpleDepositCalculator(),
            daily = DailyCapitalizationDepositCalculator(),
            monthly = MonthlyCapitalizationDepositCalculator(),
            yearly = YearlyCapitalizationDepositCalculator()
        )
        val view = MainView(calculateProfit)
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