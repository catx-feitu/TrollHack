package catx.feitu.darknya.process

import baritone.api.process.IBaritoneProcess
import baritone.api.process.PathingCommand
import baritone.api.process.PathingCommandType
import dev.fastmc.common.TickTimer
import dev.fastmc.common.TimeUnit
import catx.feitu.darknya.module.AbstractModule
import dev.luna5ama.trollhack.util.BaritoneUtils

object PauseProcess : IBaritoneProcess {

    private val pauseModules = HashMap<catx.feitu.darknya.module.AbstractModule, Long>()
    private val timer = TickTimer(TimeUnit.SECONDS)
    private var lastPausingModule: catx.feitu.darknya.module.AbstractModule? = null

    override fun isTemporary(): Boolean {
        return true
    }

    override fun priority(): Double {
        return 5.0
    }

    override fun isActive(): Boolean {
        return pauseModules.isNotEmpty()
    }

    override fun displayName0(): String {
        return "Paused by module: ${lastPausingModule?.name}"
    }

    override fun onLostControl() {
        // nothing :p
    }

    override fun onTick(calcFailed: Boolean, isSafeToCancel: Boolean): PathingCommand {
        if (timer.tickAndReset(1L)) {
            pauseModules.entries.removeIf { it.key.isDisabled || System.currentTimeMillis() - it.value > 3000L }
        }

        return PathingCommand(null, PathingCommandType.REQUEST_PAUSE)
    }

    fun catx.feitu.darknya.module.AbstractModule.pauseBaritone() {
        if (pauseModules.isEmpty()) {
            BaritoneUtils.primary?.pathingControlManager?.registerProcess(this@PauseProcess)
        }

        lastPausingModule = this

        pauseModules[this] = System.currentTimeMillis()
    }

    fun catx.feitu.darknya.module.AbstractModule.unpauseBaritone() {
        pauseModules.remove(this)
    }

    fun isPausing(module: catx.feitu.darknya.module.AbstractModule) =
        pauseModules.containsKey(module)
}