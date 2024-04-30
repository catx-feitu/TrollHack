package catx.feitu.darknya.manager.managers

import dev.fastmc.common.TickTimer
import catx.feitu.darknya.event.SafeClientEvent
import dev.luna5ama.trollhack.event.events.PacketEvent
import dev.luna5ama.trollhack.event.events.RunGameLoopEvent
import dev.luna5ama.trollhack.event.events.WorldEvent
import dev.luna5ama.trollhack.event.listener
import dev.luna5ama.trollhack.event.safeListener
import catx.feitu.darknya.manager.Manager
import dev.luna5ama.trollhack.util.TpsCalculator
import dev.luna5ama.trollhack.util.inventory.ClickFuture
import dev.luna5ama.trollhack.util.inventory.InventoryTask
import dev.luna5ama.trollhack.util.inventory.StepFuture
import dev.luna5ama.trollhack.util.inventory.removeHoldingItem
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.network.play.server.SPacketConfirmTransaction
import java.util.*

object InventoryTaskManager : Manager() {
    private val confirmMap = Short2ObjectOpenHashMap<ClickFuture>()
    private val taskQueue = PriorityQueue<InventoryTask>()
    private val timer = TickTimer()
    private var lastTask: InventoryTask? = null

    private val queueLock = Any()

    init {
        listener<PacketEvent.Receive> {
            if (it.packet !is SPacketConfirmTransaction) return@listener
            synchronized(queueLock) {
                confirmMap.remove(it.packet.actionNumber)?.confirm()
            }
        }

        safeListener<RunGameLoopEvent.Render> {
            if (lastTask == null && taskQueue.isEmpty()) {
                InventoryTask.resetIdCounter()
                return@safeListener
            }
            if (!timer.tick(0L)) return@safeListener

            lastTaskOrNext()?.let {
                runTask(it)
            }
        }

        listener<WorldEvent.Unload> {
            reset()
        }
    }

    fun addTask(task: InventoryTask) {
        synchronized(queueLock) {
            taskQueue.add(task)
        }
    }

    fun runNow(event: catx.feitu.darknya.event.SafeClientEvent, task: InventoryTask) {
        synchronized(InventoryTaskManager) {
            event {
                if (!player.inventory.itemStack.isEmpty) {
                    removeHoldingItem()
                }

                while (!task.finished) {
                    task.runTask(event)?.let {
                        handleFuture(it)
                    }
                }

                timer.reset((task.postDelay * TpsCalculator.multiplier).toLong())
            }
        }
    }

    private fun catx.feitu.darknya.event.SafeClientEvent.lastTaskOrNext(): InventoryTask? {
        return lastTask ?: run {
            val newTask = synchronized(queueLock) {
                taskQueue.poll()?.also { lastTask = it }
            } ?: return null

            if (!player.inventory.itemStack.isEmpty) {
                removeHoldingItem()
                return null
            }

            newTask
        }
    }

    private fun catx.feitu.darknya.event.SafeClientEvent.runTask(task: InventoryTask) {
        synchronized(InventoryTaskManager) {
            if (mc.currentScreen is GuiContainer && !task.runInGui && !player.inventory.itemStack.isEmpty) {
                timer.reset(500L)
                return
            }

            if (task.delay == 0L) {
                runNow(this, task)
            } else {
                task.runTask(this)?.let {
                    handleFuture(it)
                    timer.reset((task.delay * TpsCalculator.multiplier).toLong())
                }
            }

            if (task.finished) {
                timer.reset((task.postDelay * TpsCalculator.multiplier).toLong())
                lastTask = null
                return
            }
        }
    }

    private fun handleFuture(future: StepFuture) {
        if (future is ClickFuture) {
            synchronized(queueLock) {
                confirmMap[future.id] = future
            }
        }
    }

    private fun reset() {
        synchronized(queueLock) {
            timer.time = 0L
            confirmMap.clear()
            lastTask?.cancel()
            lastTask = null
            taskQueue.forEach {
                it.cancel()
            }
            taskQueue.clear()
        }
    }

}