package catx.feitu.darknya.gui

import dev.fastmc.common.TimeUnit
import dev.luna5ama.trollhack.AsyncLoader
import dev.luna5ama.trollhack.TrollHackMod
import catx.feitu.darknya.gui.clickgui.TrollClickGui
import dev.luna5ama.trollhack.gui.hudgui.AbstractHudElement
import dev.luna5ama.trollhack.gui.hudgui.TrollHudGui
import dev.luna5ama.trollhack.util.ClassUtils.instance
import dev.luna5ama.trollhack.util.collections.AliasSet
import dev.luna5ama.trollhack.util.delegate.AsyncCachedValue
import kotlinx.coroutines.Deferred
import java.lang.reflect.Modifier
import kotlin.system.measureTimeMillis

internal object GuiManager : AsyncLoader<List<Class<out AbstractHudElement>>> {
    override var deferred: Deferred<List<Class<out AbstractHudElement>>>? = null
    private val hudElementSet = AliasSet<AbstractHudElement>()

    val hudElements by AsyncCachedValue(5L, TimeUnit.SECONDS) {
        catx.feitu.darknya.gui.GuiManager.hudElementSet.distinct().sortedBy { it.nameAsString }
    }

    override suspend fun preLoad0(): List<Class<out AbstractHudElement>> {
        val classes = AsyncLoader.classes.await()
        val list: List<Class<*>>

        val time = measureTimeMillis {
            val clazz = AbstractHudElement::class.java

            list = classes.asSequence()
                .filter { Modifier.isFinal(it.modifiers) }
                .filter { it.name.startsWith("dev.luna5ama.trollhack.gui.hudgui.elements") }
                .filter { clazz.isAssignableFrom(it) }
                .sortedBy { it.simpleName }
                .toList()
        }

        TrollHackMod.logger.info("${list.size} hud elements found, took ${time}ms")

        @Suppress("UNCHECKED_CAST")
        return list as List<Class<out AbstractHudElement>>
    }

    override suspend fun load0(input: List<Class<out AbstractHudElement>>) {
        val time = measureTimeMillis {
            for (clazz in input) {
                catx.feitu.darknya.gui.GuiManager.register(clazz.instance)
            }
        }

        TrollHackMod.logger.info("${input.size} hud elements loaded, took ${time}ms")

        catx.feitu.darknya.gui.clickgui.TrollClickGui.onGuiClosed()
        TrollHudGui.onGuiClosed()

        catx.feitu.darknya.gui.clickgui.TrollClickGui.subscribe()
        TrollHudGui.subscribe()
    }

    internal fun register(hudElement: AbstractHudElement) {
        catx.feitu.darknya.gui.GuiManager.hudElementSet.add(hudElement)
        TrollHudGui.register(hudElement)
    }

    internal fun unregister(hudElement: AbstractHudElement) {
        catx.feitu.darknya.gui.GuiManager.hudElementSet.remove(hudElement)
        TrollHudGui.unregister(hudElement)
    }

    fun getHudElementOrNull(name: String?) = name?.let { catx.feitu.darknya.gui.GuiManager.hudElementSet[it] }
}