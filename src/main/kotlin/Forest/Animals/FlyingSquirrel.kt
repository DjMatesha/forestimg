package Forest.Animals

import Forest.AnimalInterfaces.IFlyingCreature
import Forest.Enums.EAnimal
import Forest.Enums.EFood
import Forest.Enums.ETreePart
import Forest.Forest.tree
import RPG.BaseSkills
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Alert
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import tornadofx.*
import view.CenterView
import view.Timer
import java.util.concurrent.ThreadLocalRandom

class FlyingSquirrel(
        override var row: Int,
        override var col: Int,
        override var animalCount: Int
) : IFlyingCreature {
    private val canvas = find(CenterView::class).root
    override val ablePart = setOf(ETreePart.CROWN, ETreePart.TRUNK, ETreePart.ROOTS)
    override var treePart = ablePart.elementAt(ThreadLocalRandom.current().nextInt(ablePart.count()))
    override val foodPart = ETreePart.CROWN

    override var img = Circle(10.0, Color.LIGHTGRAY)

    override val hunt: Set<EAnimal> = setOf()
    override val food: Set<EFood> = setOf(EFood.MAPLE_LEAVES)
    override val animalType = EAnimal.FLYING_SQUIRREL

    override var stamina = 50.0f
    override var hungriness = 50.0f
    override val childProb = 10.0f
    override val fellowship = 10.0f

    override val skills = BaseSkills()

    override var health: Int = 1
    override val additionalHealthOnLevelUp: Int = 1

    override var damage: IntRange = 1..1
    override val additionalDamageOnLevelUp: IntRange = 1..1

    override var maxWeight: Int = 1
    override val additionalWeightOnLevelUp: Int = 1

    override var intellect: Int = 1
    override val additionalIntellectOnLevelUp: Int = 1
    override val text = javafx.scene.text.Text("$animalCount")

    override fun copy(count: Int): FlyingSquirrel {
        val temp = FlyingSquirrel(row, col, animalCount)
        temp.stamina = stamina
        temp.hungriness = hungriness
        return temp
    }

    init {
        val p: Pair<Double, Double> = tree(row, col).place[treePart]!![animalType]!!
        img.relocate(p.first, p.second)
        img.setOnMouseClicked {
            if (!Timer.work) {
                val alert = Alert(AlertType.INFORMATION)
                alert.title = "Сведения о стае"
                alert.headerText = null
                alert.contentText = this.createReport()
                alert.showAndWait()
            }
        }
        text.relocate(p.first + 5, p.second + 5)
        text.font = Font.font("TimesRoman", FontWeight.BOLD, 12.0)
        text.setOnMouseClicked {
            if (!Timer.work) {
                val alert = Alert(AlertType.INFORMATION)
                alert.title = "Сведения о стае"
                alert.headerText = null
                alert.contentText = this.createReport()
                alert.showAndWait()
            }
        }
        canvas.children.addAll(img, text)
    }
}