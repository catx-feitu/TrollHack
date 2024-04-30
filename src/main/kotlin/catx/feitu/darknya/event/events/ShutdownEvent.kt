package catx.feitu.darknya.event.events

import dev.luna5ama.trollhack.event.Event
import dev.luna5ama.trollhack.event.EventBus
import dev.luna5ama.trollhack.event.EventPosting

object ShutdownEvent : Event, EventPosting by EventBus()