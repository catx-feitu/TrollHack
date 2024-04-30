package catx.feitu.darknya.gui.hudgui.elements.world

import catx.feitu.darknya.event.SafeClientEvent
import dev.luna5ama.trollhack.gui.hudgui.LabelHud
import dev.luna5ama.trollhack.module.modules.client.GuiSetting

internal object Biome : LabelHud(
    name = "Biome",
    category = Category.WORLD,
    description = "Display the current biome you are in"
) {

    override fun catx.feitu.darknya.event.SafeClientEvent.updateText() {
        val biome = world.getBiome(player.position).biomeName ?: "Unknown"

        displayText.add(biome, GuiSetting.text)
        displayText.add("Biome", GuiSetting.primary)
    }

}