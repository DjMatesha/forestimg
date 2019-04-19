package Forest.AnimalInterfaces

import Forest.FieldCell

interface IFlyingCreature : ICreature {
    fun makeMove(field: List<List<FieldCell>>) = super.makeMove(field, 2, 0f)
}