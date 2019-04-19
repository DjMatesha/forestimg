package Forest

import Forest.AnimalInterfaces.ICreature
import Forest.Enums.EFood
import Forest.Enums.EAnimal
import Forest.Animals.*
import java.util.concurrent.ThreadLocalRandom

import kotlin.math.min

fun IntRange.random() = ThreadLocalRandom.current().nextInt(this.first, this.last)

object Forest {
    private var conf = Config(1, 1, 1..1, 0.1f)
    private val animals: MutableList<ICreature> = mutableListOf()
    private var field: List<List<FieldCell>> = listOf()
    private var iteration = 1

    fun initForest(Row: Int, Col: Int, animals: IntRange, foodProb: Float, treeHeight: Double, treeWidth: Double) {
        iteration = 1
        conf = Config(Row, Col, animals, foodProb)
        field = (0 until conf.rows).map { i ->
            List(conf.columns) { j ->
                FieldCell(j * treeWidth, i * treeHeight, treeWidth, treeHeight)

            }
        }
        updateField()

        Forest.animals.clear()
        Forest.animals += Badger(ThreadLocalRandom.current().nextInt(conf.rows), ThreadLocalRandom.current().nextInt(conf.columns), conf.animalsCount.random())
        Forest.animals += Chipmunk(ThreadLocalRandom.current().nextInt(conf.rows), ThreadLocalRandom.current().nextInt(conf.columns), conf.animalsCount.random())
        Forest.animals += Squirrel(ThreadLocalRandom.current().nextInt(conf.rows), ThreadLocalRandom.current().nextInt(conf.columns), conf.animalsCount.random())
        Forest.animals += FlyingSquirrel(ThreadLocalRandom.current().nextInt(conf.rows), ThreadLocalRandom.current().nextInt(conf.columns), conf.animalsCount.random())
        Forest.animals += Woodpecker(ThreadLocalRandom.current().nextInt(conf.rows), ThreadLocalRandom.current().nextInt(conf.columns), conf.animalsCount.random())
        Forest.animals += Wolf(ThreadLocalRandom.current().nextInt(conf.rows), ThreadLocalRandom.current().nextInt(conf.columns), conf.animalsCount.random())
        Forest.animals += Vulture(ThreadLocalRandom.current().nextInt(conf.rows), ThreadLocalRandom.current().nextInt(conf.columns), conf.animalsCount.random())
    }

    fun tree(row: Int, col: Int) = field[row][col]

    fun makeIteration() {
        iteration++
        discord()
        animals.forEach { processFood(field[it.row][it.col], it) }
        union()
        hunt()
        updateField()
        updateGeneration()
        createReport(iteration)
        animals.forEach { it.makeMove(field) }
    }

    private fun processFood(fieldCell: FieldCell, animal: ICreature) {
        animal.hungriness = min(animal.hungriness + 1, 100f)
        if (animal.foodPart == animal.treePart) {
            val eatable: List<Pair<EFood, Int>> = checkForFood(fieldCell, animal)
            var hungriness = animal.groupHungriness
            val newFood: MutableMap<EFood, Int> = mutableMapOf()
            for (foodUnit in eatable) {
                val foodValue = foodValues[foodUnit.first]
                val toEat = min((hungriness / foodValue!!).toInt(), foodUnit.second)

                newFood[foodUnit.first] = foodUnit.second - toEat
                animal.feed(toEat * foodValue)
                hungriness -= foodValue * toEat / animal.animalCount
            }
            fieldCell.food = newFood
        }
    }

    fun hunt() {
        for (hunter in animals) {
            for (pray in animals) {
                if (pray.animalType in hunter.hunt && pray.row == hunter.row && pray.col == hunter.col && pray.treePart == hunter.treePart && pray.animalCount != 0) {
                    var hungriness = hunter.groupHungriness
                    val foodValue = huntValues[pray.animalType]
                    val toEat = min((hungriness / foodValue!!).toInt(), pray.animalCount)
                    pray.animalCount -= toEat
                    pray.text.text = pray.animalCount.toString()
                    hunter.feed(toEat * foodValue)
                    hungriness -= foodValue * toEat / hunter.animalCount
                }
            }
        }
        animals.forEach { it.removeAnimal() }
        animals.removeIf { animal -> animal.animalCount == 0 }
    }


    private fun checkForFood(fieldCell: FieldCell, animal: ICreature): List<Pair<EFood, Int>> =
            fieldCell.food.map {
                if (it.key in animal.food) Pair(it.key, it.value) else Pair(it.key, 0)
            }.filter { it.second > 0 }

    private fun updateField() {
        field.forEach { row -> row.forEach { cell -> cell.update(conf.foodProbability) } }
    }

    private fun discord() {
        val list: MutableList<ICreature> = mutableListOf()
        animals.forEach { animals -> animals.discord()?.let { list.add(it) } }
        animals.addAll(list)
    }

    private fun union() {
        for (i in animals)
            for (j in animals)
                if (i.samePlace(j) && i.animalCount != 0) {
                    i.animalCount += j.animalCount
                    i.text.text = i.animalCount.toString()
                    j.animalCount = 0
                }
        animals.forEach { it.removeAnimal() }
        animals.removeIf { animal -> animal.animalCount == 0 }
    }

    private fun updateGeneration() {
        animals.forEach { animal -> animal.progeny() }
        animals.forEach { animal -> animal.removeAnimalIfDead() }
        animals.removeIf { animal -> animal.animalCount == 0 }
    }

    private fun createReport(currentIteration: Int) {
        println("Generation $currentIteration report:\n${"-".repeat(15)}")

        println("ANIMALS ALIVE")
        animals.forEach { animal ->
            createReportAboutAnimal(animal)
            println()
        }

        println("FOOD STATISTICS")
        val food: MutableMap<EFood, Int> = mutableMapOf()
        field.map { row ->
            row.map { cell ->
                cell.food.forEach { foodUnit ->
                    food[foodUnit.key] =
                            if (food[foodUnit.key] != null) food[foodUnit.key]!! + foodUnit.value else foodUnit.value
                }
            }
        }
        food.forEach { foodUnit -> println("${foodUnit.key.prettyName}: ${foodUnit.value}") }
    }

    private fun createReportAboutAnimal(animal: ICreature) {
        println("Animal: ${animal.animalType.prettyName}, ${animal.animalCount} units")
        println("Hungriness: [${animal.hungriness}/100]")
        println("Stamina: [${animal.stamina}/100]")
    }

    fun over() = animals.isEmpty()

    private val foodValues: Map<EFood, Int> = mapOf(
            Pair(EFood.ROOT_VEGETABLES, 5),
            Pair(EFood.CONES, 3),
            Pair(EFood.MAPLE_LEAVES, 2),
            Pair(EFood.NUTS, 5),
            Pair(EFood.WORMS, 7)
    )

    private val huntValues: Map<EAnimal, Int> = mapOf(
            Pair(EAnimal.BADGER, 5),
            Pair(EAnimal.CHIPMUNK, 4),
            Pair(EAnimal.FLYING_SQUIRREL, 2),
            Pair(EAnimal.SQUIRREL, 3),
            Pair(EAnimal.WOODPECKER, 2)
    )
}