package org.matthelliwell.minecraft.blocks

import org.matthelliwell.minecraft.blocks.states.WoodState

/**
 * The class for wooden planks.
 */
class PlanksBlock(private val wood: WoodState) : IBlock {

    /**
     * {@inheritDoc}
     */
    override val blockId: Byte
        get() = Material.PLANKS.value.toByte()

    /**
     * {@inheritDoc}
     */
    override val blockData: Byte
        get() = wood.value.toByte()

    /**
     * {@inheritDoc}
     */
    override val transparency: Int
        get() = Material.PLANKS.transparency
}
