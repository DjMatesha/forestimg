package view

import tornadofx.*

class MainView : View("Forest") {
    override val root = borderpane {
        left<LeftView>()
        center<CenterView>()
        right<Timer>()
    }

    init {
        with(root) {
            prefWidth = 1000.0
            prefHeight = 600.0
        }
    }
}
