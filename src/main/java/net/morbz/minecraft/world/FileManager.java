package net.morbz.minecraft.world;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.morbz.minecraft.level.Level;
import org.jnbt.NBTOutputStream;

/**
 * Contains methods for managing the files and directories used by this library
 */
public class FileManager {

    private Path levelDir;
    private final Path regionDir;
    final Path worldDir = Paths.get("worlds");

    /**
     * Create the required directories
     */
    public FileManager(final String levelName, final boolean updateExistingRegions) throws IOException {
        levelDir = worldDir.resolve(levelName);

        if (Files.notExists(levelDir) || !updateExistingRegions) {
            // create the level dir making sure it doesn't overwrite and existing dir
            int count = 1;
            while (Files.exists(levelDir)) {
                levelDir = worldDir.resolve(levelName + count++);
            }

            Files.createDirectory(levelDir);
        }

        regionDir = levelDir.resolve("region");
        if (Files.notExists(regionDir)) {
            Files.createDirectory(regionDir);
        }
    }

    public Path getRegionDir() {
        return regionDir;
    }

    public Path getLevelDir() {
        return levelDir;
    }

    public Path getWorldDir() {
        return worldDir;
    }

    public void writeSessionLock() {
        File sessionLockFile = new File(levelDir.toFile(), "session.lock");
        System.out.println("Writing session lock file: " + sessionLockFile);
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(sessionLockFile))) {
            dos.writeLong(System.currentTimeMillis());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Write level.dat if it doesn't already exist
    public void writeLevelFile(final Level level) {
        try {
            File levelFile = levelDir.resolve("level.dat").toFile();
            if ( !levelFile.exists()) {
                System.out.println("Writing level file: " + levelFile);
                FileOutputStream fos = new FileOutputStream(levelFile);
                try (NBTOutputStream nbtOut = new NBTOutputStream(fos, true)) {
                    nbtOut.writeTag(level.getTag());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path getRegionFile(final int x, final int z) {
        return regionDir.resolve("r." + x + "." + z + ".mca");
    }

    /**
     * Regions region file for a particular block. This file may not exist
     */
    public Path getRegionFileForBlock(int x, int z) {
        // TODO Probably need to centralise the coord conversions somewhere
        int regionX = x / Region.BLOCKS_PER_REGION_SIDE;
        if(x < 0) {
            regionX--;
        }
        int regionZ = z / Region.BLOCKS_PER_REGION_SIDE;
        if(z < 0) {
            regionZ--;
        }

        return getRegionFile(regionX, regionZ);
    }
}
