package Forest

import Forest.Enums.EFood
import Forest.Enums.ETree
import Forest.Enums.ETreePart
import Forest.Enums.EAnimal
import javafx.scene.control.Alert
import javafx.scene.image.ImageView
import tornadofx.*
import view.CenterView
import view.Timer
import java.io.File
import java.util.concurrent.ThreadLocalRandom

class FieldCell(val x: Double, val y: Double, val width: Double, val height: Double) {
    private val canvas = find(CenterView::class).root
    val type: ETree = ETree.values()[ThreadLocalRandom.current().nextInt(ETree.values().size)]

    var food: MutableMap<EFood, Int> = mutableMapOf()
        set(newFoodUnits) = field.putAll(newFoodUnits.toMap())

    val hasFood = food.any { it.value > 0 }
    val border = 1.0
    val place = animalPlaces()

    init {
        println(type.prettyName)
        val img = ImageView(File("src\\main\\img\\${type.prettyName}.png").toURI().toString())
        img.relocate(x + border, y + border)
        img.fitHeight = height - 2 * border
        img.fitWidth = width - 2 * border
        canvas.add(img)

        val possibleFood: List<EFood> = when (type) {
            ETree.FIR, ETree.PINE, ETree.WALNUT -> listOf(EFood.NUTS, EFood.CONES)
            ETree.MAPLE -> listOf(EFood.MAPLE_LEAVES)
            else -> listOf()
        } + listOf(EFood.WORMS, EFood.ROOT_VEGETABLES)

        possibleFood.map { food.put(it, 0) }

        img.setOnMouseClicked {
            if (!Timer.work) {
                val alert = Alert(Alert.AlertType.INFORMATION)
                alert.title = "Сведения о дереве"
                alert.headerText = null
                alert.contentText = this.createReport()
                alert.showAndWait()
            }
        }
    }

    //координаты для животных
    fun animalPlaces(): Map<ETreePart, Map<EAnimal, Pair<Double, Double>>> {
        val animalSize = 25.0
        var stepX = (width -  border - animalSize) / 5
        var stepY = (height - border - animalSize) / 5
        var begX = x + border
        var begY = y + height - border - animalSize
        val groundPlace: Map<EAnimal, Pair<Double, Double>> = mapOf(EAnimal.SQUIRREL to Pair(begX, begY), EAnimal.FLYING_SQUIRREL to Pair(begX + stepX, begY),
                EAnimal.CHIPMUNK to Pair(begX + 2 * stepX, begY), EAnimal.WOODPECKER to Pair(begX + 3 * stepX, begY), EAnimal.BADGER to Pair(begX + 4 * stepX, begY),
                EAnimal.WOLF to Pair(begX + 5 * stepX, begY)
        )
        begX = x + (width - animalSize) / 2
        begY = y + border
        val trunkPlace: Map<EAnimal, Pair<Double, Double>> = mapOf(EAnimal.SQUIRREL to Pair(begX, begY), EAnimal.FLYING_SQUIRREL to Pair(begX, begY + stepY),
                EAnimal.CHIPMUNK to Pair(begX, begY + 2 * stepY), EAnimal.WOODPECKER to Pair(begX, begY + 3 * stepY), EAnimal.BADGER to Pair(begX, begY + 4 * stepY)
        )
        begY = y + height / 6
        stepX = width / 6
        stepY = height / 8
        val crownPlace: Map<EAnimal, Pair<Double, Double>> = mapOf(EAnimal.SQUIRREL to Pair(begX + stepX, begY), EAnimal.CHIPMUNK to Pair(begX - stepX, begY),
                EAnimal.FLYING_SQUIRREL to Pair(begX + 2 * stepX, begY + stepY), EAnimal.WOODPECKER to Pair(begX - 2 * stepX, begY + stepY),
                EAnimal.BADGER to Pair(begX + stepX, begY + 3 * stepY), EAnimal.VULTURE to Pair(begX - stepX, begY + 3 * stepY)
        )
        return mapOf(ETreePart.CROWN to crownPlace, ETreePart.TRUNK to trunkPlace, ETreePart.ROOTS to groundPlace)
    }

    private fun createReport(): String {
        var str = ""
        food.forEach { foodUnit -> str += "${foodUnit.key.prettyName}: ${foodUnit.value}\n" }
        return str
    }

    fun update(foodProbability: Float) {
        food.map { if (ThreadLocalRandom.current().nextFloat() < foodProbability) food[it.key] = it.value + ThreadLocalRandom.current().nextInt(1, 5) }
    }
}