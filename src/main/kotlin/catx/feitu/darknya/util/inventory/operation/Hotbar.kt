package dev.luna5ama.trollhack.util.inventory.operation

import catx.feitu.darknya.event.SafeClientEvent
import catx.feitu.darknya.manager.managers.InventoryTaskManager
import dev.luna5ama.trollhack.util.accessor.syncCurrentPlayItem
import dev.luna5ama.trollhack.util.inventory.inventoryTaskNow
import dev.luna5ama.trollhack.util.inventory.slot.*
import net.minecraft.block.Block
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.util.function.Predicate

/**
 * Try to swap selected hotbar slot to [block] that matches with [predicateItem]
 *
 * Or move an item from storage slot to an empty slot or slot that matches [predicateSlot]
 * or slot 0 if none
 */
fun catx.feitu.darknya.event.SafeClientEvent.swapToBlockOrMove(
    block: Block,
    predicateItem: Predicate<ItemStack>? = null,
    predicateSlot: Predicate<ItemStack>? = null
): Boolean {
    return if (swapToBlock(block, predicateItem)) {
        true
    } else {
        player.storageSlots.firstBlock(block, predicateItem)?.let {
            val slotTo = player.anyHotbarSlot(predicateSlot)
            inventoryTaskNow {
                swapWith(it, slotTo)
            }
            true
        } ?: false
    }
}

/**
 * Try to swap selected hotbar slot to [I] that matches with [predicateItem]
 *
 * Or move an item from storage slot to an empty slot or slot that matches [predicateSlot]
 * or slot 0 if none
 */
inline fun <reified I : Item> catx.feitu.darknya.event.SafeClientEvent.swapToItemOrMove(
    predicateItem: Predicate<ItemStack>? = null,
    predicateSlot: Predicate<ItemStack>? = null
): Boolean {
    return if (swapToItem<I>(predicateItem)) {
        true
    } else {
        player.storageSlots.firstItem<I, Slot>(predicateItem)?.let {
            val slotTo = player.anyHotbarSlot(predicateSlot)
            inventoryTaskNow {
                swapWith(it, slotTo)
            }
            true
        } ?: false
    }
}

/**
 * Try to swap selected hotbar slot to [item] that matches with [predicateItem]
 *
 * Or move an item from storage slot to an empty slot or slot that matches [predicateSlot]
 * or slot 0 if none
 */
fun catx.feitu.darknya.event.SafeClientEvent.swapToItemOrMove(
    item: Item,
    predicateItem: Predicate<ItemStack>? = null,
    predicateSlot: Predicate<ItemStack>? = null
): Boolean {
    return if (swapToItem(item, predicateItem)) {
        true
    } else {
        player.storageSlots.firstItem(item, predicateItem)?.let {
            val slotTo = player.anyHotbarSlot(predicateSlot)
            inventoryTaskNow {
                swapWith(it, slotTo)
            }
            true
        } ?: false
    }
}


/**
 * Try to swap selected hotbar slot to [B] that matches with [predicate]
 */
inline fun <reified B : Block> catx.feitu.darknya.event.SafeClientEvent.swapToBlock(predicate: Predicate<ItemStack>? = null): Boolean {
    return player.hotbarSlots.firstBlock<B>(predicate)?.let {
        swapToSlot(it)
        true
    } ?: false
}

/**
 * Try to swap selected hotbar slot to [block] that matches with [predicate]
 */
fun catx.feitu.darknya.event.SafeClientEvent.swapToBlock(block: Block, predicate: Predicate<ItemStack>? = null): Boolean {
    return player.hotbarSlots.firstBlock(block, predicate)?.let {
        swapToSlot(it)
        true
    } ?: false
}

/**
 * Try to swap selected hotbar slot to [I] that matches with [predicate]
 */
inline fun <reified I : Item> catx.feitu.darknya.event.SafeClientEvent.swapToItem(predicate: Predicate<ItemStack>? = null): Boolean {
    return player.hotbarSlots.firstItem<I, HotbarSlot>(predicate)?.let {
        swapToSlot(it)
        true
    } ?: false
}

/**
 * Try to swap selected hotbar slot to [item] that matches with [predicate]
 */
fun catx.feitu.darknya.event.SafeClientEvent.swapToItem(item: Item, predicate: Predicate<ItemStack>? = null): Boolean {
    return player.hotbarSlots.firstItem(item, predicate)?.let {
        swapToSlot(it)
        true
    } ?: false
}

/**
 * Swap the selected hotbar slot to [slot]
 */
fun catx.feitu.darknya.event.SafeClientEvent.swapToSlot(slot: Slot) {
    if (slot.isHotbarSlot) {
        swapToSlot(slot.hotbarIndex)
    } else {
        val slotTo = player.anyHotbarSlot()
        inventoryTaskNow {
            swapWith(slot, slotTo)
        }
    }
}

/**
 * Swap the selected hotbar slot to [slot]
 */
fun catx.feitu.darknya.event.SafeClientEvent.swapToSlot(slot: Int) {
    if (slot !in 0..8) return
    synchronized(InventoryTaskManager) {
        player.inventory.currentItem = slot
        playerController.syncCurrentPlayItem()
    }
}