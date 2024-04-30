package catx.feitu.darknya.gui.clickgui.window

import dev.luna5ama.trollhack.gui.IGuiScreen
import dev.luna5ama.trollhack.gui.rgui.windows.SettingWindow
import catx.feitu.darknya.module.AbstractModule
import dev.luna5ama.trollhack.setting.groups.SettingGroup
import dev.luna5ama.trollhack.setting.settings.AbstractSetting

class ModuleSettingWindow(
    screen: IGuiScreen,
    module: catx.feitu.darknya.module.AbstractModule,
) : SettingWindow<catx.feitu.darknya.module.AbstractModule>(screen, module.name, module, UiSettingGroup.NONE) {
    override val elementSettingGroup: SettingGroup
        get() {
            return element.settingGroup
        }

    override val elementSettingList: List<AbstractSetting<*>>
        get() {
            return element.fullSettingList.filter { it.name != "Enabled" }
        }
}