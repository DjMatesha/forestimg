package Forest.AnimalInterfaces

import Forest.Enums.EAnimal
import Forest.Enums.EFood
import Forest.Enums.ETreePart
import Forest.FieldCell
import Forest.Forest.tree
import Forest.random
import RPG.IAnimalCharacteristics
import RPG.ISkills
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.shape.Circle
import javafx.scene.text.Text
import tornadofx.*
import view.CenterView
import java.util.concurrent.ThreadLocalRandom

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

interface ICreature : IAnimalCharacteristics, ISkills {
    val animalType: EAnimal

    var row: Int
    var col: Int
    val ablePart: Set<ETreePart>
    var treePart: ETreePart

    var img: Circle
    val text: Text

    var animalCount: Int

    var stamina: Float
    var hungriness: Float
    val childProb: Float
    val fellowship: Float

    val groupHungriness get() = hungriness * animalCount

    val food: Set<EFood>
    val foodPart: ETreePart

    val hunt: Set<EAnimal>

    fun copy(count: Int): ICreature

    fun createReport() = "Animal: ${animalType.prettyName}, ${animalCount} units\nHungriness: [${hungriness}/100]\nStamina: [${stamina}/100]"

    fun makeMove(field: List<List<FieldCell>>,
                 minDistance: Int = 1,
                 minHungerGrowth: Float = 100f) {

        if (ThreadLocalRandom.current().nextInt(0, 100) >= hungriness) {
            stamina += 4
            return
        }

        fun getPossibleEndpoints(): List<MutableList<Pair<Int, Int>>> {
            val possibleEndpoints: MutableList<Pair<Int, Int>> = mutableListOf()
            val possibleEndpointsWithFood: MutableList<Pair<Int, Int>> = mutableListOf()

            (max(-minDistance, -row)..min(minDistance, field.size - row - 1)).map { dRow ->
                (max(-minDistance, -col)..min(minDistance, field[0].size - col - 1)).map { dCol ->
                    if (field[row + dRow][col + dCol].hasFood)
                        possibleEndpointsWithFood += Pair(row + dRow, col + dCol)
                    else
                        possibleEndpoints += Pair(row + dRow, col + dCol)
                }
            }

            return listOf(possibleEndpoints, possibleEndpointsWithFood)
        }

        val (possibleEndpoints, possibleEndpointsWithFood) = getPossibleEndpoints()

        var endpoint: Pair<Int, Int> =
                if (possibleEndpoints.isEmpty() ||
                        possibleEndpointsWithFood.isNotEmpty() && ThreadLocalRandom.current().nextInt(0, 20) < skills.pathfinding.multiplier
                )
                    possibleEndpointsWithFood.random()
                else possibleEndpoints.random()

        var dist: Int = abs(endpoint.first - row) + abs(endpoint.second - col)
        if (dist > stamina)
            endpoint = Pair(row, col).also { dist = 0 }

        fun drawMove(a: FieldCell, b: FieldCell, pos1: ETreePart, pos2: ETreePart) {
            val begin: Pair<Double, Double> = a.place[pos1]!![animalType]!!
            val end: Pair<Double, Double> = b.place[pos2]!![animalType]!!
            val timeline = javafx.animation.Timeline(javafx.animation.KeyFrame(javafx.util.Duration.millis(20.0),
                    object : EventHandler<ActionEvent> {

                        var dx = (end.first - begin.first) / 20 //Step on x or velocity
                        var dy = (end.second - begin.second)/ 20 //Step on y

                        override fun handle(t: ActionEvent) {
                            //move the ball
                            img.layoutX = img.layoutX + dx
                            img.layoutY = img.layoutY + dy
                            text.layoutX = text.layoutX + dx
                            text.layoutY = text.layoutY + dy
                        }
                    }))
            timeline.cycleCount = 20
            timeline.play()
        }
        stamina -= if (dist > 0) dist else -4
        hungriness = min(minHungerGrowth, hungriness + dist)
        val temp = if (ThreadLocalRandom.current().nextInt(0, 100) <= hungriness) foodPart
        else ablePart.elementAt(ThreadLocalRandom.current().nextInt(ablePart.count()))
        drawMove(tree(row, col), tree(endpoint.first, endpoint.second), treePart, temp)
        treePart = temp
        row = endpoint.first
        col = endpoint.second
    }

    fun feed(foodPoints: Int) {
        hungriness -= foodPoints / animalCount
    }

    fun samePlace(animal : ICreature) = ( !(this===animal) && row==animal.row && col == animal.col && animalType == animal.animalType && treePart == animal.treePart)

    fun progeny() {
        if (animalCount >= 2 && ThreadLocalRandom.current().nextInt(0, 100) < childProb * (100 - hungriness) / 100) {
            animalCount += ThreadLocalRandom.current().nextInt(1, animalCount / 2 + 1)
            text.text = animalCount.toString()
        }
    }

    fun discord(): ICreature? {
        if (animalCount > fellowship && ThreadLocalRandom.current().nextInt(0, 100) >= fellowship) {
            val part: Int = animalCount/2
            this.animalCount -= part
            this.text.text = animalCount.toString()
            return copy(part)
        }
        return null
    }

    fun removeAnimalIfDead() {
        if (hungriness == 100f) {
            animalCount = max(0,animalCount-1)
            text.text = animalCount.toString()
            if (animalCount == 0)
                this.removeAnimal()
        }
    }

    fun removeAnimal(){
        if (animalCount==0){
            val canvas = find(CenterView::class).root
            canvas.children.remove(img)
            canvas.children.remove(text)
        }
    }
}

private fun <E> List<E>.random(): E = this[(0 until this.size).random()]
