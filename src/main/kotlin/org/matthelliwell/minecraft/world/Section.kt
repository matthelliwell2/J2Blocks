package org.matthelliwell.minecraft.world

import org.jnbt.ByteArrayTag
import org.jnbt.ByteTag
import org.jnbt.CompoundTag
import org.jnbt.Tag
import org.matthelliwell.minecraft.blocks.IBlock
import org.matthelliwell.minecraft.blocks.Material
import org.matthelliwell.minecraft.tags.CompoundTagFactory
import org.matthelliwell.minecraft.tags.ITagProvider
import java.io.Serializable
import java.util.*

/**
 * Defines a section. It consist of 16 blocks in each dimension.
 */
internal class Section : ITagProvider, Serializable {

    private val blockIds: ByteArray
    private val transparency = ByteArray(BLOCKS_PER_SECTION)
    private val blockData: NibbleArray?
    private val skyLight: NibbleArray?
    /**
     * Returns the number of blocks that are not air.

     * @return Number of blocks
     */
    var blockCount = 0
        private set
    /**
     * @return The Y-position within the chunk
     */
    val y: Int

    /**
     * Creates a new instance.

     * @param y The Y-position within the chunk
     */
    constructor(y: Int) {
        blockIds = ByteArray(BLOCKS_PER_SECTION)
        blockData = NibbleArray(BLOCKS_PER_SECTION)
        skyLight = NibbleArray(BLOCKS_PER_SECTION)

        this.y = y

        // Set default transparency
        for (i in transparency.indices) {
            transparency[i] = World.DEFAULT_TRANSPARENCY
        }
    }

    /**
     * Sets a blockIn at the given position.

     * @param x The X-coordinate within the section
     * *
     * @param y The Y-coordinate within the section
     * *
     * @param z The Z-coordinate within the section
     * *
     * @param blockIn The blockIn
     */
    fun setBlock(x: Int, y: Int, z: Int, blockIn: IBlock?) {
        var block = blockIn
        // We ignore it if it's air
        if (block!!.blockId.toInt() == 0) {
            block = null
        }

        // Count non-air blocks
        val index = getBlockIndex(x, y, z)
        if (blockIds[index].toInt() == 0 && block != null) {
            blockCount++
        } else if (blockIds[index].toInt() != 0 && block == null) {
            blockCount--
        }

        // Set blockIn
        if (block != null) {
            blockIds[index] = block.blockId
            blockData!![index] = block.blockData
            transparency[index] = block.transparency.toByte()
        } else {
            blockIds[index] = 0
            blockData!![index] = 0.toByte()
            transparency[index] = World.DEFAULT_TRANSPARENCY
        }
    }

    private fun getTransparency(x: Int, y: Int, z: Int): Byte {
        val index = getBlockIndex(x, y, z)
        return transparency[index]
    }


    /**
     * Sets the sky light level of the block at given position.

     * @param x The X-coordinate
     * *
     * @param y The Y-coordinate
     * *
     * @param z The Z-coordinate
     * *
     * @param light The sky light level
     */
    fun setSkyLight(x: Int, y: Int, z: Int, light: Byte) {
        val index = getBlockIndex(x, y, z)
        skyLight!![index] = light
    }

    fun addSkyLight(x: Int, z: Int, lightIn: Byte): Byte {
        var light = lightIn
        for (y in SECTION_HEIGHT - 1 downTo 0) {
            val index = getBlockIndex(x, y, z)
            val t = getTransparency(x, y, z)
            if (t > 1) {
                light = light.minus(t).toByte()
                light--
            } else if (t.toInt() == 0) {
                light = 0
            }

            if (light > 0) {
                skyLight!![index] = light
            } else if (light <= 0) {
                break
            }
        }

        return light
    }


    private fun getBlockIndex(x: Int, y: Int, z: Int): Int {
        var index = 0
        index += y * Chunk.BLOCKS_PER_CHUNK_SIDE * Chunk.BLOCKS_PER_CHUNK_SIDE
        index += z * Chunk.BLOCKS_PER_CHUNK_SIDE
        index += x
        return index
    }

    /**
     * Returns the Y-coordinate of the highest block that is not air or -1 is there are no blocks in
     * this column.

     * @param x The X-coordinate
     * *
     * @param z The Z-coordinate
     * *
     * @return The Y-coordinate of the highest block or -1
     */
    fun getHighestBlock(x: Int, z: Int): Int {
        // Iterate column
        for (y in SECTION_HEIGHT - 1 downTo 0) {
            val index = getBlockIndex(x, y, z)
            if (blockIds[index].toInt() != 0 && transparency[index].toInt() != 1) {
                return y
            }
        }
        return -1
    }

    /**
     * {@inheritDoc}
     */
    override // Create tag
    val tag: Tag<*>
        get() {
            val factory = CompoundTagFactory("")
            factory.set(ByteArrayTag("Blocks", blockIds))
            factory.set(ByteArrayTag("Data", blockData!!.bytes))
            factory.set(ByteArrayTag("BlockLight", NibbleArray(BLOCKS_PER_SECTION).bytes))
            factory.set(ByteArrayTag("SkyLight", skyLight!!.bytes))
            factory.set(ByteTag("Y", y.toByte()))
            return factory.tag
        }

    /**
     * Creates new instance from tag
     * @param tag representation of this class
     */
    constructor(tag: Tag<*>) {
        val compoundTag = tag as CompoundTag
        val tags = compoundTag.value
        blockIds = (tags["Blocks"] as ByteArrayTag).value
        blockData = NibbleArray((tags["Data"] as ByteArrayTag).value)
        skyLight = NibbleArray((tags["SkyLight"] as ByteArrayTag).value)
        y = (tags["Y"] as ByteTag).value.toInt()

        blockCount = 0
        for (blockId in blockIds) {
            if (blockId.toInt() != 0) {
                ++blockCount
            }
        }

        // Set transparency. This is an internal field so doesn't come through from the tag. Instead
        // we have to derive it.
        for (i in transparency.indices) {
            transparency[i] = Material.getTransparency(blockIds[i]).toByte()
        }
    }

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val section = other as Section?

        if (blockCount != section!!.blockCount) return false
        if (y != section.y) return false
        if (!Arrays.equals(blockIds, section.blockIds)) return false
        if (!Arrays.equals(transparency, section.transparency)) return false

        // if (blockData != null ? !blockData.equals(section.blockData) : section.blockData != null) return false;
        if (if (skyLight != null) skyLight != section.skyLight else section.skyLight != null) return false

        return true

    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(blockIds)
        result = 31 * result + Arrays.hashCode(transparency)
        result = 31 * result + (blockData?.hashCode() ?: 0)
        result = 31 * result + (skyLight?.hashCode() ?: 0)
        result = 31 * result + blockCount
        result = 31 * result + y
        return result
    }

    companion object {
        /**
         * The height in blocks of a section
         */
        val SECTION_HEIGHT = 16

        /**
         * The total number of blocks in a section
         */
        private val BLOCKS_PER_SECTION =
                Chunk.BLOCKS_PER_CHUNK_SIDE * Chunk.BLOCKS_PER_CHUNK_SIDE * SECTION_HEIGHT
    }
}
