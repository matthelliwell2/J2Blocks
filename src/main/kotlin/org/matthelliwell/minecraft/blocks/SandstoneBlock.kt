package org.matthelliwell.minecraft.blocks

/**
 * The class for sandstone blocks with different structures.
 */
enum class SandstoneBlock(private val value: Int) : IBlock {
    NORMAL(0),
    CHISELED(1),
    SMOOTH(2);

    /**
     * {@inheritDoc}
     */
    override val blockId: Byte
        get() = Material.SANDSTONE.value.toByte()

    /**
     * {@inheritDoc}
     */
    override val blockData: Byte
        get() = value.toByte()

    /**
     * {@inheritDoc}
     */
    override val transparency: Int
        get() = Material.SANDSTONE.transparency
}
