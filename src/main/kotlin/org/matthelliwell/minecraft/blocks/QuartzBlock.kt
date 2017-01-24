package org.matthelliwell.minecraft.blocks

/**
 * The class for a quartz block. There are two different constructors depending on what variant of
 * the quartz block is needed.
 */
class QuartzBlock : IBlock {
    private val value: Int

    /**
     * Creates a new instance. This constructor is used for the basic variants default and chiseled
     * that don't have facing data.

     * @param variant The variant of the quartz block
     */
    constructor(variant: QuartzVariant) {
        value = variant.value
    }

    /**
     * Creates a new instance. This constructor is used for the pillar variant which is the only
     * variant that has facing data.

     * @param facing The facing of the pillar quartz block
     */
    constructor(facing: QuartzFacing) {
        value = facing.value
    }

    /**
     * {@inheritDoc}
     */
    override val blockId: Byte
        get() = Material.QUARTZ_BLOCK.value.toByte()

    /**
     * {@inheritDoc}
     */
    override val blockData: Byte
        get() = value.toByte()

    /**
     * {@inheritDoc}
     */
    override val transparency: Int
        get() = Material.QUARTZ_BLOCK.transparency

    /**
     * The variant of the quartz block. Except pillar which is a special type because it has facing
     * data.
     */
    enum class QuartzVariant constructor(val value: Int) {
        DEFAULT(0),
        CHISELED(1)
    }

    /**
     * The facing of the quartz block. This is only used for the pillar variant.
     */
    enum class QuartzFacing constructor(val value: Int) {
        VERTICAL(2),
        NORTH_SOUTH(3),
        EAST_WEST(4)
    }
}
