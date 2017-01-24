package org.matthelliwell.minecraft

import com.google.common.collect.ImmutableSet
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap
import org.matthelliwell.minecraft.world.Region
import java.awt.Point
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

/**
 * Implements an LRU cache of regions
 */
class RegionCache(private val regionDir: Path, private val regionLoadedCallback: (Point, Region) -> Unit, cacheCapacity: Int) {

    private val cache: ConcurrentLinkedHashMap<Point, Region>


    init {
        val builder = ConcurrentLinkedHashMap.Builder<Point, Region>()
        cache = builder
                .maximumWeightedCapacity(cacheCapacity.toLong())
                .listener({ point, region -> this.saveRegion(point, region) })
                .build()
    }


    /**
     * If the region isn't in memory, it will try and load it from disk
     */
    operator fun get(key: Point): Region? {
        var region: Region? = cache[key]
        if (region == null) {
            region = loadRegion(key)
            if (region != null) {
                cache.put(key, region)
            }
        }

        return region
    }

    fun put(key: Point, region: Region) {
        cache.put(key, region)
    }

    // TODO can be made immutable?
    fun entrySet(): MutableSet<MutableMap.MutableEntry<Point, Region>> {
        return cache.entries
    }

    /**
     * The key set need to include everything in memory and everything on disk. We need to return a copy of the memory
     * key set to avoid ConcurrentModificationException as we iterate through and stuff is added or removed from memory.
     * If you add new regions whilst iterating through this keyset, the set may no longer be valid but you won't get
     * any indication of this
     *
     * @return Returned sorted set of keys. They are sorted to try and reduce the amount of loading from disk that
     * is done as you iterate through them.
     */
    fun keySet(): Set<Point> {
        val result = TreeSet(Comparator.comparing<Point, Double>(Point::getX).thenComparing<Double>(Point::getY))
        result.addAll(cache.keys)
        result.addAll(keysOfAllRegionFiles)
        return result
    }

    /**
     * Returns the keys of all the region files that have been saved to disk
     */
    private val keysOfAllRegionFiles: Set<Point>
        get() {
            val points = ImmutableSet.Builder<Point>()
            Files.newDirectoryStream(regionDir).use { directoryStream ->
                directoryStream.forEach { points.add(getPointFromRegionFile(it)) }
            }

            return points.build()
        }

    private fun saveRegion(point: Point, region: Region) {
        try {
            val regionFile = getRegionFileFromPoint(point)
            region.writeToFile(regionFile.toFile())
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    private fun loadRegion(key: Any): Region? {
        try {
            val point = key as Point
            val regionFile = getRegionFileFromPoint(point)
            if (Files.exists(regionFile)) {
                val region = Region(point.x, point.y, null)
                region.readFromFile(regionFile.toFile())
                regionLoadedCallback(point, region)
                return region
            } else {
                return null
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    private fun getRegionFileFromPoint(point: Point): Path {
        return regionDir.resolve("r." + point.x + "." + point.y + ".mca")
    }

    /**
     * Extracts the point from the region file name
     * @param path
     * *
     * @return
     */
    private fun getPointFromRegionFile(path: Path): Point {
        val parts = path.fileName.toString().split("\\.".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
        return Point(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]))
    }
}
