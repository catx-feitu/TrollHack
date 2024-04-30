package catx.feitu.darknya.event.events.player

import dev.luna5ama.trollhack.event.*

class PlayerTravelEvent : Event, ICancellable by Cancellable(), EventPosting by Companion {
    companion object : NamedProfilerEventBus("trollPlayerTravel")
}