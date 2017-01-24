package org.matthelliwell.minecraft.blocks

/**
 * The stone brick block with different structures.
 */
enum class StoneBrickBlock(private val value: Int) : IBlock {
    NORMAL(0),
    MOSSY(1),
    CRACKED(2),
    CHISELED(3);

    /**
     * {@inheritDoc}
     */
    override val blockId: Byte
        get() = Material.STONEBRICK.value.toByte()

    /**
     * {@inheritDoc}
     */
    override val blockData: Byte
        get() = value.toByte()

    /**
     * {@inheritDoc}
     */
    override val transparency: Int
        get() = Material.STONEBRICK.transparency
}
