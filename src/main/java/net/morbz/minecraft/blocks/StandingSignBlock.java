package net.morbz.minecraft.blocks;

public class StandingSignBlock implements IBlock {

    @Override
    public byte getBlockId() {
        return (byte)Material.STANDING_SIGN.getValue();
    }

    @Override
    public byte getBlockData() {
        return 0;
    }

    @Override
    public int getTransparency() {
        return Material.STANDING_SIGN.getTransparency();
    }
}
