package dev.luna5ama.trollhack.util.inventory

import catx.feitu.darknya.event.SafeClientEvent
import net.minecraft.inventory.ClickType
import net.minecraft.inventory.Slot

object InstantFuture : StepFuture {
    override fun timeout(timeout: Long): Boolean {
        return true
    }

    override fun confirm() {

    }
}

class Click(
    private val windowID: Int,
    private val slotProvider: catx.feitu.darknya.event.SafeClientEvent.() -> Slot?,
    private val mouseButton: catx.feitu.darknya.event.SafeClientEvent.() -> Int?,
    private val type: ClickType
) : Step {
    constructor(windowID: Int, slotProvider: catx.feitu.darknya.event.SafeClientEvent.() -> Slot?, mouseButton: Int, type: ClickType) : this(
        windowID,
        slotProvider,
        { mouseButton },
        type
    )

    constructor(windowID: Int, slot: Slot, mouseButton: Int, type: ClickType) : this(
        windowID,
        { slot },
        mouseButton,
        type
    )

    override fun run(event: catx.feitu.darknya.event.SafeClientEvent): StepFuture {
        val slot = slotProvider.invoke(event)
        val mouseButton = mouseButton.invoke(event)
        return if (slot != null && mouseButton != null) {
            ClickFuture(event.clickSlot(windowID, slot, mouseButton, type))
        } else {
            InstantFuture
        }
    }
}

class ClickFuture(
    val id: Short,
) : StepFuture {
    private val time = System.currentTimeMillis()
    private var confirmed = false

    override fun timeout(timeout: Long): Boolean {
        return confirmed || System.currentTimeMillis() - time > timeout
    }

    override fun confirm() {
        confirmed = true
    }
}

interface Step {
    fun run(event: catx.feitu.darknya.event.SafeClientEvent): StepFuture
}

interface StepFuture {
    fun timeout(timeout: Long): Boolean
    fun confirm()
}