package view

import tornadofx.*
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.timerTask


class Timer: View() {
    companion object {
        var work = false
    }
    private val task = timerTask { fire(IterRequest()) }
    private var timer : Timer? = null

    override val root = vbox {
        setMaxSize(0.0, 0.0)
        subscribe<StartRequest> {event ->
            println(event.mode)
            when(event.mode) {
                true -> {
                    timer = fixedRateTimer("Timer", true, 0, 500, {task.run()})
                    work = true
                }
                false -> {
                    work = false
                    timer?.cancel()
                    timer = null
                }
            }
        }
    }
}