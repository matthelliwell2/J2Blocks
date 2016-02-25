package net.morbz.minecraft.world;

import net.morbz.minecraft.blocks.SimpleBlock;
import org.jnbt.Tag;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ChunkTest {
    @Test
    public void shouldCreateChunkFromTag() {
        // given
        final Region parent = mock(Region.class);
        final Chunk chunk = new Chunk(parent, 1, 2, null);
        chunk.setBlock(0, 0, 0, SimpleBlock.GLASS);
        chunk.setBlock(1, 10, 1, SimpleBlock.GLASS);
        chunk.setBlock(2, 20, 2, SimpleBlock.DIAMOND_BLOCK);
        chunk.setBlock(3, 30, 3, SimpleBlock.BRICK_BLOCK);

        chunk.addSkyLight();
        chunk.calculateHeightMap();

        final Tag tag = chunk.getTag();

        // when
        final Chunk result = new Chunk(parent, tag);

        // then
        assertThat(result, is(chunk));

    }

}