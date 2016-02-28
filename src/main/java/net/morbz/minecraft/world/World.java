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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import net.morbz.minecraft.blocks.IBlock;
import net.morbz.minecraft.level.Level;
import org.jnbt.NBTOutputStream;
import org.matthelliwell.minecraft.RegionCache;

/**
 * The main class for generating a Minecraft map.
 * 
 * @author MorbZ
 */
public class World implements IBlockContainer {
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

    private Path levelDir;
    private Path regionDir;

    private final Level level;
	private DefaultLayers layers;

    private boolean recalculateSkyLight = false;

    /**
     * Creates a new instance.
     *
     * @param level                 The level that is used to define the world settings
     * @param updateExistingRegions If true then we re-use an existing level directory rather than create a new one, ie it will
     *                              allow you to update the regions already saved to disk. If false or the level directory
     *                              doesn't exist then it will create a new one
     */
    public World(Level level, boolean updateExistingRegions) {
		this.level = level;
        createDirectories(updateExistingRegions);
		this.regions = new RegionCache(regionDir, this::onRegionLoaded, 30);
        writeSessionLock();
	}

    /**
     * Creates a new instance.
     *
     * @param level                 The level that is used to define the world settings
     * @param layers                The default layers. Can be 'null'
     * @param updateExistingRegions If true then we re-use an existing level directory rather than create a new one, ie it will
     *                              allow you to update the regions already saved to disk. If false or the level directory
     *                              doesn't exist then it will create a new one
     */
    public World(Level level, DefaultLayers layers, boolean updateExistingRegions) {
		this.level = level;
		this.layers = layers;

        createDirectories(updateExistingRegions);
        this.regions = new RegionCache(regionDir, this::onRegionLoaded, 30);

        writeSessionLock();
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

	/**
	 * Sets a block at the given world position.
	 * 
	 * @param x The X-coordinate
	 * @param y The Y-coordinate (Height, Must be between 0 and 255)
	 * @param z The Z-coordinate
	 * @param block The block
	 */
	public void setBlock(int x, int y, int z, IBlock block) {
        // We've have to recalc the skylight (and therefore also the height map) at the end as we don't calculate it
        // on the fly for each single block
        recalculateSkyLight = true;
		// Check for valid height
		if(y > MAX_HEIGHT - 1 || y < 0) {
			// Fail silently
			return;
		}
		
		// Get region
		Region region = getRegion(x, z, true);
		
		// Set block
		int blockX = getRegionCoord(x);
		int blockZ = getRegionCoord(z);
		region.setBlock(blockX, y, blockZ, block);
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
			region = new Region(this, regionX, regionZ, layers);
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
	 * {@inheritDoc}
	 */
	@Override
	public byte getSkyLight(int x, int y, int z) {
		// Get region
		Region region = getRegion(x, z, false);
		
		// Get light
		if(region != null) {
			int blockX = getRegionCoord(x);
			int blockZ = getRegionCoord(z);
			return region.getSkyLight(blockX, y, blockZ);
		}
		return DEFAULT_SKY_LIGHT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte getSkyLightFromParent(IBlockContainer child, int childX, int childY, int childZ) {
		int x = Region.BLOCKS_PER_REGION_SIDE * ((Region)child).getX() + childX;
		int z = Region.BLOCKS_PER_REGION_SIDE * ((Region)child).getZ() + childZ;
		return getSkyLight(x, childY, z);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void spreadSkyLight(byte light) {
        for( final Point point : regions.keySet()) {
            final Region region = regions.get(point);
            region.spreadSkyLight(light);
        }
	}

	/**
	 * Saves the world in a new directory within the /worlds/ directory. The name of the directory 
	 * is the level name. When there are multiple worlds with the same name they will be numbered.
	 *
     * @param spreadSkylight Whether to spread the skylight sideways. Calculating light in adjacent blocks is very
     *                       time consuming, particularly with the fairly simple algorithm being used. However if you
     *                       don't have any over-hangs, windows in the side of walls etc then there is no need to do this
     *                       and it speeds up the processing when you've got a large number of regions.
	 */
	public void save(boolean spreadSkylight) {
        writeLevelFile();


        if ( recalculateSkyLight ) {
            calculateSkyLight();
        }

        if (spreadSkylight) {
            System.out.println("Spreading skylight");
            for (int i = DEFAULT_SKY_LIGHT; i > 1; i--) {
                spreadSkyLight((byte) i);
            }
        }

        saveInMemoryRegions();

		System.out.println("Done");
	}

    // Write level.dat
    private void writeLevelFile() {
        try {
            File levelFile = new File(levelDir.toFile(), "level.dat");
            System.out.println("Writing level file: " + levelFile);
            FileOutputStream fos = new FileOutputStream(levelFile);
            try (NBTOutputStream nbtOut = new NBTOutputStream(fos, true)) {
                nbtOut.writeTag(level.getTag());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Write session.lock
    private void writeSessionLock() {
        File sessionLockFile = new File(levelDir.toFile(), "session.lock");
        System.out.println("Writing session lock file: " + sessionLockFile);
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(sessionLockFile))) {
            dos.writeLong(System.currentTimeMillis());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	}


	private void calculateSkyLight() {
		for( final Point point : regions.keySet()) {
            System.out.println("Adding skylight for region " + point);
            final Region region = regions.get(point);
            region.calculateHeightMap();
            region.addSkyLight();
        }
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
                Path regionFile = regionDir.resolve("r." + point.x + "." + point.y + ".mca");
                System.out.println("Writing region file: " + regionFile);
                region.writeToFile(regionFile.toFile());

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	

    private void createDirectories(boolean updateExistingRegions) {
        try {
            // Create worlds directory
            final Path worldPath = Paths.get("worlds");

            String levelName = level.getLevelName();
            levelDir = worldPath.resolve(levelName);

            if (Files.notExists(levelDir) || !updateExistingRegions) {
                // create the level dir making sure it doesn't overwrite and existing dir
                int count = 1;
                while ( Files.exists(levelDir)) {
                    levelDir = worldPath.resolve(levelName + count++);
                }

                Files.createDirectory(levelDir);
            }

            regionDir = levelDir.resolve("region");
            if (Files.notExists(regionDir)) {
                Files.createDirectory(regionDir);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void onRegionLoaded(final Point point, final Region region) {
        region.setParent(this);
    }
}
