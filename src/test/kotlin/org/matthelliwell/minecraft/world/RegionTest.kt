package org.matthelliwell.minecraft.world

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.matthelliwell.minecraft.blocks.IBlock
import org.matthelliwell.minecraft.blocks.SimpleBlock
import java.io.File
import java.io.IOException
import java.nio.file.Files

class RegionTest {
    @Test
    @Throws(IOException::class)
    fun shouldReadAndWriteRegionFile() {
        // given
        val regionDir = Files.createTempDirectory("J2BlocksTest")
        val regionFilePath = regionDir.resolve("r.1.2.mca")
        val regionFile = regionFilePath.toFile()

        val region = Region(1, 2, null)
        region.setBlock(1, 10, 3, SimpleBlock.GRASS)
        region.setBlock(2, 11, 4, SimpleBlock.GLASS)
        region.setBlock(3, 12, 5, SimpleBlock.BRICK_BLOCK)
        region.setBlock(4, 13, 6, SimpleBlock.COAL_ORE)
        region.setBlock(5, 14, 7, SimpleBlock.GOLD_BLOCK)

        // when
        region.writeToFile(regionFile)
        val result = Region(1, 2, null)
        result.readFromFile(regionFile)

        // then
        assertThat(result, `is`(region))

        // clean up
        Files.delete(regionFilePath)
        Files.delete(regionDir)
    }

    @Test
    @Throws(IOException::class)
    fun shouldReadComplexRegionFile() {
        // given
        // Load a provided region
        val inputFile = File("src/test/resources/r.900.22.mca")
        val inputRegion = Region(900, 22, null)
        inputRegion.readFromFile(inputFile)

        val blocks = arrayOf<IBlock>(SimpleBlock.BEDROCK, SimpleBlock.WATER, SimpleBlock.AIR, SimpleBlock.AIR, SimpleBlock.AIR, SimpleBlock.AIR, SimpleBlock.AIR, SimpleBlock.AIR, SimpleBlock.AIR, SimpleBlock.AIR, SimpleBlock.AIR, SimpleBlock.AIR, SimpleBlock.AIR, SimpleBlock.COBBLESTONE, SimpleBlock.GRASS)
        inputRegion.setBlocks(141, 126, blocks)

        // Write the region to a new file
        val regionDir = Files.createTempDirectory("J2BlocksTest")
        val regionFilePath = regionDir.resolve("r.900.22.mca")
        val outputRegionFile = regionFilePath.toFile()
        inputRegion.writeToFile(outputRegionFile)

        // when
        // Read in this region
        val result = Region(900, 22, null)
        result.readFromFile(outputRegionFile)
        result.setBlocks(141, 126, blocks)

        // then
        assertThat(result, `is`(inputRegion))
    }

    /*    private void compareRegions(int x, int z) throws IOException {
        final File originFile = new File("src/test/resources/orig/r." + x + "." + z + ".mca");
        final Region originRegion = new Region(x, z, null);
        originRegion.readFromFile(originFile);

        final File fixedFile = new File("src/test/resources/updated/r." + x + "." + z + ".mca");
        final Region fixedRegion = new Region(x, z, null);
        fixedRegion.readFromFile(fixedFile);

        Chunk[][] originChunks = originRegion.getChunks();
        Chunk[][] fixedChunks = fixedRegion.getChunks();

        for (int i = 0; i < originChunks.length; ++i) {
            for (int j = 0; j < originChunks[i].length; ++j) {
                Chunk origChunk = originChunks[i][j];
                Chunk fixedChunk = fixedChunks[i][j];
                compareChunks(origChunk, fixedChunk);

            }
        }
    }

    private void compareChunks(final Chunk origChunk, final Chunk fixedChunk) {
        for (int i = 0; i < origChunk.getHeightMap().length; ++i) {
            for (int j = 0; j < origChunk.getHeightMap()[i].length; ++j) {
                if ( origChunk.getHeightMap()[i][j] != fixedChunk.getHeightMap()[i][j]) {
                    System.out.println("Different was " +  origChunk.getHeightMap()[i][j] + " now " + fixedChunk.getHeightMap()[i][j]);
                }
            }
        }
    }

    private int getRegionCoord(int coord) {
        int regionCoord = coord % Region.Companion.getBLOCKS_PER_REGION_SIDE();
        if(regionCoord < 0) {
            regionCoord += Region.Companion.getBLOCKS_PER_REGION_SIDE();
        }
        return regionCoord;
    }*/

}