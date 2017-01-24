package org.matthelliwell.minecraft.blocks

/**
 * The class for the stone, granite, etc.
 */
enum class StoneBlock(private val value: Int) : IBlock {
    STONE(0),
    GRANITE(1),
    SMOOTH_GRANITE(2),
    DIORITE(3),
    SMOOTH_DIORITE(4),
    ANDESITE(5),
    SMOOTH_ANDESITE(6);

    /**
     * {@inheritDoc}
     */
    override val blockId: Byte
        get() = Material.STONE.value.toByte()

    /**
     * {@inheritDoc}
     */
    override val blockData: Byte
        get() = value.toByte()

    /**
     * {@inheritDoc}
     */
    override val transparency: Int
        get() = Material.STONE.transparency
}
