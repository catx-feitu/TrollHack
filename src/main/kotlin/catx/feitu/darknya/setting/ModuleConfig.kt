package catx.feitu.darknya.setting

import dev.luna5ama.trollhack.TrollHackMod
import catx.feitu.darknya.module.AbstractModule
import dev.luna5ama.trollhack.module.modules.client.Configurations
import dev.luna5ama.trollhack.setting.configs.NameableConfig
import java.io.File

internal object ModuleConfig : NameableConfig<catx.feitu.darknya.module.AbstractModule>(
    "modules",
    "${TrollHackMod.DIRECTORY}/config/modules",
) {
    override val file: File get() = File("$filePath/${Configurations.modulePreset}.json")
    override val backup get() = File("$filePath/${Configurations.modulePreset}.bak")
}