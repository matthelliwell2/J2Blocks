package org.matthelliwell.minecraft.blocks

enum class SaplingBlock(override val blockData: Byte) : IBlock {

    OAK_SAPLING(0.toByte()),
    SPRUCE_SAPLING(1.toByte()),
    BIRCH_SAPLING(2.toByte()),
    JUNGLE_SAPLING(3.toByte()),
    ACACIA_SAPLING(4.toByte()),
    DARK_OAK_SAPLING(5.toByte());

    override val blockId: Byte
        get() = Material.SAPLING.value.toByte()

    override val transparency: Int
        get() = Material.SAPLING.transparency.toByte().toInt()

}
