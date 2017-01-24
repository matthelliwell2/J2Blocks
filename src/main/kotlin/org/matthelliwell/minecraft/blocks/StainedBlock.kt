package org.matthelliwell.minecraft.blocks

/**
 * A class for all blocks that have a stained version (glass, wool, carpet, clay).
 *
 * @param material The material of the stained block
 *
 * @param color The color of the block
 */
class StainedBlock(private val material: StainedBlock.StainedMaterial, private val color: StainedBlock.StainedColor) : IBlock {

    /**
     * {@inheritDoc}
     */
    override val blockId: Byte
        get() = material.material.value.toByte()

    /**
     * {@inheritDoc}
     */
    override val blockData: Byte
        get() = color.value.toByte()

    /**
     * {@inheritDoc}
     */
    override val transparency: Int
        get() = material.transparency

    /**
     * The material of the stained block.
     */
    class StainedMaterial private constructor(var material: Material, var transparency: Int) {
        companion object {

            val WOOL = StainedMaterial(Material.WOOL, 0)
            val GLASS = StainedMaterial(Material.STAINED_GLASS, 1)
            val CLAY = StainedMaterial(Material.STAINED_HARDENED_CLAY, 0)
            val GLASS_PANE = StainedMaterial(Material.STAINED_GLASS_PANE, 1)
            val CARPET = StainedMaterial(Material.CARPET, 1)
        }
    }

    /**
     * The color of the stained block.
     */
    enum class StainedColor private constructor(val value: Int) {
        WHITE(0),
        ORANGE(1),
        MAGENTA(2),
        LIGHT_BLUE(3),
        YELLOW(4),
        LIME(5),
        PINK(6),
        GRAY(7),
        LIGHT_GRAY(8),
        CYAN(9),
        PURPLE(10),
        BLUE(11),
        BROWN(12),
        GREEN(13),
        RED(14),
        BLACK(15)
    }
}
