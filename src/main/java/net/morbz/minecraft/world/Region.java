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

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import net.morbz.minecraft.blocks.IBlock;
import net.unknown.RegionFile;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;

/** 
 * Defines a region. It consists of up to 32x32 chunks in XZ-dimension.
 * 
 * @author MorbZ
 */
public class Region implements Serializable {
	/**
	 * Chunks per region side
	 */
	public static final int CHUNKS_PER_REGION_SIDE = 32;
	
	/**
	 * Blocks per region side
	 */
	public static final int BLOCKS_PER_REGION_SIDE = CHUNKS_PER_REGION_SIDE * Chunk.BLOCKS_PER_CHUNK_SIDE;
	
	private final Chunk[][] chunks = new Chunk[CHUNKS_PER_REGION_SIDE][CHUNKS_PER_REGION_SIDE];
	private final DefaultLayers layers;
	
	private final int xPos;
	private final int zPos;

	/**
	 * Creates a new instance.
	 *  @param xPos The X-coordinate within the world
	 * @param zPos The Z-coordinate within the world
	 * @param layers The default layers. Can be 'null'
	 */
	public Region(int xPos, int zPos, DefaultLayers layers) {
		this.xPos = xPos;
		this.zPos = zPos;
		this.layers = layers;
	}
	
	/**
	 * @return The X-coordinate within the world
	 */
	public int getX() {
		return xPos;
	}
	
	/**
	 * @return The Z-coordinate within the world
	 */
	public int getZ() {
		return zPos;
	}


	public void setBlocks(int x, int z, IBlock[] blocks) {
        Chunk chunk = getChunk(x, z, true);

        int blockX = x % Chunk.BLOCKS_PER_CHUNK_SIDE;
        int blockZ = z % Chunk.BLOCKS_PER_CHUNK_SIDE;

        chunk.setBlocks(blockX, blockZ, blocks);
    }

	/**
	 * Sets a block at the given position.
	 * 
	 * @param x The X-coordinate within the region
	 * @param y The Y-coordinate
	 * @param z The Z-coordinate within the region
	 * @param block The block
	 */
	public void setBlock(int x, int y, int z, IBlock block) {
		// Get chunk 
		final Chunk chunk = getChunk(x, z, true);
		
		// Set block
		int blockX = x % Chunk.BLOCKS_PER_CHUNK_SIDE;
		int blockZ = z % Chunk.BLOCKS_PER_CHUNK_SIDE;
		chunk.setBlock(blockX, y, blockZ, block);
	}

	public int getHighestBlock(int x, int z) {
		final Chunk chunk = getChunk(x, z, false);
		final int blockX = x % Chunk.BLOCKS_PER_CHUNK_SIDE;
		final int blockZ = z % Chunk.BLOCKS_PER_CHUNK_SIDE;

		return chunk.getHighestBlock(blockX, blockZ);
	}

	public Chunk[][] getChunks() {
        return chunks;
    }

	public void addSkyLight(final int x, final int z) {
		final Chunk chunk = getChunk(x, z, false);
		if(chunk != null) {
			int blockX = x % Chunk.BLOCKS_PER_CHUNK_SIDE;
			int blockZ = z % Chunk.BLOCKS_PER_CHUNK_SIDE;
			chunk.addSkyLight(blockX, blockZ);
		}
	}

	private Chunk getChunk(int x, int z, boolean create) {
		// Make chunk coords
		int chunkX = x / Chunk.BLOCKS_PER_CHUNK_SIDE;
		int chunkZ = z / Chunk.BLOCKS_PER_CHUNK_SIDE;
		Chunk chunk = chunks[chunkX][chunkZ];
		
		// Create chunk
		if(chunk == null && create) {
			chunk = new Chunk(this, chunkX, chunkZ, layers);
			chunks[chunkX][chunkZ] = chunk;
		}
		return chunk;
	}

	/**
	 * Writes this region to a file.
	 * 
	 * @param path The path to write the file
	 * @throws IOException 
	 */
	public void writeToFile(File path) throws IOException {
		// Write region file
		RegionFile regionFile = new RegionFile(path);
		try {
			 for(int x = 0; x < CHUNKS_PER_REGION_SIDE; x++) {
				for(int z = 0; z < CHUNKS_PER_REGION_SIDE; z++) {
					Chunk chunk = chunks[x][z];
					if(chunk != null && chunk.hasBlocks()) {
						try (NBTOutputStream out = new NBTOutputStream(regionFile.getChunkDataOutputStream(x, z), false)) {
							out.writeTag(chunks[x][z].getTag());
						}
					}
				}
			}
		} finally {
			regionFile.close();
		}
	}

	public void readFromFile(File path) throws IOException {
		RegionFile regionFile = new RegionFile(path);
		try {
			for(int x = 0; x < CHUNKS_PER_REGION_SIDE; x++) {
                for (int z = 0; z < CHUNKS_PER_REGION_SIDE; z++) {
                    final DataInputStream is = regionFile.getChunkDataInputStream(x, z);
                    if ( is != null ) {
                        try (NBTInputStream in = new NBTInputStream(is, false)) {
                            chunks[x][z] = new Chunk(this, in.readTag());
                        }
                    }
                }
            }
		} finally {
			regionFile.close();
		}

	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final Region region = (Region) o;

		if (xPos != region.xPos) return false;
		if (zPos != region.zPos) return false;

        // Debugging stuff
/*
        for (int i = 0; i < chunks.length; ++i) {
            for (int j = 0; j < chunks[i].length; ++j) {
                if (!chunks[i][j].equals(region.chunks[i][j])) {
                    System.out.println("same = " + chunks[i][j].equals(region.chunks[i][j]));
                }
            }
        }
*/

		if (!Arrays.deepEquals(chunks, region.chunks)) return false;
		return layers != null ? layers.equals(region.layers) : region.layers == null;

	}

	@Override
	public int hashCode() {
		int result = Arrays.deepHashCode(chunks);
		result = 31 * result + (layers != null ? layers.hashCode() : 0);
		result = 31 * result + xPos;
		result = 31 * result + zPos;
		return result;
	}
}
