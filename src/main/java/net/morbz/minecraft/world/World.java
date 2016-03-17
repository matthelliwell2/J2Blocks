package net.morbz.minecraft.world;

/*
* The MIT License (MIT)
* 
* Copyright (c) 2014-2015 Merten Peetz
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import net.morbz.minecraft.blocks.IBlock;
import net.morbz.minecraft.level.Level;
import org.matthelliwell.minecraft.RegionCache;

/**
 * The main class for generating a Minecraft map.
 * 
 * @author MorbZ
 */
public class World {
	/**
	 * Maximal world height
	 */
	public static final int MAX_HEIGHT = 256;
	
	/**
	 * The default transparency level (fully transparent)
	 */
	public static final byte DEFAULT_TRANSPARENCY = 1;
	
	/**
	 * The default sky light level (maximal light)
	 */
	public static final byte DEFAULT_SKY_LIGHT = 0xF;

    /** Cache of all the regions */
	private final RegionCache regions;

    private final Level level;
	private DefaultLayers layers;

    private FileManager fileManager;

    /**
     * Creates a new instance.
     *
     * @param level                 The level that is used to define the world settings
     * @param layers                The default layers. Can be 'null'
     * @param updateExistingRegions If true then we re-use an existing level directory rather than create a new one, ie it will
     *                              allow you to update the regions already saved to disk. If false or the level directory
     *                              doesn't exist then it will create a new one
     */
    public World(Level level, DefaultLayers layers, boolean updateExistingRegions) throws IOException {
		this.level = level;
		this.layers = layers;

        this.fileManager = new FileManager(level.getLevelName(), updateExistingRegions);
        this.regions = new RegionCache(fileManager.getRegionDir(), this::onRegionLoaded, 30);

        this.fileManager.writeSessionLock();
    }

	public void setBlocks(int x, int z, IBlock[] blocks) {
        if ( blocks.length == 0 || blocks.length > 255 ) {
            return;
        }

        Region region = getRegion(x, z, true);

        // Set block
        int blockX = getRegionCoord(x);
        int blockZ = getRegionCoord(z);

        region.setBlocks(blockX, blockZ, blocks);
    }


	private Region getRegion(int x, int z, boolean create) {
		// Get region point
		int regionX = x / Region.BLOCKS_PER_REGION_SIDE;
		if(x < 0) {
			regionX--;
		}
		int regionZ = z / Region.BLOCKS_PER_REGION_SIDE;
		if(z < 0) {
			regionZ--;
		}
		Point point = new Point(regionX, regionZ);
		
		// Create region
		Region region = regions.get(point);
		if(region == null && create) {
			region = new Region(regionX, regionZ, layers);
			regions.put(point, region);
		}
		return region;	
	}
	
	private int getRegionCoord(int coord) {
		int regionCoord = coord % Region.BLOCKS_PER_REGION_SIDE;
		if(regionCoord < 0) {
			regionCoord += Region.BLOCKS_PER_REGION_SIDE;
		}
		return regionCoord;
	}
	

	/**
	 * Saves the world in a new directory within the /worlds/ directory. The name of the directory 
	 * is the level name. When there are multiple worlds with the same name they will be numbered.
	 * */
	public void save() {
        fileManager.writeLevelFile(level);

        saveInMemoryRegions();

		System.out.println("Done");
	}



    /**
     * Calculates the skylight for column with specified coords
     */
    public void calculateSkylight(int x, int z) {
        final Region region = getRegion(x, z, false);
        region.addSkyLight(getRegionCoord(x), getRegionCoord(z));
    }

    /**
     * Any regions in memory are saved to disk
     */
    private void saveInMemoryRegions() {
        System.out.println("Saving regions from memory");
        // We haven't overriden this so this will just return the regions in memory
        try {
            for (Map.Entry<Point, Region> entry : regions.entrySet()) {
                final Point point = entry.getKey();
                final Region region = entry.getValue();
                Path regionFile = fileManager.getRegionFile(point.x, point.y);
                System.out.println("Writing region file: " + regionFile);
                region.writeToFile(regionFile.toFile());

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
    private void onRegionLoaded(final Point point, final Region region) {
    }
}
