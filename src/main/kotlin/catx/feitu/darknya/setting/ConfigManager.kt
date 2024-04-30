package catx.feitu.darknya.setting

import dev.luna5ama.trollhack.TrollHackMod
import dev.luna5ama.trollhack.setting.configs.IConfig
import dev.luna5ama.trollhack.util.collections.NameableSet

internal object ConfigManager {
    private val configSet = NameableSet<IConfig>()

    init {
        catx.feitu.darknya.setting.ConfigManager.register(catx.feitu.darknya.setting.GuiConfig)
        catx.feitu.darknya.setting.ConfigManager.register(catx.feitu.darknya.setting.ModuleConfig)
    }

    fun loadAll(): Boolean {
        var success =
            catx.feitu.darknya.setting.ConfigManager.load(catx.feitu.darknya.setting.GenericConfig) // Generic config must be loaded first

        catx.feitu.darknya.setting.ConfigManager.configSet.forEach {
            success = catx.feitu.darknya.setting.ConfigManager.load(it) || success
        }

        return success
    }

    fun load(config: IConfig): Boolean {
        return try {
            config.load()
            TrollHackMod.logger.info("${config.name} config loaded")
            true
        } catch (e: Exception) {
            TrollHackMod.logger.error("Failed to load ${config.name} config", e)
            false
        }
    }

    fun saveAll(): Boolean {
        var success =
            catx.feitu.darknya.setting.ConfigManager.save(catx.feitu.darknya.setting.GenericConfig) // Generic config must be loaded first

        catx.feitu.darknya.setting.ConfigManager.configSet.forEach {
            success = catx.feitu.darknya.setting.ConfigManager.save(it) || success
        }

        return success
    }

    fun save(config: IConfig): Boolean {
        return try {
            config.save()
            TrollHackMod.logger.info("${config.name} config saved")
            true
        } catch (e: Exception) {
            TrollHackMod.logger.error("Failed to save ${config.name} config!", e)
            false
        }
    }

    fun register(config: IConfig) {
        catx.feitu.darknya.setting.ConfigManager.configSet.add(config)
    }

    fun unregister(config: IConfig) {
        catx.feitu.darknya.setting.ConfigManager.configSet.remove(config)
    }
}