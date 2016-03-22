package net.morbz.minecraft.world;

import java.io.IOException;

import net.morbz.minecraft.blocks.IBlock;
import net.morbz.minecraft.blocks.SaplingBlock;
import net.morbz.minecraft.level.Level;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class WorldTest {
    private World testSubject;

    @Before
    public void setUp() throws IOException {
        final Level level = new Level("test");
        testSubject = new World(level, null, false);
    }
    @Test
    public void shouldCalculateHeightMap() {
        testSubject.setBlocks(327981, 253119, getBlocks(200));
        testSubject.setBlocks(327981, 253117, getBlocks(200));
        testSubject.setBlocks(327980, 253118, getBlocks(200));
        testSubject.setBlocks(327982, 253118, getBlocks(200));
        for (int i = 1; i < 255; ++i) {
            // given
            testSubject.setBlocks(327981, 253118, getBlocks(i));

            // when
            final int result = testSubject.getHighestBlock(327981, 253118);

            // then
            assertThat(result, is(i));
        }
    }

    private static IBlock[] getBlocks(final int size) {
        final IBlock[] result = new IBlock[size];
        for (int i = 0; i < size; ++i) {
            result[i] = SaplingBlock.BIRCH_SAPLING;
        }

        return result;
    }
}