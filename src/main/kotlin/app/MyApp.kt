package app

import javafx.scene.image.Image
import view.MainView
import javafx.stage.Stage
import tornadofx.App
import view.StartRequest
import java.io.File

class MyApp: App(MainView::class) {
    override fun start(stage: Stage) {
        stage.isResizable = false
        stage.icons += Image(File("src\\main\\img\\icon.png").toURI().toString())
        super.start(stage)
    }

    override fun stop() {
        fire(StartRequest(false))
        super.stop()
    }
}