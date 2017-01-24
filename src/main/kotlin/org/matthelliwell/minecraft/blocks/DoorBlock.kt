package org.matthelliwell.minecraft.blocks

import org.matthelliwell.minecraft.blocks.states.Facing4State
import org.matthelliwell.minecraft.blocks.states.HalfState

/**
 * The class for doors of all materials. A door consists of an upper and a lower block. Both can be
 * can be created via the makeUpper() and makeLower() methods.
 */
class DoorBlock
private constructor(private val material: DoorBlock.DoorMaterial, private val facing: Facing4State?, private val half: HalfState, private val hinge: DoorBlock.HingeSide?, private val isOpen: Boolean) : IBlock {

    /**
     * {@inheritDoc}
     */
    override val blockId: Byte
        get() = material.value.toByte()

    /**
     * {@inheritDoc}
     */
    override val blockData: Byte
        get() {
            var data: Byte = 0
            if (half === HalfState.UPPER) {
                data = data or (1 shl 3).toByte()
                if (hinge == HingeSide.RIGHT) {
                    data = data or 1
                }
            } else {
                var facingData = 0
                when (facing) {
                    Facing4State.WEST -> facingData = 0
                    Facing4State.NORTH -> facingData = 1
                    Facing4State.EAST -> facingData = 2
                    Facing4State.SOUTH -> facingData = 3
                }
                data = data or (facingData and 0x3).toByte()
                if (isOpen) {
                    data = data or (1 shl 2).toByte()
                }
            }
            return data
        }

    /**
     * {@inheritDoc}
     */
    override val transparency: Int
        get() = 1

    /**
     * The site on which the hinge is.
     */
    enum class HingeSide {
        LEFT,
        RIGHT
    }

    /**
     * The material of which the door consists.
     */
    enum class DoorMaterial constructor(private val material: Material) {
        OAK(Material.WOODEN_DOOR),
        IRON(Material.IRON_DOOR),
        SPRUCE(Material.SPRUCE_DOOR),
        BIRCH(Material.BIRCH_DOOR),
        JUNGLE(Material.JUNGLE_DOOR),
        ACACIA(Material.ACACIA_DOOR),
        DARK_OAK(Material.DARK_OAK_DOOR);

        val value: Int
            get() = material.value
    }

    companion object {
        /**
         * Creates the upper part of a door.

         * @param material The material of which the door consists
         * *
         * @param hingeSide Whether the hingeSide is on the left or right side
         * *
         * @return A new door block
         */
        fun makeUpper(material: DoorMaterial, hingeSide: HingeSide): DoorBlock {
            return DoorBlock(material, null, HalfState.UPPER, hingeSide, false)
        }

        /**
         * Creates the lower part of a door.

         * @param material The material of which the door consists
         * *
         * @param facing The direction in which the door is facing
         * *
         * @param isOpen Whether the door is open
         * *
         * @return A new door block
         */
        fun makeLower(material: DoorMaterial, facing: Facing4State, isOpen: Boolean): DoorBlock {
            return DoorBlock(material, facing, HalfState.LOWER, null, isOpen)
        }
    }
}
