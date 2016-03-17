package net.morbz.minecraft.world;

import net.morbz.minecraft.blocks.SimpleBlock;
import org.jnbt.Tag;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SectionTest {
    @Test
    public void shouldCreateSectionFromTag() {
        // givem
        final int ycoord = 10;
        final Section section = new Section(ycoord);
        section.setBlock(1, 2, 3, SimpleBlock.GOLD_BLOCK);
        section.setBlock(2, 3, 4, SimpleBlock.DIAMOND_BLOCK);
        section.setBlock(3, 4, 5, SimpleBlock.GLASS);
        section.setBlock(4, 5, 6, SimpleBlock.GRASS);

        section.setSkyLight(5, 6, 7, (byte)1);
        section.setSkyLight(6, 7, 8, (byte)2);
        section.setSkyLight(7, 8, 9, (byte)3);

        final Tag tag = section.getTag();

        // when
        final Section result = new Section(tag);

        // then
        assertThat(result, is(section));
    }

}