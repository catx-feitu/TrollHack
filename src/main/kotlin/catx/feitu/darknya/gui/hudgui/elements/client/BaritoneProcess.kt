package catx.feitu.darknya.gui.hudgui.elements.client

import catx.feitu.darknya.event.SafeClientEvent
import dev.luna5ama.trollhack.gui.hudgui.LabelHud
import dev.luna5ama.trollhack.module.modules.movement.AutoWalk
import catx.feitu.darknya.process.PauseProcess
import dev.luna5ama.trollhack.util.BaritoneUtils

internal object BaritoneProcess : LabelHud(
    name = "Baritone Process",
    category = Category.CLIENT,
    description = "Shows what Baritone is doing"
) {

    override fun catx.feitu.darknya.event.SafeClientEvent.updateText() {
        val process = BaritoneUtils.primary?.pathingControlManager?.mostRecentInControl()?.orElse(null) ?: return

        when {
            process == PauseProcess -> {
                displayText.addLine(process.displayName0())
            }
            AutoWalk.baritoneWalk -> {
                displayText.addLine("AutoWalk (${AutoWalk.direction.displayName})")
            }
            else -> {
                displayText.addLine("Process: ${process.displayName()}")
            }
        }
    }

}