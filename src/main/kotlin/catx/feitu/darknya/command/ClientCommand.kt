package catx.feitu.darknya.command

import dev.luna5ama.trollhack.command.args.AbstractArg
import dev.luna5ama.trollhack.event.AlwaysListening
import catx.feitu.darknya.event.ClientExecuteEvent
import catx.feitu.darknya.event.SafeExecuteEvent
import dev.luna5ama.trollhack.gui.hudgui.AbstractHudElement
import catx.feitu.darknya.module.AbstractModule
import dev.luna5ama.trollhack.module.modules.client.CommandSetting
import dev.luna5ama.trollhack.util.PlayerProfile
import dev.luna5ama.trollhack.util.Wrapper
import dev.luna5ama.trollhack.util.threads.ConcurrentScope
import dev.luna5ama.trollhack.util.threads.toSafe
import kotlinx.coroutines.launch
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.math.BlockPos
import java.io.File

abstract class ClientCommand(
    name: String,
    alias: Array<out String> = emptyArray(),
    description: String = "No description",
) : CommandBuilder<catx.feitu.darknya.event.ClientExecuteEvent>(name, alias, description), AlwaysListening {

    val prefixName get() = "$prefix$name"

    @CommandBuilder
    protected inline fun AbstractArg<*>.module(
        name: String,
        block: BuilderBlock<catx.feitu.darknya.module.AbstractModule>
    ) {
        arg(ModuleArg(name), block)
    }

    @CommandBuilder
    protected inline fun AbstractArg<*>.hudElement(
        name: String,
        block: BuilderBlock<AbstractHudElement>
    ) {
        arg(HudElementArg(name), block)
    }

    @CommandBuilder
    protected inline fun AbstractArg<*>.block(
        name: String,
        block: BuilderBlock<Block>
    ) {
        arg(BlockArg(name), block)
    }

    @CommandBuilder
    protected inline fun AbstractArg<*>.item(
        name: String,
        block: BuilderBlock<Item>
    ) {
        arg(ItemArg(name), block)
    }

    @CommandBuilder
    protected inline fun AbstractArg<*>.player(
        name: String,
        block: BuilderBlock<PlayerProfile>
    ) {
        arg(PlayerArg(name), block)
    }

    @CommandBuilder
    protected inline fun AbstractArg<*>.blockPos(
        name: String,
        block: BuilderBlock<BlockPos>
    ) {
        arg(BlockPosArg(name), block)
    }

    @CommandBuilder
    protected inline fun AbstractArg<*>.baritoneBlock(
        name: String,
        block: BuilderBlock<Block>
    ) {
        arg(BaritoneBlockArg(name), block)
    }

    @CommandBuilder
    protected inline fun AbstractArg<*>.schematic(
        name: String,
        file: BuilderBlock<File>
    ) {
        arg(SchematicArg(name), file)
    }

    @CommandBuilder
    protected fun AbstractArg<*>.executeAsync(
        description: String = "No description",
        block: ExecuteBlock<catx.feitu.darknya.event.ClientExecuteEvent>
    ) {
        val asyncExecuteBlock: ExecuteBlock<catx.feitu.darknya.event.ClientExecuteEvent> = {
            ConcurrentScope.launch { block() }
        }
        this.execute(description, block = asyncExecuteBlock)
    }

    @CommandBuilder
    protected fun AbstractArg<*>.executeSafe(
        description: String = "No description",
        block: ExecuteBlock<catx.feitu.darknya.event.SafeExecuteEvent>
    ) {
        val safeExecuteBlock: ExecuteBlock<catx.feitu.darknya.event.ClientExecuteEvent> = {
            toSafe()?.block()
        }
        this.execute(description, block = safeExecuteBlock)
    }

    protected companion object {
        val mc = Wrapper.minecraft
        val prefix: String get() = CommandSetting.prefix
    }

}