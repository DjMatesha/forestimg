package RPG

interface IAnimalCharacteristics {
    var health: Int
    val additionalHealthOnLevelUp: Int

    var damage: IntRange
    val additionalDamageOnLevelUp: IntRange

    var maxWeight: Int
    val additionalWeightOnLevelUp: Int

    var intellect: Int
    val additionalIntellectOnLevelUp: Int
}