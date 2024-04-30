package dev.luna5ama.trollhack.util.threads

import catx.feitu.darknya.event.ClientExecuteEvent
import catx.feitu.darknya.event.SafeClientEvent
import catx.feitu.darknya.event.SafeExecuteEvent
import kotlinx.coroutines.CompletableDeferred
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun catx.feitu.darknya.event.ClientExecuteEvent.toSafe() =
    if (world != null && player != null && playerController != null && connection != null) catx.feitu.darknya.event.SafeExecuteEvent(
        world,
        player,
        playerController,
        connection,
        this
    )
    else null

@OptIn(ExperimentalContracts::class)
inline fun <R> runSafeOrElse(defaultValue: R, block: catx.feitu.darknya.event.SafeClientEvent.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    val instance = catx.feitu.darknya.event.SafeClientEvent.instance
    return if (instance != null) {
        block.invoke(instance)
    } else {
        defaultValue
    }
}

@OptIn(ExperimentalContracts::class)
inline fun runSafeOrFalse(block: catx.feitu.darknya.event.SafeClientEvent.() -> Boolean): Boolean {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    val instance = catx.feitu.darknya.event.SafeClientEvent.instance
    return if (instance != null) {
        block.invoke(instance)
    } else {
        false
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <R> runSafe(block: catx.feitu.darknya.event.SafeClientEvent.() -> R): R? {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    val instance = catx.feitu.darknya.event.SafeClientEvent.instance
    return if (instance != null) {
        block.invoke(instance)
    } else {
        null
    }
}

suspend fun <R> runSafeSuspend(block: suspend catx.feitu.darknya.event.SafeClientEvent.() -> R): R? {
    return catx.feitu.darknya.event.SafeClientEvent.instance?.let { block(it) }
}

fun <T> onMainThreadSafe(block: catx.feitu.darknya.event.SafeClientEvent.() -> T): CompletableDeferred<T?> {
    return onMainThread { catx.feitu.darknya.event.SafeClientEvent.instance?.block() }
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
