package catx.feitu.darknya.event.events

import dev.luna5ama.trollhack.event.Event
import dev.luna5ama.trollhack.event.EventBus
import dev.luna5ama.trollhack.event.EventPosting
import catx.feitu.darknya.module.AbstractModule

class ModuleToggleEvent internal constructor(val module: catx.feitu.darknya.module.AbstractModule) : Event, EventPosting by Companion {
    companion object : EventBus()
}