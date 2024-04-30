package catx.feitu.darknya.event.events.render

import dev.luna5ama.trollhack.event.*
import net.minecraftforge.client.event.RenderGameOverlayEvent

sealed class RenderOverlayEvent(override val event: RenderGameOverlayEvent) : Event, WrappedForgeEvent {
    val type: RenderGameOverlayEvent.ElementType
        get() = event.type

    class Pre(event: RenderGameOverlayEvent.Pre) : RenderOverlayEvent(event), ICancellable, EventPosting by Companion {
        companion object : EventBus()
    }

    class Post(event: RenderGameOverlayEvent.Post) : RenderOverlayEvent(event), ICancellable,
        EventPosting by Companion {
        companion object : EventBus()
    }
}
