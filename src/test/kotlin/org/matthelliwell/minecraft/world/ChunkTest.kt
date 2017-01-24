package org.matthelliwell.minecraft.world

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.matthelliwell.minecraft.blocks.SimpleBlock
import org.mockito.Mockito.mock

class ChunkTest {
    @Test
    fun shouldCreateChunkFromTag() {
        // given
        val parent = mock(Region::class.java)
        val chunk = Chunk(parent, 1, 2, null)
        chunk.setBlock(0, 0, 0, SimpleBlock.GLASS)
        chunk.setBlock(1, 10, 1, SimpleBlock.GLASS)
        chunk.setBlock(2, 20, 2, SimpleBlock.DIAMOND_BLOCK)
        chunk.setBlock(3, 30, 3, SimpleBlock.BRICK_BLOCK)

        val tag = chunk.tag

        // when
        val result = Chunk(parent, tag)

        // then
        assertThat(result, `is`(chunk))
    }
}