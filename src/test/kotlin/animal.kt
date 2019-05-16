import Forest.AnimalInterfaces.ICreature
import Forest.Animals.*
import Forest.Config
import Forest.FieldCell
import Forest.Forest
import Forest.Forest.conf
import Forest.Forest.field
import Forest.Forest.initForest
import io.kotlintest.matchers.*
import io.kotlintest.specs.FunSpec
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs
import kotlin.math.min

fun IntRange.random() = ThreadLocalRandom.current().nextInt(this.first, this.last)

class MyTest : FunSpec() {
    init {
        test("MoveTest") {
            initForest((1..10).random(), (1..20).random(), 1..99, ThreadLocalRandom.current().nextFloat(), 1.0, 1.0, testMode = true)
            Forest.animals.forEach {
                it.stamina = (0..100).random()
                val pred = it.copy(it.animalCount)
                it.makeMove(field)
                test("Animal stay in the forest") {
                    it.col shouldBe between(0, conf.columns - 1)
                    it.row shouldBe between(0, conf.rows - 1)
                }
                test("Stamina in the right range") {
                    it.stamina shouldBe between(0, 100)
                }
                test("Correct change of stamina") {
                    if (it.col == pred.col && it.row == pred.col)
                        it.stamina shouldBe min(pred.stamina + 4, 100)
                    else
                        it.stamina shouldBe pred.stamina - abs(it.row - pred.row) + abs(it.col - pred.col)
                }

            }
        }.config(invocations = 10)
        test("CopyTest") {
            val animals: MutableList<ICreature> = mutableListOf()
            animals += Badger((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Chipmunk((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Squirrel((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += FlyingSquirrel((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Woodpecker((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Wolf((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Vulture((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals.forEach {
                val count = (0..it.animalCount).random()
                val new = it.copy(count)
                new.animalCount shouldBe count
                new.stamina shouldBe it.stamina
                new.hungriness shouldBe it.hungriness
                new.animalType shouldBe it.animalType
                new shouldNotBe it
            }
        }.config(invocations = 10)
        test("DiscordTest") {
            val animals: MutableList<ICreature> = mutableListOf()
            animals += Badger((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Chipmunk((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Squirrel((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += FlyingSquirrel((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Woodpecker((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Wolf((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Vulture((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals.forEach {
                it.hungriness = ThreadLocalRandom.current().nextDouble(100.0).toFloat()
                val pred = it.copy(it.animalCount)
                val new = it.discord()
                if (new == null) {
                    pred.animalCount shouldBe it.animalCount
                    pred.stamina shouldBe it.stamina
                    pred.hungriness shouldBe it.hungriness
                    pred.animalType shouldBe it.animalType
                } else {
                    (new.animalCount + it.animalCount) shouldBe pred.animalCount
                    new.stamina shouldBe it.stamina
                    pred.stamina shouldBe it.stamina
                    new.hungriness shouldBe it.hungriness
                    pred.hungriness shouldBe it.hungriness
                    new.animalType shouldBe it.animalType
                    pred.animalType shouldBe it.animalType
                    new shouldNotBe it
                }
            }
        }.config(invocations = 10)
        test("ProgenyTest") {
            val animals: MutableList<ICreature> = mutableListOf()
            animals += Badger((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Chipmunk((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Squirrel((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += FlyingSquirrel((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Woodpecker((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Wolf((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Vulture((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals.forEach {
                it.hungriness = ThreadLocalRandom.current().nextDouble(100.0).toFloat()
                val count = it.animalCount
                it.progeny()
                if (count < 2) {
                    test("Require 2 animal for progeny") {
                        it.animalCount shouldBe count
                    }
                } else {
                    test("Amount of progeny") {
                        it.animalCount shouldBe lte(count + count / 2 + 1)
                    }
                }
            }
        }.config(invocations = 10)
        test("DeadTest") {
            val animals: MutableList<ICreature> = mutableListOf()
            animals += Badger((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Chipmunk((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Squirrel((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += FlyingSquirrel((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Woodpecker((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Wolf((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals += Vulture((1..10).random(), (1..20).random(), (1..99).random(), testMode = true)
            animals.forEach {
                it.hungriness = (99..100).random().toFloat()
                it.animalCount = (0..2).random()
                val count = it.animalCount
                it.removeAnimalIfDead()
                test("Require correct amount of animal") {
                    it.animalCount shouldBe gte(0)
                }
                test("No more than 1 dead animal") {
                    count-it.animalCount shouldBe between(0,1)
                }
            }
        }.config(invocations = 10)
    }
}