package net.morbz.minecraft.world;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.morbz.minecraft.blocks.IBlock;
import net.morbz.minecraft.blocks.SimpleBlock;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RegionTest {
    @Test
    public void shouldReadAndWriteRegionFile() throws IOException {
        // given
        final Path regionDir = Files.createTempDirectory("J2BlocksTest");
        final Path regionFilePath = regionDir.resolve("r.1.2.mca");
        final File regionFile = regionFilePath.toFile();

        final Region region = new Region(null, 1, 2, null);
        region.setBlock(1, 10, 3, SimpleBlock.GRASS);
        region.setBlock(2, 11, 4, SimpleBlock.GLASS);
        region.setBlock(3, 12, 5, SimpleBlock.BRICK_BLOCK);
        region.setBlock(4, 13, 6, SimpleBlock.COAL_ORE);
        region.setBlock(5, 14, 7, SimpleBlock.GOLD_BLOCK);

        // when
        region.writeToFile(regionFile);
        final Region result = new Region(null, 1, 2, null);
        result.readFromFile(regionFile);

        // then
        assertThat(result, is(region));

        // clean up
        Files.delete(regionFilePath);
        Files.delete(regionDir);
    }

    @Test
    public void shouldReadComplexRegionFile() throws IOException {
        // given
        // Load a provided region
        final File inputFile = new File("src/test/resources/r.900.22.mca");
        final Region inputRegion = new Region(null, 900, 22, null);
        inputRegion.readFromFile(inputFile);

        final IBlock[] blocks = {SimpleBlock.BEDROCK, SimpleBlock.WATER,
        SimpleBlock.AIR, SimpleBlock.AIR,SimpleBlock.AIR,SimpleBlock.AIR,SimpleBlock.AIR,SimpleBlock.AIR,SimpleBlock.AIR,SimpleBlock.AIR,
                SimpleBlock.AIR,SimpleBlock.AIR,SimpleBlock.AIR,SimpleBlock.COBBLESTONE, SimpleBlock.GRASS};
        inputRegion.setBlocks(141, 126, blocks);

        // Write the region to a new file
        final Path regionDir = Files.createTempDirectory("J2BlocksTest");
        final Path regionFilePath = regionDir.resolve("r.900.22.mca");
        final File outputRegionFile = regionFilePath.toFile();
        inputRegion.writeToFile(outputRegionFile);

        // when
        // Read in this region
        final Region result = new Region(null, 900, 22, null);
        result.readFromFile(outputRegionFile);
        result.setBlocks(141, 126, blocks);

        // then
        assertThat(result, is(inputRegion));
    }

}