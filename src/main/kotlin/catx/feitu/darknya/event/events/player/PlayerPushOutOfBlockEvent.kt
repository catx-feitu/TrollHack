package catx.feitu.darknya.event.events.player

import dev.luna5ama.trollhack.event.*
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent

class PlayerPushOutOfBlockEvent(override val event: PlayerSPPushOutOfBlocksEvent) : Event, ICancellable,
    WrappedForgeEvent, EventPosting by Companion {
    companion object : EventBus()
}