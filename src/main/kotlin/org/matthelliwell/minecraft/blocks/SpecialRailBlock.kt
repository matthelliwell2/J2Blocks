package org.matthelliwell.minecraft.blocks

import org.matthelliwell.minecraft.blocks.states.Facing2State
import org.matthelliwell.minecraft.blocks.states.Facing4State

/**
 * The class for special rails (powered, detector and activator). There are straight and sloped
 * rails that can be created via the makeStraight() and makeSloped() methods. Special rails cannot
 * be curved.
 */
class SpecialRailBlock(private val material: SpecialRailBlock.SpecialRailMaterial, private val isActive: Boolean, private val value: Int) : IBlock {

    /**
     * {@inheritDoc}
     */
    override val blockId: Byte
        get() = material.value.toByte()

    /**
     * {@inheritDoc}
     */
    override // Is active?
    val blockData: Byte
        get() {
            var data = value.toByte()
            if (isActive) {
                data = data or (1 shl 3).toByte()
            }
            return data
        }

    /**
     * {@inheritDoc}
     */
    override val transparency: Int
        get() = 1

    /**
     * The type of the special rail.
     */
    enum class SpecialRailMaterial(private val material: Material) {
        POWERED(Material.GOLDEN_RAIL),
        DETECTOR(Material.DETECTOR_RAIL),
        ACTIVATOR(Material.ACTIVATOR_RAIL);

        val value: Int
            get() = material.value
    }

    companion object {
        /**
         * Creates straight, flat rails going either in north/south or east/west direction.
         *
         * @param material The type of the special rails
         * *
         * @param facing The direction of the rails
         * *
         * @param isActive Whether the rails are active
         * *
         * @return A new special rails block
         */
        fun makeStraight(material: SpecialRailMaterial, facing: Facing2State, isActive: Boolean): SpecialRailBlock {
            // Facing direction
            var value = 0
            when (facing) {
                Facing2State.NORTH_SOUTH -> value = 0
                Facing2State.EAST_WEST -> value = 1
            }
            return SpecialRailBlock(material, isActive, value)
        }

        /**
         * Creates straight rails that are sloped.

         * @param material The type of the special rails
         * *
         * @param facing The direction in which the rails are ascending
         * *
         * @param isActive Whether the rails are active
         * *
         * @return A new special rails block
         */
        fun makeSloped(material: SpecialRailMaterial, facing: Facing4State, isActive: Boolean): SpecialRailBlock {
            // Ascending direction
            var value = 0
            when (facing) {
                Facing4State.EAST -> value = 2
                Facing4State.WEST -> value = 3
                Facing4State.NORTH -> value = 4
                Facing4State.SOUTH -> value = 5
            }
            return SpecialRailBlock(material, isActive, value)
        }
    }
}
