package org.matthelliwell.minecraft.world

import org.jnbt.*
import org.matthelliwell.minecraft.blocks.CustomBlock
import org.matthelliwell.minecraft.blocks.IBlock
import org.matthelliwell.minecraft.tags.CompoundTagFactory
import org.matthelliwell.minecraft.tags.ITagProvider
import org.matthelliwell.minecraft.tags.ListTagFactory
import java.io.Serializable
import java.util.*

/**
 * Defines a chunk. It consists of 16x16 blocks in XZ-dimension and up to 16 sections for the
 * height.
 */
open class Chunk : ITagProvider, Serializable {

    private val sections = arrayOfNulls<Section>(SECTIONS_PER_CHUNK)
    val heightMap = Array(BLOCKS_PER_CHUNK_SIDE) { IntArray(BLOCKS_PER_CHUNK_SIDE) }
    private val xPos: Int
    private val zPos: Int
    private val parent: Region

    /**
     * Creates a new instance.

     * @param parent The parent block container
     * *
     * @param xPos The X-coordinate within the region
     * *
     * @param zPos The Z-coordinate within the region
     * *
     * @param layers The default layers. Can be 'null'
     */
    constructor(parent: Region, xPos: Int, zPos: Int, layers: DefaultLayers?) {
        this.parent = parent
        this.xPos = xPos
        this.zPos = zPos

        // Set default blocks
        if (layers != null) {
            // Iterate layers
            for (y in 0..World.MAX_HEIGHT - 1) {
                val material = layers.getLayer(y)
                if (material != null) {
                    // Create block
                    val block = CustomBlock(material.value.toByte(), 0, material.transparency)

                    // Iterate area
                    for (x in 0..BLOCKS_PER_CHUNK_SIDE - 1) {
                        for (z in 0..BLOCKS_PER_CHUNK_SIDE - 1) {
                            // Set block
                            setBlock(x, y, z, block)
                        }
                    }
                }
            }
        }
    }


    /**
     * Creates a new instance from a tag. Doesn't use the defaultLayers as this should have already been passed in
     * and used to create the default blocks when creating the chunk in the first place
     * @param tag The tag representation of this class
     */
    constructor(parent: Region, tag: Tag<*>) {
        this.parent = parent

        val levelTag = (tag as CompoundTag).value
        val compoundTag = levelTag["Level"] as CompoundTag
        val tags = compoundTag.value
        val xcoord = (tags["xPos"] as IntTag).value
        val zcoord = (tags["zPos"] as IntTag).value

        // Chunk coords  number of chunk relative to origin of the world but due to a bug this class wants them
        // relative to the containing region. Region coords need to have been set before this is called.
        xPos = xcoord - parent.x * Region.CHUNKS_PER_REGION_SIDE
        zPos = zcoord - parent.z * Region.CHUNKS_PER_REGION_SIDE

        val sectionTags = (tags["Sections"] as ListTag).value
        sectionTags.forEach {
            val section = Section(it)
            sections[section.y] = section }

        val heightMapArray = (tags["HeightMap"] as IntArrayTag).value
        var i = 0
        for (z in 0..BLOCKS_PER_CHUNK_SIDE - 1) {
            for (x in 0..BLOCKS_PER_CHUNK_SIDE - 1) {
                heightMap[x][z] = heightMapArray[i]
                i++
            }
        }
    }


    /**
     * Sets a block at the given position.

     * @param x The X-coordinate within the chunk
     * *
     * @param y The Y-coordinate
     * *
     * @param z The Z-coordinate within the chunk
     * *
     * @param block The block
     */
    fun setBlock(x: Int, y: Int, z: Int, block: IBlock) {
        // Get section
        val section = getSection(y, true)

        // Set block
        val blockY = y % Section.SECTION_HEIGHT
        section!!.setBlock(x, blockY, z, block)
    }


    /**
     * As we are setting all the blocks for a column (probably) we can recalculate the heightmap and skylight
     * for this column so we don't have to go back to this region later on as this is slow if it needs to be
     * loaded from disk
     */
    fun setBlocks(x: Int, z: Int, blocks: Array<IBlock>) {
        for (y in blocks.indices) {
            setBlock(x, y, z, blocks[y])
        }

        // Calculate the height map as we go to reduce the amount of iterating we have to do
        heightMap[x][z] = 0
        calculateHeightMap(x, z)
    }

    fun addSkyLight(x: Int, z: Int) {
        sections.filter { it != null }.map{it!!.addSkyLight(x, z, World.DEFAULT_SKY_LIGHT)}.findLast{it <= 0}
    }

    /**
     * Returns the highest non transparent block. calculateHeightMap() has to be invoked before
     * calling this method to get actual results.

     * @param x The X-coordinate
     * *
     * @param z The Z-coordinate
     * *
     * @return The Y-coordinate of the highest block
     */
    fun getHighestBlock(x: Int, z: Int): Int {
        return heightMap[x][z]
    }

    private fun getSection(y: Int, create: Boolean): Section? {
        // Get section
        val sectionY = y / Section.SECTION_HEIGHT
        var section: Section? = sections[sectionY]

        // Create section
        if (section == null && create) {
            section = Section(sectionY)
            sections[sectionY] = section
        }
        return section
    }

    /**
     * Has at least 1 block that is not air.

     * @return True if there is a block
     */
    fun hasBlocks(): Boolean {
        // Iterate sections
        for (section in sections) {
            if (section != null && section.blockCount > 0) {
                return true
            }
        }
        return false
    }


    private fun calculateHeightMap(x: Int, z: Int) {
        // Iterate sections from top to bottom
        for (y in SECTIONS_PER_CHUNK - 1 downTo 0) {
            val section = sections[y]
            if (section != null) {
                if (heightMap[x][z] == 0) {
                    val height = section.getHighestBlock(x, z)
                    if (height != -1) {
                        heightMap[x][z] = y * Section.SECTION_HEIGHT + height + 1
                        break
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    override val tag: Tag<*>
        get() {
            val factory = ListTagFactory("Sections", CompoundTag::class.java)
            for (section in sections) {
                if (section != null && section.blockCount > 0) {
                    factory.add(section.tag)
                }
            }
            val region = parent
            val xcoord = region.x * Region.CHUNKS_PER_REGION_SIDE + xPos
            val zcoord = region.z * Region.CHUNKS_PER_REGION_SIDE + zPos
            val factory2 = CompoundTagFactory("Level")
            factory2.set(factory.tag)
            factory2.set(IntTag("xPos", xcoord))
            factory2.set(IntTag("zPos", zcoord))
            factory2.set(LongTag("LastUpdate", System.currentTimeMillis()))
            factory2.set(ByteTag("V", 1.toByte()))

            factory2.set(ByteTag("LightPopulated", 1.toByte()))
            factory2.set(ByteTag("TerrainPopulated", 1.toByte()))

            factory2.set(ListTagFactory("Entities", CompoundTag::class.java).tag)
            factory2.set(ListTagFactory("TileEntities", CompoundTag::class.java).tag)
            val heightMapAry = IntArray(BLOCKS_PER_CHUNK_SIDE * BLOCKS_PER_CHUNK_SIDE)
            var i = 0
            for (z in 0..BLOCKS_PER_CHUNK_SIDE - 1) {
                for (x in 0..BLOCKS_PER_CHUNK_SIDE - 1) {
                    heightMapAry[i] = heightMap[x][z]
                    i++
                }
            }
            factory2.set(IntArrayTag("HeightMap", heightMapAry))
            val factory3 = CompoundTagFactory("")
            factory3.set(factory2.tag)
            return factory3.tag
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val chunk = other as Chunk?

        if (xPos != chunk!!.xPos) return false
        if (zPos != chunk.zPos) return false

        if (!Arrays.equals(sections, chunk.sections)) return false
        if (!Arrays.deepEquals(heightMap, chunk.heightMap)) return false
        return true

    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(sections)
        result = 31 * result + Arrays.deepHashCode(heightMap)
        result = 31 * result + xPos
        result = 31 * result + zPos
        return result
    }

    companion object {
        /**
         * Sections per chunk
         */
        private val SECTIONS_PER_CHUNK = 16

        /**
         * Blocks per chunk side
         */
        val BLOCKS_PER_CHUNK_SIDE = 16
    }
}
