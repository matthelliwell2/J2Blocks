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
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
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
	
	private final Map<Point, Region> regions;

    private final Level level;
	private DefaultLayers layers;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param level The level that is used to define the world settings
	 */
	public World(Level level) {
		this.level = level;

		this.regions = new RegionCache(getRegionDir(), this::onRegionLoaded, 30);
	}
	
	/**
	 * Creates a new instance.
	 * 
	 * @param level The level that is used to define the world settings
	 * @param layers The default layers. Can be 'null'
	 */
	public World(Level level, DefaultLayers layers) {
		this.level = level;
		this.layers = layers;
        this.regions = new RegionCache(getRegionDir(), this::onRegionLoaded, 30);
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
		// Not used
	}

    private Path getRegionDir() {
        try {
            final Path dir = FileSystems.getDefault().getPath("worlds/" + level.getLevelName() + "/region");
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            return dir;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	/**
	 * Saves the world in a new directory within the /worlds/ directory. The name of the directory 
	 * is the level name. When there are multiple worlds with the same name they will be numbered.
	 * 
	 * @return The directory in which the world has been saved
	 * @throws IOException When file writing fails
	 */
	public File save() throws IOException {
		// Create worlds directory
		File worldDir = new File("worlds");
		if(!dirExists(worldDir)) {
			worldDir.mkdir();
		}
		
		// Get level directory
		String levelName = level.getLevelName();
		File levelDir = new File(worldDir, levelName);
		/*if(dirExists(levelDir)) {
			int dirPostfix = 0;
			do {
				dirPostfix++;
				levelDir = new File(worldDir, levelName + dirPostfix);
			} while(dirExists(levelDir));
		}*/
		
		// Create directories
//		levelDir.mkdir();
		
		File regionDir = new File(levelDir, "region");
//		regionDir.mkdir();
		
		// Write session.lock
        File sessionLockFile = new File(levelDir, "session.lock");
        System.out.println("Writing session lock file: " + sessionLockFile);
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(sessionLockFile))) {
            dos.writeLong(System.currentTimeMillis());
        }
		
		// Write level.dat
		File levelFile = new File(levelDir, "level.dat");
		System.out.println("Writing level file: " + levelFile);
		FileOutputStream fos = new FileOutputStream(levelFile);
        try (NBTOutputStream nbtOut = new NBTOutputStream(fos, true)) {
            nbtOut.writeTag(level.getTag());
        }

		processRegions(regionDir);
		
		System.out.println("Done");
		return levelDir;
	}

	private void processRegions(final File regionDir) throws IOException {
		for( final Point point : regions.keySet()) {
			System.out.println("Processing region " + point);
			final Region region = regions.get(point);
			region.calculateHeightMap();
			region.addSkyLight();
			for(int i = DEFAULT_SKY_LIGHT; i > 1; i--) {
				region.spreadSkyLight((byte)i);
			}

			File regionFile = new File(regionDir, "r." + point.x + "." + point.y + ".mca");
			System.out.println("Writing region file: " + regionFile);
			region.writeToFile(regionFile);

            // Drop the region once processed to try and speed up the serialisation
            regions.remove(point);
		}

        // Remove all regions so temp dir is removed
        regions.clear();
	}
	

	private boolean dirExists(File f) {
		return(f.exists() && f.isDirectory());
	}

    private void onRegionLoaded(final Point point, final Region region) {
        region.setParent(this);
    }
}
