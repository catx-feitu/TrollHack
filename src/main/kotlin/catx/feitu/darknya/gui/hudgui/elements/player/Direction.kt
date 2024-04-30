package catx.feitu.darknya.gui.hudgui.elements.player

import catx.feitu.darknya.event.SafeClientEvent
import dev.luna5ama.trollhack.gui.hudgui.LabelHud
import dev.luna5ama.trollhack.module.modules.client.GuiSetting
import dev.luna5ama.trollhack.util.math.Direction

internal object Direction : LabelHud(
    name = "Direction",
    category = Category.PLAYER,
    description = "Direction of player facing to"
) {

    override fun catx.feitu.darknya.event.SafeClientEvent.updateText() {
        val entity = mc.renderViewEntity ?: player
        val direction = Direction.fromEntity(entity)
        displayText.add(direction.displayString, GuiSetting.primary)
        displayText.add("(${direction.displayNameXY})", GuiSetting.text)
    }

}