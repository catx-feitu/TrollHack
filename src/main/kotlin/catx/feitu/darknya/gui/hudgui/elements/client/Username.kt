package catx.feitu.darknya.gui.hudgui.elements.client

import catx.feitu.darknya.event.SafeClientEvent
import dev.luna5ama.trollhack.gui.hudgui.LabelHud
import dev.luna5ama.trollhack.module.modules.client.GuiSetting

internal object Username : LabelHud(
    name = "Username",
    category = Category.CLIENT,
    description = "Player username"
) {

    private val prefix = setting("Prefix", "Welcome")
    private val suffix = setting("Suffix", "")

    override fun catx.feitu.darknya.event.SafeClientEvent.updateText() {
        displayText.add(prefix.value, GuiSetting.text)
        displayText.add(mc.session.username, GuiSetting.primary)
        displayText.add(suffix.value, GuiSetting.text)
    }

}