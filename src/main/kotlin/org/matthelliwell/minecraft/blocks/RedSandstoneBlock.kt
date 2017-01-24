package org.matthelliwell.minecraft.blocks

enum class RedSandstoneBlock(override val blockData: Byte) : IBlock {

    RED_SANDSTONE(0.toByte()),
    CHISELED_RED_SANDSTONE(1.toByte()),
    SMOOTH_RED_SANDSTONE(2.toByte());

    override val blockId: Byte
        get() = Material.RED_SANDSTONE.value.toByte()

    override val transparency: Int
        get() = Material.RED_SANDSTONE.transparency.toByte().toInt()

}
