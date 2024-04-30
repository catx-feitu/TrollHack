package catx.feitu.darknya.gui.hudgui.elements.misc

import catx.feitu.darknya.event.SafeClientEvent
import dev.luna5ama.trollhack.gui.hudgui.LabelHud
import dev.luna5ama.trollhack.module.modules.client.GuiSetting

internal object ServerBrand : LabelHud(
    name = "Server Brand",
    category = Category.MISC,
    description = "Brand / type of the server"
) {

    override fun catx.feitu.darknya.event.SafeClientEvent.updateText() {
        if (mc.isIntegratedServerRunning) {
            displayText.add("Singleplayer: " + mc.player?.serverBrand)
        } else {
            val serverBrand = mc.player?.serverBrand ?: "Unknown Server Type"
            displayText.add(serverBrand, GuiSetting.text)
        }
    }

}