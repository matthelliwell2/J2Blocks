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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import net.morbz.minecraft.blocks.IBlock;
import net.morbz.minecraft.blocks.Material;
import net.morbz.minecraft.tags.CompoundTagFactory;
import net.morbz.minecraft.tags.ITagProvider;
import org.jnbt.ByteArrayTag;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.Tag;

/**
 * Defines a section. It consist of 16 blocks in each dimension.
 * 
 * @author MorbZ
 */
class Section implements ITagProvider, Serializable {
	/**
	 * The height in blocks of a section
	 */
	public static final int SECTION_HEIGHT = 16;
	
	/**
	 * The total number of blocks in a section
	 */
	private static final int BLOCKS_PER_SECTION =
		Chunk.BLOCKS_PER_CHUNK_SIDE * Chunk.BLOCKS_PER_CHUNK_SIDE * SECTION_HEIGHT;
	
	private final byte[] blockIds;
	private final byte[] transparency = new byte[BLOCKS_PER_SECTION];
	private final NibbleArray blockData;
	private final NibbleArray skyLight;
	private int blockCount = 0;
	private final int y;

	/**
	 * Creates a new instance.
	 *
	 * @param y The Y-position within the chunk
	 */
	public Section(int y) {
        blockIds = new byte[BLOCKS_PER_SECTION];
        blockData = new NibbleArray(BLOCKS_PER_SECTION);
        skyLight = new NibbleArray(BLOCKS_PER_SECTION);

/*
        for (int i = 0; i < BLOCKS_PER_SECTION; ++i) {
            skyLight.set(i, (byte)255);
        }
*/

		this.y = y;
	
		// Set default transparency
		for(int i = 0; i < transparency.length; i++) {
			transparency[i] = World.DEFAULT_TRANSPARENCY;
		}
	}
	
	/**
	 * @return The Y-position within the chunk
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Sets a block at the given position.
	 * 
	 * @param x The X-coordinate within the section
	 * @param y The Y-coordinate within the section
	 * @param z The Z-coordinate within the section
	 * @param block The block
	 */
	public void setBlock(int x, int y, int z, IBlock block) {
		// We ignore it if it's air
		if(block.getBlockId() == 0) {
			block = null;
		}
		
		// Count non-air blocks
		int index = getBlockIndex(x, y, z);
		if(blockIds[index] == 0 && block != null) {
			blockCount++;
		} else if(blockIds[index] != 0 && block == null) {
			blockCount--;
		}
		
		// Set block
		if(block != null) {
			blockIds[index] = block.getBlockId();
			blockData.set(index, block.getBlockData());
			transparency[index] = (byte)block.getTransparency();
		} else {
			blockIds[index] = 0;
			blockData.set(index, (byte)0);
			transparency[index] = World.DEFAULT_TRANSPARENCY;
		}
	}
	
	private byte getTransparency(int x, int y, int z) {
		int index = getBlockIndex(x, y, z);
		return transparency[index];
	}
	

	/**
	 * Sets the sky light level of the block at given position.
	 * 
	 * @param x The X-coordinate
	 * @param y The Y-coordinate
	 * @param z The Z-coordinate
	 * @param light The sky light level
	 */
	public void setSkyLight(int x, int y, int z, byte light) {
		int index = getBlockIndex(x, y, z);
		skyLight.set(index, light);
	}

    public byte addSkyLight(int x, int z, byte light) {
        for (int y = SECTION_HEIGHT - 1; y >= 0; --y) {
            int index = getBlockIndex(x, y, z);
            final byte t = getTransparency(x, y, z);
            if (t > 1) {
                light -= t;
                light--;
            } else if (t == 0) {
                light = 0;
            }

            if (light > 0) {
                skyLight.set(index, light);
            } else if (light <= 0) {
                break;
            }
        }

        return light;
    }

	

	private int getBlockIndex(int x, int y, int z) {
		int index = 0;
		index += y * Chunk.BLOCKS_PER_CHUNK_SIDE * Chunk.BLOCKS_PER_CHUNK_SIDE;
		index += z * Chunk.BLOCKS_PER_CHUNK_SIDE;
		index += x;
		return index;
	}
	
	/**
	 * Returns the number of blocks that are not air.
	 * 
	 * @return Number of blocks
	 */
	public int getBlockCount() {
		return blockCount;
	}
	
	/**
	 * Returns the Y-coordinate of the highest block that is not air or -1 is there are no blocks in
	 * this column.
	 * 
	 * @param x The X-coordinate
	 * @param z The Z-coordinate
	 * @return The Y-coordinate of the highest block or -1
	 */
	public int getHighestBlock(int x, int z) {
		// Iterate column
		for(int y = SECTION_HEIGHT - 1; y >= 0; y--) {
			int index = getBlockIndex(x, y, z);
			if(blockIds[index] != 0 && transparency[index] != 1) {
				return y;
			}
		}
		return -1;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tag getTag() {
		// Create tag
		CompoundTagFactory factory = new CompoundTagFactory("");
		factory.set(new ByteArrayTag("Blocks", blockIds));
		factory.set(new ByteArrayTag("Data", blockData.getBytes()));
		factory.set(new ByteArrayTag("BlockLight", new NibbleArray(BLOCKS_PER_SECTION).getBytes()));
		factory.set(new ByteArrayTag("SkyLight", skyLight.getBytes()));
		factory.set(new ByteTag("Y", (byte)y));
		return factory.getTag();
	}

	/**
	 * Creates new instance from tag
	 * @param tag representation of this class
	 */
	public Section(Tag tag) {
		final CompoundTag compoundTag = (CompoundTag)tag;
		final Map<String, Tag> tags = compoundTag.getValue();
		blockIds = ((ByteArrayTag)tags.get("Blocks")).getValue();
		blockData = new NibbleArray(((ByteArrayTag)tags.get("Data")).getValue());
		skyLight = new NibbleArray(((ByteArrayTag)tags.get("SkyLight")).getValue());
        y = ((ByteTag)tags.get("Y")).getValue();

		blockCount = 0;
        for (final byte blockId : blockIds) {
            if (blockId != 0) {
                ++blockCount;
            }
        }

        // Set transparency. This is an internal field so doesn't come through from the tag. Instead
        // we have to derive it.
        for(int i = 0; i < transparency.length; i++) {
            transparency[i] = (byte)Material.getTransparency(blockIds[i]);
        }
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final Section section = (Section) o;

		if (blockCount != section.blockCount) return false;
		if (y != section.y) return false;
		if (!Arrays.equals(blockIds, section.blockIds)) return false;
		if (!Arrays.equals(transparency, section.transparency)) return false;

        // debug stuff
/*		for (int i = 0; i < blockData.size(); ++i) {
            if (blockData.get(i) != section.blockData.get(i)) {
                System.out.println("same = " + (blockData.get(i) != section.blockData.get(i)));
            }
        }*/
		// if (blockData != null ? !blockData.equals(section.blockData) : section.blockData != null) return false;
		if (skyLight != null ? !skyLight.equals(section.skyLight) : section.skyLight != null) return false;

		return true;

	}

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(blockIds);
		result = 31 * result + Arrays.hashCode(transparency);
		result = 31 * result + (blockData != null ? blockData.hashCode() : 0);
		result = 31 * result + (skyLight != null ? skyLight.hashCode() : 0);
		result = 31 * result + blockCount;
		result = 31 * result + y;
		return result;
	}

}
