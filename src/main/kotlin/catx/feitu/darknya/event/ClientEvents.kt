package catx.feitu.darknya.event

import dev.luna5ama.trollhack.command.CommandManager
import dev.luna5ama.trollhack.command.execute.ExecuteEvent
import dev.luna5ama.trollhack.command.execute.IExecuteEvent
import dev.luna5ama.trollhack.event.events.ConnectionEvent
import dev.luna5ama.trollhack.event.events.RunGameLoopEvent
import dev.luna5ama.trollhack.event.events.WorldEvent
import dev.luna5ama.trollhack.util.Wrapper
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.multiplayer.PlayerControllerMP
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraftforge.fml.common.eventhandler.Event

abstract class AbstractClientEvent {
    val mc = Wrapper.minecraft
    abstract val world: WorldClient?
    abstract val player: EntityPlayerSP?
    abstract val playerController: PlayerControllerMP?
    abstract val connection: NetHandlerPlayClient?
}

open class ClientEvent : catx.feitu.darknya.event.AbstractClientEvent() {
    final override val world: WorldClient? = mc.world
    final override val player: EntityPlayerSP? = mc.player
    final override val playerController: PlayerControllerMP? = mc.playerController
    final override val connection: NetHandlerPlayClient? = mc.connection

    inline operator fun <T> invoke(block: catx.feitu.darknya.event.ClientEvent.() -> T) = run(block)
}

open class SafeClientEvent internal constructor(
    override val world: WorldClient,
    override val player: EntityPlayerSP,
    override val playerController: PlayerControllerMP,
    override val connection: NetHandlerPlayClient
) : catx.feitu.darknya.event.AbstractClientEvent() {
    inline operator fun <T> invoke(block: catx.feitu.darknya.event.SafeClientEvent.() -> T) = run(block)

    companion object : catx.feitu.darknya.event.ListenerOwner() {
        var instance: catx.feitu.darknya.event.SafeClientEvent? = null; private set

        init {
            listener<ConnectionEvent.Disconnect>(Int.MAX_VALUE, true) {
                catx.feitu.darknya.event.SafeClientEvent.Companion.reset()
            }

            listener<WorldEvent.Unload>(Int.MAX_VALUE, true) {
                catx.feitu.darknya.event.SafeClientEvent.Companion.reset()
            }

            listener<RunGameLoopEvent.Tick>(Int.MAX_VALUE, true) {
                catx.feitu.darknya.event.SafeClientEvent.Companion.update()
            }
        }

        fun update() {
            val world = Wrapper.world ?: return
            val player = Wrapper.player ?: return
            val playerController = Wrapper.minecraft.playerController ?: return
            val connection = Wrapper.minecraft.connection ?: return

            catx.feitu.darknya.event.SafeClientEvent.Companion.instance =
                catx.feitu.darknya.event.SafeClientEvent(world, player, playerController, connection)
        }

        fun reset() {
            catx.feitu.darknya.event.SafeClientEvent.Companion.instance = null
        }
    }
}

class ClientExecuteEvent(
    args: Array<String>
) : catx.feitu.darknya.event.ClientEvent(), IExecuteEvent by ExecuteEvent(CommandManager, args)

class SafeExecuteEvent internal constructor(
    world: WorldClient,
    player: EntityPlayerSP,
    playerController: PlayerControllerMP,
    connection: NetHandlerPlayClient,
    event: catx.feitu.darknya.event.ClientExecuteEvent
) : catx.feitu.darknya.event.SafeClientEvent(world, player, playerController, connection), IExecuteEvent by event

fun Event.cancel() {
    this.isCanceled = true
}