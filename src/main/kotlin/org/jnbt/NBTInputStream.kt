package org.jnbt

import java.io.Closeable
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.zip.GZIPInputStream

/**
 * This class reads **NBT**, or **Named Binary Tag**
 * streams, and produces an object graph of subclasses of the `Tag`
 * object.
 *
 * The NBT format was created by Markus Persson, and the specification may be
 * found at [
 * http://www.minecraft.net/docs/NBT.txt](http://www.minecraft.net/docs/NBT.txt).
 * @author Graham Edgecombe
 */
class NBTInputStream : Closeable {

    /**
     * The data input stream.
     */
    private val `is`: DataInputStream

    /**
     * Creates a new `NBTInputStream`, which will source its data
     * from the specified input stream.
     * @param is The input stream.
     *
     * @param gzipped Whether the stream is GZip-compressed.
     *
     * @throws IOException if an I/O error occurs.
     */
    constructor(`is`: InputStream, gzipped: Boolean) {
        var result = `is`
        if (gzipped) {
            result = GZIPInputStream(`is`)
        }
        this.`is` = DataInputStream(result)
    }

    /**
     * Creates a new `NBTInputStream`, which will source its data
     * from the specified GZIP-compressed input stream.
     * @param is The input stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    constructor(`is`: InputStream) {
        this.`is` = DataInputStream(GZIPInputStream(`is`))
    }

    //TODO: comment this.  supports raw Gziped data.
    // author: ensirius
    constructor(`is`: DataInputStream) {
        this.`is` = `is`
    }

    /**
     * Reads an NBT tag from the stream.
     * @return The tag that was read.
     *
     * @throws IOException if an I/O error occurs.
     */
    fun readTag(): Tag<*> {
        return readTag(0)
    }

    /**
     * Reads an NBT from the stream.
     * @param depth The depth of this tag.
     *
     * @return The tag that was read.
     * *
     * @throws IOException if an I/O error occurs.
     */
    private fun readTag(depth: Int): Tag<*> {
        val type = `is`.readByte().toInt() and 0xFF

        val name: String
        if (type != NBTConstants.TYPE_END) {
            val nameLength = `is`.readShort() and 0xFFFF.toShort()
            val nameBytes = ByteArray(nameLength.toInt())
            `is`.readFully(nameBytes)
            name = String(nameBytes, NBTConstants.CHARSET)
        } else {
            name = ""
        }

        return readTagPayload(type.toInt(), name, depth)
    }

    /**
     * Reads the payload of a tag, given the name and type.

     * @param type
     * *            The type.
     * *
     * @param name
     * *            The name.
     * *
     * @param depth
     * *            The depth.
     * *
     * @return The tag.
     * *
     * @throws IOException
     * *             if an I/O error occurs.
     */
    private fun readTagPayload(type: Int, name: String, depth: Int): Tag<*> {

        when (type) {
            NBTConstants.TYPE_END -> {
                if (depth == 0) {
                    throw IOException(
                            "[JNBT] TAG_End found without a TAG_Compound/TAG_List tag preceding it.")
                } else {
                    return EndTag()
                }
            }
            NBTConstants.TYPE_BYTE -> return ByteTag(name, `is`.readByte())
            NBTConstants.TYPE_SHORT -> return ShortTag(name, `is`.readShort())
            NBTConstants.TYPE_INT -> return IntTag(name, `is`.readInt())
            NBTConstants.TYPE_LONG -> return LongTag(name, `is`.readLong())
            NBTConstants.TYPE_FLOAT -> return FloatTag(name, `is`.readFloat())
            NBTConstants.TYPE_DOUBLE -> return DoubleTag(name, `is`.readDouble())
            NBTConstants.TYPE_BYTE_ARRAY -> {
                val length = `is`.readInt()
                val bytes = ByteArray(length)
                `is`.readFully(bytes)
                return ByteArrayTag(name, bytes)
            }
            NBTConstants.TYPE_STRING -> {
                val length = `is`.readShort().toInt()
                val bytes = ByteArray(length)
                `is`.readFully(bytes)
                return StringTag(name, String(bytes, NBTConstants.CHARSET))
            }
            NBTConstants.TYPE_LIST -> {
                val childType = `is`.readByte().toInt()
                val length = `is`.readInt()

                val tagList = ArrayList<Tag<*>>()
                for (i in 0..length - 1) {
                    val tag = readTagPayload(childType, "", depth + 1)
                    if (tag is EndTag) {
                        throw IOException(
                                "[JNBT] TAG_End not permitted in a list.")
                    }
                    tagList.add(tag)
                }

                return ListTag(name, NBTUtils.getTypeClass(childType),
                        tagList)
            }
            NBTConstants.TYPE_COMPOUND -> {
                val tagMap = HashMap<String, Tag<*>>()
                while (true) {
                    val tag = readTag(depth + 1)
                    if (tag is EndTag) {
                        break
                    } else {
                        tagMap.put(tag.name, tag)
                    }
                }

                return CompoundTag(name, tagMap)
            }
            NBTConstants.TYPE_INT_ARRAY -> {
                val length = `is`.readInt()
                val ints = IntArray(length)
                for (i in 0..length - 1) {
                    ints[i] = `is`.readInt()
                }
                return IntArrayTag(name, ints)
            }
            else -> throw IOException("[JNBT] Invalid tag type: " + type
                    + ".")
        }
    }

    override fun close() {
        `is`.close()
    }
}
