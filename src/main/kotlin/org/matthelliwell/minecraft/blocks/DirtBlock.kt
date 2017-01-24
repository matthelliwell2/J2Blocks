package org.matthelliwell.minecraft.blocks

/**
 * The class for the dirt and podzol blocks.
 */
enum class DirtBlock constructor(private val value: Int) : IBlock {
    DIRT(0),
    COARSE_DIRT(1),
    PODZOL(2);

    /**
     * {@inheritDoc}
     */
    override val blockId: Byte
        get() = Material.DIRT.value.toByte()

    /**
     * {@inheritDoc}
     */
    override val blockData: Byte
        get() = value.toByte()

    /**
     * {@inheritDoc}
     */
    override val transparency: Int
        get() = Material.DIRT.transparency
}
