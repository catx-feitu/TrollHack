package catx.feitu.darknya.setting

import dev.luna5ama.trollhack.TrollHackMod
import dev.luna5ama.trollhack.setting.configs.NameableConfig

internal object GenericConfig : NameableConfig<GenericConfigClass>(
    "generic",
    "${TrollHackMod.DIRECTORY}/config/"
)