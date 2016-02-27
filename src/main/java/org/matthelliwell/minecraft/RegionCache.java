package org.matthelliwell.minecraft;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;

import com.google.common.collect.ImmutableSet;
import net.morbz.minecraft.world.Region;

/**
 * Implements an LRU cache of regions
 */
public class RegionCache extends LinkedHashMap<Point, Region> {
    private final BiConsumer<Point, Region> regionLoadedCallback;
    private final int cacheCapacity;
    private final Path regionDir;


    public RegionCache(final Path regionDir, final BiConsumer<Point, Region> regionLoadedCallback, final int cacheCapacity) {
        super(cacheCapacity);
        this.regionLoadedCallback = regionLoadedCallback;
        this.cacheCapacity = cacheCapacity;
        this.regionDir = regionDir;
    }

    /**
     * If the cache size is graeter than the specified size then we write the region to disk before removing it
     * from memory
     */
    @Override
    protected boolean removeEldestEntry(final Map.Entry<Point, Region> eldest) {
        if (size() > cacheCapacity) {
            saveRegion(eldest);
            return true;
        } else {
            return false;
        }
    }

    /**
     * If the region isn't in memory, it will try and load it from disk
     */
    @Override
    public Region get(final Object key) {
        Region region = super.get(key);
        if ( region == null ) {
            region = loadRegion(key);
            put((Point) key, region);
        }

        return region;
    }

    /**
     * The key set need to include everything in memory and everything on disk. We need to return a copy of the memory
     * key set to avoid ConcurrentModificationException as we iterate through and stuff is added or removed from memory.
     * If you add new regions whilst iterating through this keyset, the set may no longer be valid but you won't get
     * any indication of this
     * @return Returned sorted set of keys. They are sorted to try and reduce the amount of loading from disk that
     * is done as iterate through them
     */
    @Override
    public Set<Point> keySet() {
        final Set<Point> result = new TreeSet<>(Comparator.comparing(Point::getX).thenComparing(Point::getY));
        result.addAll(super.keySet());
        result.addAll(getKeysOfAllRegionFiles());
        return result;
    }

    /**
     * Returns the keys of all the region files that have been saved to disk
     */
    private Set<Point> getKeysOfAllRegionFiles() {
        final ImmutableSet.Builder<Point> points = new ImmutableSet.Builder<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(regionDir)) {
            for (final Path path : directoryStream) {
                points.add(getPointFromRegionFile(path));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return points.build();
    }


    private void saveRegion(final Map.Entry<Point, Region> eldest) {
        try {
            final Point point = eldest.getKey();
            final Path regionFile = getRegionFileFromPoint(point);
            final Region region = eldest.getValue();
            region.writeToFile(regionFile.toFile());
            System.out.println("Saved region " + point);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Region loadRegion(final Object key)  {
        try {
            final Point point = (Point) key;
            final Path regionFile = getRegionFileFromPoint(point);
            if (Files.exists(regionFile)) {
                final Region region = new Region(null, point.x, point.y, null);
                region.readFromFile(regionFile.toFile());
                System.out.println("Loaded region " + point);
                regionLoadedCallback.accept(point, region);
                return region;
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getRegionFileFromPoint(final Point point) {
        return regionDir.resolve("r." + point.x + "." + point.y + ".mca");
    }

    /**
     * Extracts the point from the region file name
     * @param path
     * @return
     */
    private Point getPointFromRegionFile(final Path path) {
        final String[] parts = path.getFileName().toString().split("\\.");
        return new Point(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }

}
