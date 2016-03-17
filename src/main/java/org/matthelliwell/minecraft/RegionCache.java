package org.matthelliwell.minecraft;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;

import com.google.common.collect.ImmutableSet;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import net.morbz.minecraft.world.Region;

/**
 * Implements an LRU cache of regions
 */
public class RegionCache {
    private final BiConsumer<Point, Region> regionLoadedCallback;
    private final Path regionDir;

    private ConcurrentLinkedHashMap<Point, Region> cache;


    public RegionCache(final Path regionDir, final BiConsumer<Point, Region> regionLoadedCallback, final int cacheCapacity) {
        final ConcurrentLinkedHashMap.Builder<Point, Region> builder = new ConcurrentLinkedHashMap.Builder<>();
        cache = builder
                .maximumWeightedCapacity(cacheCapacity)
                .listener(this::saveRegion)
                .build();

        this.regionLoadedCallback = regionLoadedCallback;
        this.regionDir = regionDir;
    }


    /**
     * If the region isn't in memory, it will try and load it from disk
     */
    public Region get(final Point key) {
        Region region = cache.get(key);
        if ( region == null ) {
            region = loadRegion(key);
            if ( region != null ) {
                cache.put(key, region);
            }
        }

        return region;
    }

    public void put(Point key, Region region) {
        cache.put(key, region);
    }

    public Set<Map.Entry<Point, Region>> entrySet() {
        return cache.entrySet();
    }

    /**
     * The key set need to include everything in memory and everything on disk. We need to return a copy of the memory
     * key set to avoid ConcurrentModificationException as we iterate through and stuff is added or removed from memory.
     * If you add new regions whilst iterating through this keyset, the set may no longer be valid but you won't get
     * any indication of this
     * @return Returned sorted set of keys. They are sorted to try and reduce the amount of loading from disk that
     * is done as iterate through them
     */
    public Set<Point> keySet() {
        final Set<Point> result = new TreeSet<>(Comparator.comparing(Point::getX).thenComparing(Point::getY));
        result.addAll(cache.keySet());
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


    private void saveRegion(final Point point, final Region region) {
        try {
            final Path regionFile = getRegionFileFromPoint(point);
            region.writeToFile(regionFile.toFile());
//            System.out.println("Evicted region " + point);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Region loadRegion(final Object key)  {
        try {
            final Point point = (Point) key;
            final Path regionFile = getRegionFileFromPoint(point);
            if (Files.exists(regionFile)) {
                final Region region = new Region(point.x, point.y, null);
                region.readFromFile(regionFile.toFile());
//                System.out.println("Loaded region " + point);
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
