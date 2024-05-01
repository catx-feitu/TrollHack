package dev.luna5ama.trollhack.module.modules.misc

import dev.luna5ama.trollhack.command.CommandManager
import dev.luna5ama.trollhack.event.events.ConnectionEvent
import dev.luna5ama.trollhack.event.events.TickEvent
import dev.luna5ama.trollhack.event.events.baritone.BaritoneCommandEvent
import dev.luna5ama.trollhack.event.listener
import dev.luna5ama.trollhack.event.safeListener
import dev.luna5ama.trollhack.module.Category
import dev.luna5ama.trollhack.module.Module
import dev.luna5ama.trollhack.util.BaritoneUtils
import dev.luna5ama.trollhack.util.accessor.sendClickBlockToController
import dev.luna5ama.trollhack.util.text.MessageSendUtils
import dev.luna5ama.trollhack.util.text.formatValue
import dev.luna5ama.trollhack.util.threads.runSafe

internal object AutoMine : Module(
    name = "Auto Mine",
    description = "Automatically mines chosen ores",
    category = Category.MISC
) {

    private val manual by setting("Manual", false)
    private val iron = setting("Iron", false)
    private val diamond = setting("Diamond", true)
    private val gold = setting("Gold", false)
    private val coal = setting("Coal", false)
    private val log = setting("Logs", false)

    init {
        onEnable {
            runSafe {
                run()
            } ?: disable()
        }

        onDisable {
            BaritoneUtils.cancelEverything()
        }
    }

    private fun run() {
        if (isDisabled || manual) return

        val blocks = ArrayList<String>()

        if (iron.value) blocks.add("iron_ore")
        if (diamond.value) blocks.add("diamond_ore")
        if (gold.value) blocks.add("gold_ore")
        if (coal.value) blocks.add("coal_ore")
        if (log.value) {
            blocks.add("log")
            blocks.add("log2")
        }

        if (blocks.isEmpty()) {
            MessageSendUtils.sendBaritoneMessage(
                "Error: you have to choose at least one thing to mine. " +
                    "To mine custom blocks run the ${formatValue("${CommandManager.prefix}b mine block")} command"
            )
            BaritoneUtils.cancelEverything()
            return
        }

        MessageSendUtils.sendBaritoneCommand("mine", *blocks.toTypedArray())
    }

    init {
        safeListener<TickEvent.Pre> {
            if (manual) {
                mc.sendClickBlockToController(true)
            }
        }

        listener<ConnectionEvent.Disconnect> {
            disable()
        }

        listener<BaritoneCommandEvent> {
            if (it.command.contains("cancel")) {
                disable()
            }
        }

        iron.listeners.add { runSafe { run() } }
        diamond.listeners.add { runSafe { run() } }
        gold.listeners.add { runSafe { run() } }
        coal.listeners.add { runSafe { run() } }
        log.listeners.add { runSafe { run() } }
    }
}