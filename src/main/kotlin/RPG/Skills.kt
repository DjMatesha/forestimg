package RPG

class Skill(private var currentExperience: Int = 0) {
    val multiplier = multipliersOnLevel[expToLevelUp.indexOfLast { it <= currentExperience }]

    fun addExp(exp: Int) {
        currentExperience += exp
    }

    companion object Levels {
        val expToLevelUp = listOf(0, 100, 250, 500, 1000, 2500)
        val multipliersOnLevel = listOf(0, 1, 2, 5, 10, 20)
    }
}

class BaseSkills {
    val strength = Skill()
    val agility = Skill()
    val endurance = Skill()
    val diplomacy = Skill()
    val pathfinding = Skill()
}

interface ISkills {
    val skills: BaseSkills

    val strength get() = skills.strength.multiplier
    fun addExpToStrength(exp: Int) = skills.strength.addExp(exp)

    val agility get() = skills.agility.multiplier
    fun addExpToAgility(exp: Int) = skills.agility.addExp(exp)

    val endurance get() = skills.endurance.multiplier
    fun addExpToEndurance(exp: Int) = skills.endurance.addExp(exp)

    val diplomacy get() = skills.diplomacy.multiplier
    fun addExpToDiplomacy(exp: Int) = skills.diplomacy.addExp(exp)

    val pathfinding get() = skills.pathfinding.multiplier
    fun addExpToPathfinding(exp: Int) = skills.pathfinding.addExp(exp)
}