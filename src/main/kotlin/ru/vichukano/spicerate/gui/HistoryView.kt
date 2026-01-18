package ru.vichukano.spicerate.gui

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage
import javafx.stage.Modality
import ru.vichukano.spicerate.core.model.DepositDetails
import ru.vichukano.spicerate.gui.controller.DepositCalculationsController
import java.util.UUID

class HistoryView(
    private val controller: DepositCalculationsController,
    private var calculations: List<DepositDetails>,
    private val onClose: () -> Unit,
    private val onDelete: (HistoryItem) -> Unit
) : AnchorPane() {
    private val table = TableView<HistoryItem>()
    private var stage: Stage? = null
    private val openDetailsViews = mutableMapOf<String, DetailsView>()
    val isShowing: Boolean
        get() = stage?.isShowing == true

    init {
        children.add(table)
        setTopAnchor(table, 0.0)
        setBottomAnchor(table, 0.0)
        setLeftAnchor(table, 0.0)
        setRightAnchor(table, 0.0)
        initTable()
        populateTable()
        table.setOnMouseClicked { event ->
            if (event.clickCount == 2) {
                table.selectionModel.selectedItem?.let { selectedItem ->
                    val existingView = openDetailsViews[selectedItem.id]
                    if (existingView != null && existingView.isShowing) {
                        existingView.requestFocus()
                    } else {
                        controller.getDetailsById(selectedItem.id)?.let { details ->
                            val detailsView = DetailsView(details)
                            openDetailsViews[selectedItem.id] = detailsView
                            detailsView.show {
                                openDetailsViews.remove(selectedItem.id)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initTable() {
        table.columns.addAll(
            createTableColumn("id", "id"),
            createTableColumn("Дата открытия", "openDate"),
            createTableColumn("Ставка", "rate"),
            createTableColumn("Эффективная ставка", "effectiveRate"),
            createTableColumn("Вид капитализации", "capitalization"),
            createTableColumn("Сумма открытия", "amount"),
            createTableColumn("Сумма в конце", "finalAmount")
        )
        addDeleteButtonColumn()
        addReplenishButtonColumn()
    }

    private fun addReplenishButtonColumn() {
        val replenishColumn = TableColumn<HistoryItem, Void>("").apply {
            style = "-fx-alignment: CENTER;"
        }

        replenishColumn.setCellFactory {
            object : TableCell<HistoryItem, Void>() {
                private val btn = Button("Пополнение")

                init {
                    btn.setOnAction {
                        val item = tableView.items[index]
                        val view = ReplenishmentView(
                            UUID.fromString(item.id),
                            controller,
                            onReplenished = {
                                val allCalculations = controller.getAllCalculations()
                                update(allCalculations)
                                val updatedDetails = allCalculations.find { it.id.toString() == item.id }
                                if (updatedDetails != null) {
                                    openDetailsViews[item.id]?.updateView(updatedDetails)
                                }
                            }
                        )
                        val stage = Stage()
                        stage.initOwner(btn.scene.window)
                        stage.initModality(Modality.APPLICATION_MODAL)
                        stage.scene = Scene(view)
                        stage.title = "Пополнение депозита"
                        stage.show()
                    }
                }

                override fun updateItem(item: Void?, empty: Boolean) {
                    super.updateItem(item, empty)
                    graphic = if (empty) {
                        null
                    } else {
                        btn
                    }
                }
            }
        }
        table.columns.add(replenishColumn)
    }

    private fun addDeleteButtonColumn() {
        val deleteColumn = TableColumn<HistoryItem, Void>("").apply {
            style = "-fx-alignment: CENTER;"
        }

        deleteColumn.setCellFactory {
            object : TableCell<HistoryItem, Void>() {
                private val btn = Button("Удалить")

                init {
                    btn.setOnAction {
                        val item = tableView.items[index]
                        onDelete(item)
                    }
                }

                override fun updateItem(item: Void?, empty: Boolean) {
                    super.updateItem(item, empty)
                    graphic = if (empty) {
                        null
                    } else {
                        btn
                    }
                }
            }
        }
        table.columns.add(deleteColumn)
    }

    private fun populateTable() {
        table.items.clear()
        calculations.forEach {
            table.items.add(
                HistoryItem(
                    id = it.id.toString(),
                    openDate = it.startDate.toString(),
                    rate = it.baseRate.toString(),
                    effectiveRate = it.effectiveRate,
                    capitalization = it.capitalization.value,
                    amount = it.startSum,
                    finalAmount = it.endSum
                )
            )
        }
    }

    fun update(newCalculations: List<DepositDetails>) {
        this.calculations = newCalculations
        populateTable()
    }

    private fun createTableColumn(name: String, propertyName: String): TableColumn<HistoryItem, String> {
        return TableColumn<HistoryItem, String>(name).apply {
            cellValueFactory = PropertyValueFactory(propertyName)
        }
    }

    fun show() {
        stage = Stage()
        stage?.let {
            it.title = "История расчетов"
            it.scene = Scene(this, 1200.0, 800.0)
            it.setOnCloseRequest { onClose() }
            it.show()
        }
    }

    override fun requestFocus() {
        stage?.requestFocus()
    }
}
