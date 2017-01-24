/*
 ** 2011 January 5
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 **/

/*
 * 2011 February 16
 * 
 * This source code is based on the work of Scaevolus (see notice above).
 * It has been slightly modified by Mojang AB (constants instead of magic
 * numbers, a chunk timestamp header, and auto-formatted according to our
 * formatter template).
 * 
 */

// Interfaces with region files on the disk

/*

 Region File Format

 Concept: The minimum unit of storage on hard drives is 4KB. 90% of Minecraft
 chunks are smaller than 4KB. 99% are smaller than 8KB. Write a simple
 container to store chunks in single files in runs of 4KB sectors.

 Each region file represents a 32x32 group of chunks. The conversion from
 chunk number to region number is floor(coord / 32): a chunk at (30, -3)
 would be in region (0, -1), and one at (70, -30) would be at (3, -1).
 Region files are named "r.x.z.data", where x and z are the region coordinates.

 A region file begins with a 4KB header that describes where chunks are stored
 in the file. A 4-byte big-endian integer represents sector offsets and sector
 counts. The chunk offset for a chunk (x, z) begins at byte 4*(x+z*32) in the
 file. The bottom byte of the chunk offset indicates the number of sectors the
 chunk takes up, and the top 3 bytes represent the sector number of the chunk.
 Given a chunk offset o, the chunk data begins at byte 4096*(o/256) and takes up
 at most 4096*(o%256) bytes. A chunk cannot exceed 1MB in size. If a chunk
 offset is 0, the corresponding chunk is not stored in the region file.

 Chunk data begins with a 4-byte big-endian integer representing the chunk data
 length in bytes, not counting the length field. The length must be smaller than
 4096 times the number of sectors. The next byte is a version field, to allow
 backwards-compatible updates to how chunks are encoded.

 A version of 1 represents a gzipped NBT file. The gzipped data is the chunk
 length - 1.

 A version of 2 represents a deflated (zlib compressed) NBT file. The deflated
 data is the chunk length - 1.

 */

package org.unknown

import java.io.*
import java.util.*
import java.util.zip.DeflaterOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream

class RegionFile(private val fileName: File) {
    private var file: RandomAccessFile? = null
    private val offsets: IntArray
    private val chunkTimestamps: IntArray
    private var sectorFree: ArrayList<Boolean>? = null
    private var sizeDelta: Int = 0
    private var lastModified: Long = 0

    init {
        offsets = IntArray(SECTOR_INTS)
        chunkTimestamps = IntArray(SECTOR_INTS)
        debugln("REGION LOAD " + fileName)

        sizeDelta = 0

        try {
            if (fileName.exists()) {
                lastModified = fileName.lastModified()
            }

            file = RandomAccessFile(fileName, "rw")

            if (file!!.length() < SECTOR_BYTES) {
                /* we need to write the chunk offset table */
                for (i in 0..SECTOR_INTS - 1) {
                    file!!.writeInt(0)
                }
                // write another sector for the timestamp info
                for (i in 0..SECTOR_INTS - 1) {
                    file!!.writeInt(0)
                }

                sizeDelta += SECTOR_BYTES * 2
            }

            if ((file!!.length() and 0xfff).toInt() != 0) {
                /* the file size is not a multiple of 4KB, grow it */
                for (i in 0..(file!!.length() and 0xfff) - 1) {
                    file!!.write(0.toByte().toInt())
                }
            }

            /* set up the available sector map */
            val nSectors = file!!.length().toInt() / SECTOR_BYTES
            sectorFree = ArrayList<Boolean>(nSectors)

            for (i in 0..nSectors - 1) {
                sectorFree!!.add(true)
            }

            sectorFree!![0] = false // chunk offset table
            sectorFree!![1] = false // for the last modified info

            file!!.seek(0)
            for (i in 0..SECTOR_INTS - 1) {
                val offset = file!!.readInt()
                offsets[i] = offset
                if (offset != 0 && (offset shr 8) + (offset and 0xFF) <= sectorFree!!.size) {
                    for (sectorNum in 0..(offset and 0xFF) - 1) {
                        sectorFree!![(offset shr 8) + sectorNum] = false
                    }
                }
            }
            for (i in 0..SECTOR_INTS - 1) {
                val lastModValue = file!!.readInt()
                chunkTimestamps[i] = lastModValue
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /* the modification date of the region file when it was first opened */
    fun lastModified(): Long {
        return lastModified
    }

    /* gets how much the region file has grown since it was last checked */
    @Synchronized fun getSizeDelta(): Int {
        val ret = sizeDelta
        sizeDelta = 0
        return ret
    }

    // various small debug printing helpers
    private fun debug(`in`: String) {
        //        System.out.print(in);
    }

    private fun debugln(`in`: String) {
        debug(`in` + "\n")
    }

    private fun debug(mode: String, x: Int, z: Int, `in`: String) {
        debug("REGION " + mode + " " + fileName.name + "[" + x + "," + z + "] = " + `in`)
    }

    private fun debug(mode: String, x: Int, z: Int, count: Int, `in`: String) {
        debug("REGION " + mode + " " + fileName.name + "[" + x + "," + z + "] " + count + "B = " + `in`)
    }

    private fun debugln(mode: String, x: Int, z: Int, `in`: String) {
        debug(mode, x, z, `in` + "\n")
    }

    /*
     * gets an (uncompressed) stream representing the chunk data returns null if
     * the chunk is not found or an error occurs
     */
    @Synchronized fun getChunkDataInputStream(x: Int, z: Int): DataInputStream? {
        if (outOfBounds(x, z)) {
            debugln("READ", x, z, "out of bounds")
            return null
        }

        try {
            val offset = getOffset(x, z)
            if (offset == 0) {
                // debugln("READ", x, z, "miss");
                return null
            }

            val sectorNumber = offset shr 8
            val numSectors = offset and 0xFF

            if (sectorNumber + numSectors > sectorFree!!.size) {
                debugln("READ", x, z, "invalid sector")
                return null
            }

            file!!.seek((sectorNumber * SECTOR_BYTES).toLong())
            val length = file!!.readInt()

            if (length > SECTOR_BYTES * numSectors) {
                debugln("READ", x, z, "invalid length: $length > 4096 * $numSectors")
                return null
            }

            val version = file!!.readByte()
            if (version.toInt() == VERSION_GZIP) {
                val data = ByteArray(length - 1)
                file!!.read(data)
                val ret = DataInputStream(GZIPInputStream(ByteArrayInputStream(data)))
                // debug("READ", x, z, " = found");
                return ret
            } else if (version.toInt() == VERSION_DEFLATE) {
                val data = ByteArray(length - 1)
                file!!.read(data)
                val ret = DataInputStream(InflaterInputStream(ByteArrayInputStream(data)))
                // debug("READ", x, z, " = found");
                return ret
            }

            debugln("READ", x, z, "unknown version " + version)
            return null
        } catch (e: IOException) {
            debugln("READ", x, z, "exception")
            return null
        }

    }

    fun getChunkDataOutputStream(x: Int, z: Int): DataOutputStream? {
        if (outOfBounds(x, z)) return null

        return DataOutputStream(DeflaterOutputStream(ChunkBuffer(x, z)))
    }

    /*
     * lets chunk writing be multithreaded by not locking the whole file as a
     * chunk is serializing -- only writes when serialization is over
     */
    internal inner class ChunkBuffer(private val x: Int, private val z: Int)// initialize to 8KB
        : ByteArrayOutputStream(8096) {

        override fun close() {
            this@RegionFile.write(x, z, buf, count)
        }
    }

    /* write a chunk at (x,z) with length bytes of data to disk */
    @Synchronized protected fun write(x: Int, z: Int, data: ByteArray, length: Int) {
        try {
            val offset = getOffset(x, z)
            var sectorNumber = offset shr 8
            val sectorsAllocated = offset and 0xFF
            val sectorsNeeded = (length + CHUNK_HEADER_SIZE) / SECTOR_BYTES + 1

            // maximum chunk size is 1MB
            if (sectorsNeeded >= 256) {
                return
            }

            if (sectorNumber != 0 && sectorsAllocated == sectorsNeeded) {
                /* we can simply overwrite the old sectors */
                debug("SAVE", x, z, length, "rewrite")
                write(sectorNumber, data, length)
            } else {
                /* we need to allocate new sectors */

                /* mark the sectors previously used for this chunk as free */
                for (i in 0..sectorsAllocated - 1) {
                    sectorFree!![sectorNumber + i] = true
                }

                /* scan for a free space large enough to store this chunk */
                var runStart = sectorFree!!.indexOf(true)
                var runLength = 0
                if (runStart != -1) {
                    for (i in runStart..sectorFree!!.size - 1) {
                        if (runLength != 0) {
                            if (sectorFree!![i])
                                runLength++
                            else
                                runLength = 0
                        } else if (sectorFree!![i]) {
                            runStart = i
                            runLength = 1
                        }
                        if (runLength >= sectorsNeeded) {
                            break
                        }
                    }
                }

                if (runLength >= sectorsNeeded) {
                    /* we found a free space large enough */
                    debug("SAVE", x, z, length, "reuse")
                    sectorNumber = runStart
                    setOffset(x, z, sectorNumber shl 8 or sectorsNeeded)
                    for (i in 0..sectorsNeeded - 1) {
                        sectorFree!![sectorNumber + i] = false
                    }
                    write(sectorNumber, data, length)
                } else {
                    /*
                     * no free space large enough found -- we need to grow the
                     * file
                     */
                    debug("SAVE", x, z, length, "grow")
                    file!!.seek(file!!.length())
                    sectorNumber = sectorFree!!.size
                    for (i in 0..sectorsNeeded - 1) {
                        file!!.write(emptySector)
                        sectorFree!!.add(false)
                    }
                    sizeDelta += SECTOR_BYTES * sectorsNeeded

                    write(sectorNumber, data, length)
                    setOffset(x, z, sectorNumber shl 8 or sectorsNeeded)
                }
            }
            setTimestamp(x, z, (System.currentTimeMillis() / 1000L).toInt())
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /* write a chunk data to the region file at specified sector number */
    @Throws(IOException::class)
    private fun write(sectorNumber: Int, data: ByteArray, length: Int) {
        debugln(" " + sectorNumber)
        file!!.seek((sectorNumber * SECTOR_BYTES).toLong())
        file!!.writeInt(length + 1) // chunk length
        file!!.writeByte(VERSION_DEFLATE) // chunk version number
        file!!.write(data, 0, length) // chunk data
    }

    /* is this an invalid chunk coordinate? */
    private fun outOfBounds(x: Int, z: Int): Boolean {
        return x < 0 || x >= 32 || z < 0 || z >= 32
    }

    private fun getOffset(x: Int, z: Int): Int {
        return offsets[x + z * 32]
    }

    fun hasChunk(x: Int, z: Int): Boolean {
        return getOffset(x, z) != 0
    }

    @Throws(IOException::class)
    private fun setOffset(x: Int, z: Int, offset: Int) {
        offsets[x + z * 32] = offset
        file!!.seek(((x + z * 32) * 4).toLong())
        file!!.writeInt(offset)
    }

    @Throws(IOException::class)
    private fun setTimestamp(x: Int, z: Int, value: Int) {
        chunkTimestamps[x + z * 32] = value
        file!!.seek((SECTOR_BYTES + (x + z * 32) * 4).toLong())
        file!!.writeInt(value)
    }

    @Throws(IOException::class)
    fun close() {
        file!!.close()
    }

    companion object {
        private val VERSION_GZIP = 1
        private val VERSION_DEFLATE = 2

        private val SECTOR_BYTES = 4096
        private val SECTOR_INTS = SECTOR_BYTES / 4

        internal val CHUNK_HEADER_SIZE = 5
        private val emptySector = ByteArray(4096)
    }
}