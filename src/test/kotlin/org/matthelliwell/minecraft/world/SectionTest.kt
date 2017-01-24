package org.matthelliwell.minecraft.world

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.matthelliwell.minecraft.blocks.SimpleBlock

class SectionTest {
    @Test
    fun shouldCreateSectionFromTag() {
        // givem
        val ycoord = 10
        val section = Section(ycoord)
        section.setBlock(1, 2, 3, SimpleBlock.GOLD_BLOCK)
        section.setBlock(2, 3, 4, SimpleBlock.DIAMOND_BLOCK)
        section.setBlock(3, 4, 5, SimpleBlock.GLASS)
        section.setBlock(4, 5, 6, SimpleBlock.GRASS)

        section.setSkyLight(5, 6, 7, 1.toByte())
        section.setSkyLight(6, 7, 8, 2.toByte())
        section.setSkyLight(7, 8, 9, 3.toByte())

        val tag = section.tag

        // when
        val result = Section(tag)

        // then
        assertThat(result, `is`(section))
    }

}