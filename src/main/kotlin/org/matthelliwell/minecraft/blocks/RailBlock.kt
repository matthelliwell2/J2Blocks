package org.matthelliwell.minecraft.blocks

import org.matthelliwell.minecraft.blocks.states.Facing2State
import org.matthelliwell.minecraft.blocks.states.Facing4State

/**
 * The class for basic rails. There are straight, curved and sloped rails that can be created via
 * the makeStraight(), makeCurved() and makeSloped() methods.
 */
class RailBlock private constructor(private val value: Int) : IBlock {

    /**
     * {@inheritDoc}
     */
    override val blockId: Byte
        get() = Material.RAIL.value.toByte()

    /**
     * {@inheritDoc}
     */
    override val blockData: Byte
        get() = value.toByte()

    /**
     * {@inheritDoc}
     */
    override val transparency: Int
        get() = Material.RAIL.transparency

    enum class RailCurve constructor(val value: Int) {
        SOUTH_EAST(6),
        SOUTH_WEST(7),
        NORTH_WEST(8),
        NORTH_EAST(9)
    }

    companion object {

        /**
         * Creates straight, flat rails going either in north/south or east/west direction.

         * @param facing The direction of the rails
         * *
         * @return A new rails block
         */
        fun makeStraight(facing: Facing2State): RailBlock {
            // Facing direction
            var value = 0
            when (facing) {
                Facing2State.NORTH_SOUTH -> value = 0
                Facing2State.EAST_WEST -> value = 1
            }
            return RailBlock(value)
        }

        /**
         * Creates straight rails that are sloped.

         * @param facing The direction in which the rails are ascending
         * *
         * @return A new rails block
         */
        fun makeSloped(facing: Facing4State): RailBlock {
            // Ascending direction
            var value = 0
            when (facing) {
                Facing4State.EAST -> value = 2
                Facing4State.WEST -> value = 3
                Facing4State.NORTH -> value = 4
                Facing4State.SOUTH -> value = 5
            }
            return RailBlock(value)
        }

        /**
         * Creates curved, flat rails.

         * @param curve The type of the curve
         * *
         * @return A new rails block
         */
        fun makeCurved(curve: RailCurve): RailBlock {
            return RailBlock(curve.value)
        }
    }
}
