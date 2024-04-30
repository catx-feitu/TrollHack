package dev.luna5ama.trollhack.util.combat

import dev.luna5ama.trollhack.event.AlwaysListening
import catx.feitu.darknya.event.SafeClientEvent
import dev.luna5ama.trollhack.event.events.ConnectionEvent
import dev.luna5ama.trollhack.event.events.TickEvent
import dev.luna5ama.trollhack.event.listener
import dev.luna5ama.trollhack.event.safeParallelListener
import catx.feitu.darknya.manager.managers.EntityManager
import dev.luna5ama.trollhack.util.inventory.attackDamage
import dev.luna5ama.trollhack.util.inventory.operation.swapToSlot
import dev.luna5ama.trollhack.util.inventory.slot.filterByStack
import dev.luna5ama.trollhack.util.inventory.slot.hotbarSlots
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.MobEffects
import net.minecraft.item.ItemAxe
import net.minecraft.item.ItemSword
import net.minecraft.item.ItemTool
import net.minecraft.util.CombatRules
import net.minecraft.util.DamageSource
import java.util.*
import kotlin.math.max
import kotlin.math.round

object CombatUtils : AlwaysListening {
    private val cachedArmorValues = WeakHashMap<EntityLivingBase, ArmorInfo>()

    fun catx.feitu.darknya.event.SafeClientEvent.calcDamageFromPlayer(entity: EntityPlayer, assumeCritical: Boolean = false): Float {
        val itemStack = entity.heldItemMainhand
        var damage = itemStack.attackDamage
        if (assumeCritical) damage *= 1.5f

        return calcDamage(player, damage)
    }

    fun catx.feitu.darknya.event.SafeClientEvent.calcDamageFromMob(entity: EntityMob): Float {
        var damage = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).attributeValue.toFloat()
        damage += EnchantmentHelper.getModifierForCreature(entity.heldItemMainhand, entity.creatureAttribute)

        return calcDamage(player, damage)
    }

    fun calcDamage(
        entity: EntityLivingBase,
        damageIn: Float = 100f,
        source: DamageSource = DamageSource.GENERIC,
        roundDamage: Boolean = false
    ): Float {
        val armorInfo = cachedArmorValues[entity] ?: return 0.0f
        var damage = CombatRules.getDamageAfterAbsorb(damageIn, armorInfo.armorValue, armorInfo.toughness)

        if (source != DamageSource.OUT_OF_WORLD) {
            entity.getActivePotionEffect(MobEffects.RESISTANCE)?.let {
                damage *= max(1.0f - (it.amplifier + 1) * 0.2f, 0.0f)
            }
        }

        damage *= getProtectionModifier(entity, source)

        return if (roundDamage) round(damage) else damage
    }

    private fun getProtectionModifier(entity: EntityLivingBase, damageSource: DamageSource): Float {
        var modifier = 0

        for (armor in entity.armorInventoryList.toList()) {
            if (armor.isEmpty) continue // Skip if item stack is empty
            val nbtTagList = armor.enchantmentTagList
            for (i in 0 until nbtTagList.tagCount()) {
                val compoundTag = nbtTagList.getCompoundTagAt(i)

                val id = compoundTag.getInteger("id")
                val level = compoundTag.getInteger("lvl")

                Enchantment.getEnchantmentByID(id)?.let {
                    modifier += it.calcModifierDamage(level, damageSource)
                }
            }
        }

        modifier = modifier.coerceIn(0, 20)

        return 1.0f - modifier / 25.0f
    }

    fun catx.feitu.darknya.event.SafeClientEvent.equipBestWeapon(preferWeapon: PreferWeapon = PreferWeapon.NONE, allowTool: Boolean = false) {
        player.hotbarSlots.filterByStack {
            val item = it.item
            item is ItemSword || item is ItemAxe || allowTool && item is ItemTool
        }.maxByOrNull {
            val itemStack = it.stack
            val item = itemStack.item
            val damage = itemStack.attackDamage

            when {
                preferWeapon == PreferWeapon.SWORD && item is ItemSword -> damage * 10.0f
                preferWeapon == PreferWeapon.AXE && item is ItemAxe -> damage * 10.0f
                else -> damage
            }
        }?.let {
            swapToSlot(it)
        }
    }

    val EntityLivingBase.scaledHealth: Float
        get() = this.health + this.absorptionAmount * (this.health / this.maxHealth)

    val EntityLivingBase.totalHealth: Float
        get() = this.health + this.absorptionAmount

    init {
        safeParallelListener<TickEvent.Post> {
            for (entity in EntityManager.entity) {
                if (entity !is EntityLivingBase) continue
                val armorValue = entity.totalArmorValue.toFloat()
                val toughness =
                    entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).attributeValue.toFloat()

                cachedArmorValues.getOrPut(entity, ::ArmorInfo).update(armorValue, toughness)
            }
        }

        listener<ConnectionEvent.Disconnect> {
            cachedArmorValues.clear()
        }
    }

    private class ArmorInfo {
        var armorValue = 0.0f; private set
        var toughness = 0.0f; private set

        fun update(armorValue: Float, toughness: Float) {
            this.armorValue = armorValue
            this.toughness = toughness
        }
    }

    enum class PreferWeapon {
        SWORD, AXE, NONE
    }
}