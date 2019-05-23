package view

import tornadofx.*


class CreateForestRequest(val width: Int, val height: Int, val animals: IntRange, val flocks: IntRange, val foodProb: Float ) : FXEvent(EventBus.RunOn.ApplicationThread)
class StartRequest(val mode: Boolean) : FXEvent(EventBus.RunOn.ApplicationThread)
class IterRequest : FXEvent(EventBus.RunOn.ApplicationThread)
class DeadForest : FXEvent(EventBus.RunOn.ApplicationThread)