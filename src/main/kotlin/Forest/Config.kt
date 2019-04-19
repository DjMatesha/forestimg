package Forest

data class Config(
    val rows: Int,
    val columns: Int,
    val animalsCount: IntRange,
    val foodProbability: Float
) {
    init {
        require(rows in 1..10)
        { "Number of rows should be an integer between 1 and 100." }
        require(columns in 1..20)
        { "Number of columns should be an integer between 1 and 100." }
        require(animalsCount.first in 1..99 && animalsCount.last in 1..99)
        { "Number of non-predator animals should be an IntRange between 1 and 100." }
        require(foodProbability in 0.0..1.0)
        { "Food probability should be a float between 0 and 1." }
    }
}