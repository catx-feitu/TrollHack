package dev.luna5ama.trollhack.util.threads

import dev.luna5ama.trollhack.event.ClientExecuteEvent
import dev.luna5ama.trollhack.event.SafeClientEvent
import dev.luna5ama.trollhack.event.SafeExecuteEvent
import kotlinx.coroutines.CompletableDeferred
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun ClientExecuteEvent.toSafe() =
    if (world != null && player != null && playerController != null && connection != null) SafeExecuteEvent(
        world,
        player,
        playerController,
        connection,
        this
    )
    else null

@OptIn(ExperimentalContracts::class)
inline fun <R> runSafeOrElse(defaultValue: R, block: SafeClientEvent.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    val instance = SafeClientEvent.instance
    return if (instance != null) {
        block.invoke(instance)
    } else {
        defaultValue
    }
}

@OptIn(ExperimentalContracts::class)
inline fun runSafeOrFalse(block: SafeClientEvent.() -> Boolean): Boolean {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    val instance = SafeClientEvent.instance
    return if (instance != null) {
        block.invoke(instance)
    } else {
        false
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <R> runSafe(block: SafeClientEvent.() -> R): R? {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    val instance = SafeClientEvent.instance
    return if (instance != null) {
        block.invoke(instance)
    } else {
        null
    }
}

suspend fun <R> runSafeSuspend(block: suspend SafeClientEvent.() -> R): R? {
    return SafeClientEvent.instance?.let { block(it) }
}

fun <T> onMainThreadSafe(block: SafeClientEvent.() -> T): CompletableDeferred<T?> {
    return onMainThread { SafeClientEvent.instance?.block() }
}

/**
 * Runs [block] on Minecraft main thread (Client thread)
 *
 * @return [CompletableDeferred] callback
 *
 * @see [onMainThread]
 */
fun <T> onMainThread(block: () -> T): CompletableDeferred<T> {
    return MainThreadExecutor.add(block)
}

inline fun <T : Any, R> T.runSynchronized(block: T.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return synchronized(this) {
        block.invoke(this)
    }
}
