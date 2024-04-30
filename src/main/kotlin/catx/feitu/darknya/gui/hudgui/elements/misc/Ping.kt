package catx.feitu.darknya.gui.hudgui.elements.misc

import catx.feitu.darknya.event.SafeClientEvent
import dev.luna5ama.trollhack.gui.hudgui.LabelHud
import dev.luna5ama.trollhack.module.modules.client.GuiSetting
import dev.luna5ama.trollhack.util.InfoCalculator

internal object Ping : LabelHud(
    name = "Ping",
    category = Category.MISC,
    description = "Delay between client and server"
) {

    override fun catx.feitu.darknya.event.SafeClientEvent.updateText() {
        displayText.add(InfoCalculator.ping().toString(), GuiSetting.text)
        displayText.add("ms", GuiSetting.primary)
    }

}