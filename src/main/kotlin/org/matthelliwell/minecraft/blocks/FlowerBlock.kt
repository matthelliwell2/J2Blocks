package org.matthelliwell.minecraft.blocks

enum class FlowerBlock private constructor(private val material: Material, override val blockData: Byte) : IBlock {

    DANDELION(Material.YELLOW_FLOWER, 0.toByte()),
    POPPY(Material.RED_FLOWER, 0.toByte()),
    BLUE_ORCHID(Material.RED_FLOWER, 1.toByte()),
    ALLIUM(Material.RED_FLOWER, 2.toByte()),
    AZURE_BLUET(Material.RED_FLOWER, 3.toByte()),
    RED_TULIP(Material.RED_FLOWER, 4.toByte()),
    ORANGE_TULIP(Material.RED_FLOWER, 5.toByte()),
    WHITE_TULIP(Material.RED_FLOWER, 6.toByte()),
    PINK_TULIP(Material.RED_FLOWER, 7.toByte()),
    OXEYE_DAISY(Material.RED_FLOWER, 8.toByte());

    override val blockId: Byte
        get() = this.material.value.toByte()

    override val transparency: Int
        get() = this.material.transparency.toByte().toInt()

}
