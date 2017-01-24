package org.matthelliwell.minecraft.world

import org.jnbt.NBTInputStream
import org.jnbt.NBTOutputStream
import org.matthelliwell.minecraft.blocks.IBlock
import org.unknown.RegionFile
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.util.*

/**
 * Defines a region. It consists of up to 32x32 chunks in XZ-dimension.

 * @author MorbZ
 */
open class Region
/**
 * Creates a new instance.
 * @param xPos The X-coordinate within the world
 * *
 * @param zPos The Z-coordinate within the world
 * *
 * @param layers The default layers. Can be 'null'
 */
(
        /**
         * @return The X-coordinate within the world
         */
        val x: Int,
        /**
         * @return The Z-coordinate within the world
         */
        val z: Int, private val layers: DefaultLayers?) : Serializable {

    val chunks = Array(CHUNKS_PER_REGION_SIDE) { arrayOfNulls<Chunk>(CHUNKS_PER_REGION_SIDE) }


    fun setBlocks(x: Int, z: Int, blocks: Array<IBlock>) {
        val chunk = getChunk(x, z, true)

        val blockX = x % Chunk.BLOCKS_PER_CHUNK_SIDE
        val blockZ = z % Chunk.BLOCKS_PER_CHUNK_SIDE

        chunk!!.setBlocks(blockX, blockZ, blocks)
    }

    /**
     * Sets a block at the given position.

     * @param x The X-coordinate within the region
     * *
     * @param y The Y-coordinate
     * *
     * @param z The Z-coordinate within the region
     * *
     * @param block The block
     */
    fun setBlock(x: Int, y: Int, z: Int, block: IBlock) {
        // Get chunk
        val chunk = getChunk(x, z, true)

        // Set block
        val blockX = x % Chunk.BLOCKS_PER_CHUNK_SIDE
        val blockZ = z % Chunk.BLOCKS_PER_CHUNK_SIDE
        chunk!!.setBlock(blockX, y, blockZ, block)
    }

    fun getHighestBlock(x: Int, z: Int): Int {
        val chunk = getChunk(x, z, false)
        val blockX = x % Chunk.BLOCKS_PER_CHUNK_SIDE
        val blockZ = z % Chunk.BLOCKS_PER_CHUNK_SIDE

        return chunk!!.getHighestBlock(blockX, blockZ)
    }

    fun addSkyLight(x: Int, z: Int) {
        val chunk = getChunk(x, z, false)
        if (chunk != null) {
            val blockX = x % Chunk.BLOCKS_PER_CHUNK_SIDE
            val blockZ = z % Chunk.BLOCKS_PER_CHUNK_SIDE
            chunk.addSkyLight(blockX, blockZ)
        }
    }

    private fun getChunk(x: Int, z: Int, create: Boolean): Chunk? {
        // Make chunk coords
        val chunkX = x / Chunk.BLOCKS_PER_CHUNK_SIDE
        val chunkZ = z / Chunk.BLOCKS_PER_CHUNK_SIDE
        var chunk: Chunk? = chunks[chunkX][chunkZ]

        // Create chunk
        if (chunk == null && create) {
            chunk = Chunk(this, chunkX, chunkZ, layers)
            chunks[chunkX][chunkZ] = chunk
        }
        return chunk
    }

    /**
     * Writes this region to a file.

     * @param path The path to write the file
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun writeToFile(path: File) {
        // Write region file
        val regionFile = RegionFile(path)
        try {
            for (x in 0..CHUNKS_PER_REGION_SIDE - 1) {
                for (z in 0..CHUNKS_PER_REGION_SIDE - 1) {
                    val chunk = chunks[x][z]
                    if (chunk != null && chunk.hasBlocks()) {
                        NBTOutputStream(regionFile.getChunkDataOutputStream(x, z)!!, false).use { out -> out.writeTag(chunks[x][z]!!.tag) }
                    }
                }
            }
        } finally {
            regionFile.close()
        }
    }

    @Throws(IOException::class)
    fun readFromFile(path: File) {
        val regionFile = RegionFile(path)
        try {
            for (x in 0..CHUNKS_PER_REGION_SIDE - 1) {
                for (z in 0..CHUNKS_PER_REGION_SIDE - 1) {
                    val `is` = regionFile.getChunkDataInputStream(x, z)
                    if (`is` != null) {
                        NBTInputStream(`is`, false).use { `in` -> chunks[x][z] = Chunk(this, `in`.readTag()) }
                    }
                }
            }
        } finally {
            regionFile.close()
        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val region = other as Region?

        if (x != region!!.x) return false
        if (z != region.z) return false


        if (!Arrays.deepEquals(chunks, region.chunks)) return false
        return if (layers != null) layers == region.layers else region.layers == null

    }

    override fun hashCode(): Int {
        var result = Arrays.deepHashCode(chunks)
        result = 31 * result + (layers?.hashCode() ?: 0)
        result = 31 * result + x
        result = 31 * result + z
        return result
    }

    companion object {
        /**
         * Chunks per region side
         */
        val CHUNKS_PER_REGION_SIDE = 32

        /**
         * Blocks per region side
         */
        val BLOCKS_PER_REGION_SIDE = CHUNKS_PER_REGION_SIDE * Chunk.BLOCKS_PER_CHUNK_SIDE
    }
}
