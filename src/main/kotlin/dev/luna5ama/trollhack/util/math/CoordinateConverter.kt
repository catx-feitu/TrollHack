package dev.luna5ama.trollhack.util.math

import dev.luna5ama.trollhack.manager.managers.WaypointManager
import net.minecraft.util.math.BlockPos

object CoordinateConverter {
    /**
     * More efficient impl of [BlockPos.toString]
     */
    fun BlockPos.asString(): String {
        return "${this.x}, ${this.y}, ${this.z}"
    }

    fun toCurrent(dimension: Int, pos: BlockPos): BlockPos {
        return if (dimension == WaypointManager.genDimension()) {
            pos
        } else {
            when (dimension) {
                -1 -> toOverworld(pos) // Nether to overworld
                0 -> toNether(pos) // Overworld to nether
                else -> pos // End or custom dimension by server
            }
        }
    }

    fun bothConverted(dimension: Int, pos: BlockPos): String {
        return when (dimension) {
            -1 -> "${toOverworld(pos).asString()} (${pos.asString()})"
            0 -> "${pos.asString()} (${toNether(pos).asString()})"
            else -> pos.asString()
        }
    }

    fun toNether(pos: BlockPos): BlockPos {
        return BlockPos(pos.x / 8, pos.y, pos.z / 8)
    }

    fun toOverworld(pos: BlockPos): BlockPos {
        return BlockPos(pos.x * 8, pos.y, pos.z * 8)
    }
}