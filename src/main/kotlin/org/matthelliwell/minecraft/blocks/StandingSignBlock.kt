package org.matthelliwell.minecraft.blocks

class StandingSignBlock : IBlock {

    override val blockId: Byte
        get() = Material.STANDING_SIGN.value.toByte()

    override val blockData: Byte
        get() = 0

    override val transparency: Int
        get() = Material.STANDING_SIGN.transparency
}
