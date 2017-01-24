package org.matthelliwell.minecraft.blocks

/**
 * A custom block for testing purposes and internal functions. The block ID and block data can be
 * set freely.
 */
class CustomBlock (val blockIdInt: Byte, val blockDataInt: Int, val transparencyVal : Int) : IBlock {

    override val blockId: Byte
        get() = blockIdInt

    override val blockData: Byte
        get() = blockIdInt

    override val transparency: Int
        get() = transparencyVal
}
