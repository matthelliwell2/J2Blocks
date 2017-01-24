package org.matthelliwell.minecraft.world

import org.jnbt.NBTOutputStream
import org.matthelliwell.minecraft.level.Level
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Contains methods for managing the files and directories used by this library
 */
class FileManager(levelName: String, updateExistingRegions: Boolean) {

    var levelDir: Path? = null
        private set
    val regionDir: Path
    val worldDir: Path = Paths.get("worlds")

    init {
        levelDir = worldDir.resolve(levelName)

        if (Files.notExists(levelDir) || !updateExistingRegions) {
            // create the level dir making sure it doesn't overwrite and existing dir
            var count = 1
            while (Files.exists(levelDir)) {
                levelDir = worldDir.resolve(levelName + count++)
            }

            Files.createDirectories(levelDir)
        }

        regionDir = levelDir!!.resolve("region")
        if (Files.notExists(regionDir)) {
            Files.createDirectory(regionDir)
        }
    }

    fun writeSessionLock() {
        val sessionLockFile = File(levelDir!!.toFile(), "session.lock")
        println("Writing session lock file: " + sessionLockFile)
        try {
            DataOutputStream(FileOutputStream(sessionLockFile)).use { dos -> dos.writeLong(System.currentTimeMillis()) }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    // Write level.dat if it doesn't already exist
    fun writeLevelFile(level: Level) {
        try {
            val levelFile = levelDir!!.resolve("level.dat").toFile()
            if (!levelFile.exists()) {
                println("Writing level file: " + levelFile)
                val fos = FileOutputStream(levelFile)
                NBTOutputStream(fos, true).use { nbtOut -> nbtOut.writeTag(level.tag) }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    fun getRegionFile(x: Int, z: Int): Path {
        return regionDir.resolve("r.$x.$z.mca")
    }

    /**
     * Regions region file for a particular block. This file may not exist
     */
    fun getRegionFileForBlock(x: Int, z: Int): Path {
        // TODO Probably need to centralise the coord conversions somewhere
        var regionX = x / Region.BLOCKS_PER_REGION_SIDE
        if (x < 0) {
            regionX--
        }
        var regionZ = z / Region.BLOCKS_PER_REGION_SIDE
        if (z < 0) {
            regionZ--
        }

        return getRegionFile(regionX, regionZ)
    }
}
