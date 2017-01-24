package org.matthelliwell.minecraft.blocks

import org.matthelliwell.minecraft.blocks.states.Facing5State

/**
 * A redstone torch for both states active and inactive.
 *
 * @param isActive Whether the torch active
 *
 * @param facing The direction in which the torch is facing
 */
class RedstoneTorchBlock (private val isActive: Boolean, private val facing: Facing5State) : IBlock {

    /**
     * {@inheritDoc}
     */
    override val blockId: Byte
        get() {
            val material = if (isActive) Material.REDSTONE_TORCH else Material.UNLIT_REDSTONE_TORCH
            return material.value.toByte()
        }

    /**
     * {@inheritDoc}
     */
    override val blockData: Byte
        get() {
            var data: Byte = 0
            when (facing) {
                Facing5State.EAST -> data = 1
                Facing5State.WEST -> data = 2
                Facing5State.SOUTH -> data = 3
                Facing5State.NORTH -> data = 4
                Facing5State.UP -> data = 5
            }
            return data
        }

    /**
     * {@inheritDoc}
     */
    override val transparency: Int
        get() {
            val material = if (isActive) Material.REDSTONE_TORCH else Material.UNLIT_REDSTONE_TORCH
            return material.transparency
        }
}
