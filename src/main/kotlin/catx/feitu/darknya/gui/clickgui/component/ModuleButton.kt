package catx.feitu.darknya.gui.clickgui.component

import catx.feitu.darknya.gui.clickgui.TrollClickGui
import dev.luna5ama.trollhack.gui.clickgui.window.ModuleSettingWindow
import dev.luna5ama.trollhack.gui.rgui.component.Button
import catx.feitu.darknya.module.AbstractModule

class ModuleButton(
    override val screen: catx.feitu.darknya.gui.clickgui.TrollClickGui,
    val module: catx.feitu.darknya.module.AbstractModule
) : Button(
    screen,
    module.name,
    module.description
) {
    override val progress: Float
        get() = if (module.isEnabled) 1.0f else 0.0f

    private val settingWindow by lazy { ModuleSettingWindow(screen, module) }

    init {
        action { _, buttonId ->
            when (buttonId) {
                0 -> module.toggle()
                1 -> screen.displayWindow(settingWindow)
            }
        }
    }
}