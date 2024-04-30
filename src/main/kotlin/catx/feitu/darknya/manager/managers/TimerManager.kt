package catx.feitu.darknya.manager.managers

import dev.luna5ama.trollhack.event.events.RunGameLoopEvent
import dev.luna5ama.trollhack.event.events.TickEvent
import dev.luna5ama.trollhack.event.listener
import catx.feitu.darknya.graphics.RenderUtils3D
import catx.feitu.darknya.manager.Manager
import catx.feitu.darknya.module.AbstractModule
import dev.luna5ama.trollhack.util.accessor.tickLength
import dev.luna5ama.trollhack.util.accessor.timer
import dev.luna5ama.trollhack.util.extension.lastValueOrNull
import dev.luna5ama.trollhack.util.extension.synchronized
import dev.luna5ama.trollhack.util.threads.runSafe
import java.util.*
import kotlin.math.roundToInt

object TimerManager : Manager() {
    private val modifiers = TreeMap<catx.feitu.darknya.module.AbstractModule, Modifier>().synchronized()
    private var modified = false

    var globalTicks = Int.MIN_VALUE; private set
    var tickLength = 50.0f; private set

    init {
        listener<RunGameLoopEvent.Start>(Int.MAX_VALUE, true) {
            runSafe {
                synchronized(modifiers) {
                    modifiers.values.removeIf { it.endTick < globalTicks }
                    modifiers.lastValueOrNull()?.let {
                        mc.timer.tickLength = it.tickLength
                    } ?: return@runSafe null
                }

                modified = true
            } ?: run {
                modifiers.clear()
                if (modified) {
                    mc.timer.tickLength = 50.0f
                    modified = false
                }
            }

            tickLength = mc.timer.tickLength
        }

        listener<TickEvent.Pre>(Int.MAX_VALUE, true) {
            globalTicks++
        }
    }

    fun catx.feitu.darknya.module.AbstractModule.resetTimer() {
        modifiers.remove(this)
    }

    fun catx.feitu.darknya.module.AbstractModule.modifyTimer(tickLength: Float, timeoutTicks: Int = 1) {
        runSafe {
            modifiers[this@modifyTimer] =
                Modifier(tickLength, globalTicks + RenderUtils3D.partialTicks.roundToInt() + timeoutTicks)
        }
    }

    private class Modifier(
        val tickLength: Float,
        val endTick: Int
    )
}