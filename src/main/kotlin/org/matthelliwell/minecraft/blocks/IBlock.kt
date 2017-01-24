package org.matthelliwell.minecraft.blocks

/**
 * Interface for all blocks.
 */
interface IBlock {
    /**
     * Returns the block ID. That is the basic ID of the material without additional data.

     * @return The block ID
     */
    val blockId: Byte

    /**
     * Returns the block data. It can hold additional information about the block depending on the
     * material.

     * @return The block data. Only the 4 rightmost bits are relevant.
     */
    val blockData: Byte

    /**
     * Returns the transparency level of this block. 0 means fully opaque, 1 means fully transparent
     * and values > 1 mean transparent but the light level is decreased by n at this block.

     * @return The transparency level
     */
    val transparency: Int
}
