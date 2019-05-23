package view

import Forest.Forest
import tornadofx.*

import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle

class CenterView : View() {
    override val root = Pane()

    init {
        with(root) {
            style { borderColor += box(c("#a1a1a1")) }
        }
        subscribe<IterRequest> {
            Forest.makeIteration()
            if (Forest.over()){
                fire(StartRequest(false))
                fire(DeadForest())
                root.clear()
            }
        }
        subscribe<CreateForestRequest> {event ->
            root.add(Rectangle(root.width,root.height, c("#a1a1a1")))
            Forest.initForest(event.height, event.width, event.animals, event.flocks, event.foodProb, root.height/event.height, root.width/event.width)
        }
    }
}