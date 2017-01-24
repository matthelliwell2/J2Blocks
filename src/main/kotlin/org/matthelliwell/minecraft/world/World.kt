package org.matthelliwell.minecraft.world

import org.matthelliwell.minecraft.RegionCache
import org.matthelliwell.minecraft.blocks.IBlock
import org.matthelliwell.minecraft.level.Level
import java.awt.Point
import java.io.IOException

/**
 * The main class for generating a Minecraft map.
 *
 * @property level                 The level that is used to define the world settings
 *
 * @property layers                The default layers. Can be 'null'
 *
 * @property updateExistingRegions If true then we re-use an existing level directory rather than create a new one, ie it will
 *                                 allow you to update the regions already saved to disk. If false or the level directory
 *                                 doesn't exist then it will create a new one
 */
class World(private val level: Level, private val layers: DefaultLayers, updateExistingRegions: Boolean) {

    /** Cache of all the regions  */
    private val regions: RegionCache

    private val fileManager: FileManager

    init {

        this.fileManager = FileManager(level.levelName, updateExistingRegions)
        this.regions = RegionCache(fileManager.regionDir, this::onRegionLoaded, 30)

        this.fileManager.writeSessionLock()
    }

    fun setBlocks(x: Int, z: Int, blocks: Array<IBlock>) {
        if (blocks.isEmpty() || blocks.size > 255) {
            return
        }

        val region = getRegion(x, z, true)

        // Set block
        val blockX = getRegionCoord(x)
        val blockZ = getRegionCoord(z)

        region.setBlocks(blockX, blockZ, blocks)
    }

    fun getHighestBlock(x: Int, z: Int): Int {
        val region = getRegion(x, z, false)

        // Set block
        val blockX = getRegionCoord(x)
        val blockZ = getRegionCoord(z)

        return region.getHighestBlock(blockX, blockZ)
    }


    private fun getRegion(x: Int, z: Int, create: Boolean): Region {
        // Get region point
        var regionX = x / Region.BLOCKS_PER_REGION_SIDE
        if (x < 0) {
            regionX--
        }
        var regionZ = z / Region.BLOCKS_PER_REGION_SIDE
        if (z < 0) {
            regionZ--
        }
        val point = Point(regionX, regionZ)

        // Create region
        var region: Region? = regions[point]
        if (region == null && create) {
            region = Region(regionX, regionZ, layers)
            regions.put(point, region)
        }
        return region!!
    }

    private fun getRegionCoord(coord: Int): Int {
        var regionCoord = coord % Region.BLOCKS_PER_REGION_SIDE
        if (regionCoord < 0) {
            regionCoord += Region.BLOCKS_PER_REGION_SIDE
        }
        return regionCoord
    }


    /**
     * Saves the world in a new directory within the /worlds/ directory. The name of the directory
     * is the level name. When there are multiple worlds with the same name they will be numbered.
     */
    fun save() {
        fileManager.writeLevelFile(level)

        saveInMemoryRegions()

        println("Done")
    }


    /**
     * Calculates the skylight for column with specified coords
     */
    fun calculateSkylight(x: Int, z: Int) {
        val region = getRegion(x, z, false)
        region.addSkyLight(getRegionCoord(x), getRegionCoord(z))
    }

    /**
     * Any regions in memory are saved to disk
     */
    private fun saveInMemoryRegions() {
        println("Saving regions from memory")
        // We haven't overriden this so this will just return the regions in memory
        try {
            for ((point, region) in regions.entrySet()) {
                val regionFile = fileManager.getRegionFile(point.x, point.y)
                println("Writing region file: " + regionFile)
                region.writeToFile(regionFile.toFile())

            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    private fun onRegionLoaded(point: Point, region: Region) {}

    companion object {
        /**
         * Maximal world height
         */
        val MAX_HEIGHT = 256

        /**
         * The default transparency level (fully transparent)
         */
        val DEFAULT_TRANSPARENCY: Byte = 1

        /**
         * The default sky light level (maximal light)
         */
        val DEFAULT_SKY_LIGHT: Byte = 0xF
    }
}
