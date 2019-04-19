package view

import javafx.scene.paint.Color
import tornadofx.*
import java.util.regex.Pattern
import javafx.geometry.Pos
import javafx.scene.control.*
import java.io.File
import kotlin.math.max
import kotlin.math.min


class LeftView : View() {
    private var forestWidth: TextField by singleAssign()
    private var forestHeight: TextField by singleAssign()
    private var foodProb: Slider by singleAssign()
    private var infoLabel: Label by singleAssign()
    private var animalMin: TextField by singleAssign()
    private var animalMax: TextField by singleAssign()
    private var create: Button by singleAssign()
    private var start: ToggleButton by singleAssign()
    override val root = vbox {
        form {
            minHeight = 400.0
            fieldset("Лес") {
                field("Ширина") {
                    forestWidth = textfield("5")
                    val p = Pattern.compile("""|[1-9]|[1][0-5]""")
                    forestWidth.textProperty().addListener { _, oldValue, newValue -> if (!p.matcher(newValue).matches()) forestWidth.text = oldValue }
                    forestWidth.focusedProperty().addListener { _, _, isNowFocused ->
                        if (!isNowFocused)
                            if (forestWidth.text == "") forestWidth.text = "1"
                    }

                }
                field("Высота") {
                    forestHeight = textfield("5")
                    val p = Pattern.compile("""|[1-9]|10""")
                    forestHeight.textProperty().addListener { _, oldValue, newValue -> if (!p.matcher(newValue).matches()) forestHeight.text = oldValue }
                    forestHeight.focusedProperty().addListener { _, _, isNowFocused ->
                        if (!isNowFocused)
                            if (forestHeight.text == "") forestHeight.text = "1"
                    }
                }
            }
            fieldset("Система") {
                field("Число животных") {
                    hbox {
                        animalMin = textfield("10")
                        val p = Pattern.compile("""|[1-9][\d]|[1-9]""")
                        animalMin.textProperty().addListener { _, oldValue, newValue -> if (!p.matcher(newValue).matches()) animalMin.text = oldValue }
                        animalMin.focusedProperty().addListener { _, _, isNowFocused ->
                            if (!isNowFocused)
                                if (animalMin.text == "") animalMin.text = "1"
                                    animalMin.text = min(animalMin.text.toInt(), animalMax.text.toInt()).toString()
                        }
                        label("-") {
                            minWidth = 10.0
                            alignment = Pos.CENTER
                        }
                        animalMax = textfield("20")
                        animalMax.textProperty().addListener { _, oldValue, newValue -> if (!p.matcher(newValue).matches()) animalMax.text = oldValue }
                        animalMax.focusedProperty().addListener { _, _, isNowFocused ->
                            if (!isNowFocused){
                                if (animalMax.text == "") animalMax.text = "99"
                                animalMax.text = max(animalMin.text.toInt(), animalMax.text.toInt()).toString()
                            }
                        }
                    }
                }
                field {
                    vbox {
                        label("Вероятность еды")
                        foodProb = slider(0, 100, 10)
                        foodProb.isShowTickLabels = true
                        foodProb.isShowTickMarks = true
                        foodProb.minorTickCount = 1
                        foodProb.valueProperty().addListener { _, _, newValue ->
                            infoLabel.text = """Вероятность: ${newValue.toInt() / 100.0}%"""
                        }

                        infoLabel = label("Вероятность: ${foodProb.value.toInt()}%")
                        infoLabel.textFill = Color.BLUE
                    }
                }
                field {
                    create = button("Создать лес").apply {
                        minWidth = 100.0
                        textFill = Color.BLACK
                        action {
                            start.isSelected = true
                            fire(StartRequest(false))
                            fire(CreateForestRequest(forestWidth.text.toInt(), forestHeight.text.toInt(), animalMin.text.toInt()..animalMax.text.toInt(),foodProb.value.toInt()/100f))
                        }
                    }
                }
                field {
                    start = togglebutton {
                        isVisible = false
                        alignment = Pos.CENTER
                        minWidth = 100.0
                        val stateText = selectedProperty().stringBinding {
                            textFill = Color.BLACK
                            if (it == true) "Старт" else "Пауза"
                        }
                        textProperty().bind(stateText)
                        action {
                            fire(StartRequest(!isSelected))
                        }
                        subscribe<CreateForestRequest> {
                            isVisible = true
                        }
                    }
                }
            }
            subscribe<DeadForest> {
                start.isVisible = false
                val alert = Alert(Alert.AlertType.INFORMATION)
                alert.title = "Лес мертв"
                alert.headerText = null
                alert.contentText =  "Все животные мертвы"
                alert.showAndWait()
            }
        }
        imageview(File("src\\main\\img\\squirrel.png").toURI().toString()){
            fitWidth = 200.0
            fitHeight = 250.0
        }
    }

    init {
        with(root) {
            style { borderColor += box(c("#a1a1a1")) }
            prefWidth = 200.0
        }
    }
}