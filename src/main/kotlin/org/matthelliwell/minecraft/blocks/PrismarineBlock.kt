package org.matthelliwell.minecraft.blocks

enum class PrismarineBlock(override val blockData: Byte) : IBlock {

    PRISMARINE(0.toByte()),
    PRISMARINE_BRICK(1.toByte()),
    DARK_PRISMARINE(2.toByte());

    override val blockId: Byte
        get() = Material.PRISMARINE.value.toByte()

    override val transparency: Int
        get() = Material.PRISMARINE.value.toByte().toInt()

}
