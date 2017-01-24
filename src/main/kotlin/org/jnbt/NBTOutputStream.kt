package org.jnbt

import java.io.Closeable
import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.zip.GZIPOutputStream

/**
 * This class writes **NBT**, or **Named Binary Tag**
 * `Tag` objects to an underlying `OutputStream`.
 *
 * The NBT format was created by Markus Persson, and the specification may be
 * found at [
 * http://www.minecraft.net/docs/NBT.txt](http://www.minecraft.net/docs/NBT.txt).
 * @author Graham Edgecombe, Jocopa3
 */
class NBTOutputStream : Closeable {

    /**
     * The output stream.
     */
    private val os: DataOutputStream

    /**
     * Creates a new `NBTOutputStream`, which will write data to the
     * specified underlying output stream, GZip-compressed.

     * @param os
     * *            The output stream.
     * *
     * @throws IOException
     * *             if an I/O error occurs.
     */
    constructor(os: OutputStream) {

        this.os = DataOutputStream(GZIPOutputStream(os))
    }


    /**
     * Creates a new `NBTOutputStream`, which will write data to the
     * specified underlying output stream.

     * @param os
     * *            The output stream.
     * *
     * @param gzipped
     * *            Whether the output stream should be GZip-compressed.
     * *
     * @throws IOException
     * *             if an I/O error occurs.
     */
    constructor(os: OutputStream, gzipped: Boolean) {
        var result = os
        if (gzipped) {
            result = GZIPOutputStream(os)
        }
        this.os = DataOutputStream(result)
    }

    /**
     * Writes a tag.

     * @param tag
     * *            The tag to write.
     * *
     * @throws IOException
     * *             if an I/O error occurs.
     */
    fun writeTag(tag: Tag<*>) {

        val type = NBTUtils.getTypeCode(tag.javaClass)
        val name = tag.name
        val nameBytes = name.toByteArray(NBTConstants.CHARSET)

        os.writeByte(type)
        os.writeShort(nameBytes.size)
        os.write(nameBytes)

        if (type == NBTConstants.TYPE_END) {
            throw IOException(
                    "[JNBT] Named TAG_End not permitted.")
        }

        writeTagPayload(tag)
    }

    /**
     * Writes tag payload.

     * @param tag
     * *            The tag.
     * *
     * @throws IOException
     * *             if an I/O error occurs.
     */
    private fun writeTagPayload(tag: Tag<*>) {

        val type = NBTUtils.getTypeCode(tag.javaClass)
        when (type) {
            NBTConstants.TYPE_END -> writeEndTagPayload(tag as EndTag)
            NBTConstants.TYPE_BYTE -> writeByteTagPayload(tag as ByteTag)
            NBTConstants.TYPE_SHORT -> writeShortTagPayload(tag as ShortTag)
            NBTConstants.TYPE_INT -> writeIntTagPayload(tag as IntTag)
            NBTConstants.TYPE_LONG -> writeLongTagPayload(tag as LongTag)
            NBTConstants.TYPE_FLOAT -> writeFloatTagPayload(tag as FloatTag)
            NBTConstants.TYPE_DOUBLE -> writeDoubleTagPayload(tag as DoubleTag)
            NBTConstants.TYPE_BYTE_ARRAY -> writeByteArrayTagPayload(tag as ByteArrayTag)
            NBTConstants.TYPE_STRING -> writeStringTagPayload(tag as StringTag)
            NBTConstants.TYPE_LIST -> writeListTagPayload(tag as ListTag)
            NBTConstants.TYPE_COMPOUND -> writeCompoundTagPayload(tag as CompoundTag)
            NBTConstants.TYPE_INT_ARRAY -> writeIntArrayTagPayload(tag as IntArrayTag)
            else -> throw IOException("[JNBT] Invalid tag type: " + type + ".")
        }
    }

    /**
     * Writes a `TAG_Byte` tag.

     * @param tag
     * *            The tag.
     * *
     * @throws IOException
     * *             if an I/O error occurs.
     */
    private fun writeByteTagPayload(tag: ByteTag) {

        os.writeByte(tag.value.toInt())
    }

    /**
     * Writes a `TAG_Byte_Array` tag.

     * @param tag
     * *            The tag.
     * *
     * @throws IOException
     * *             if an I/O error occurs.
     */
    private fun writeByteArrayTagPayload(tag: ByteArrayTag) {

        val bytes = tag.value
        os.writeInt(bytes.size)
        os.write(bytes)
    }

    /**
     * Writes a `TAG_Compound` tag.

     * @param tag
     * *            The tag.
     * *
     * @throws IOException
     * *             if an I/O error occurs.
     */
    private fun writeCompoundTagPayload(tag: CompoundTag) {

        for (childTag in tag.value.values) {
            writeTag(childTag)
        }
        os.writeByte(0.toByte().toInt()) // end tag - better way?
    }

    /**
     * Writes a `TAG_List` tag.

     * @param tag
     * *            The tag.
     * *
     * @throws IOException
     * *             if an I/O error occurs.
     */
    private fun writeListTagPayload(tag: ListTag) {

        val clazz = tag.type
        val tags = tag.value
        val size = tags.size

        os.writeByte(NBTUtils.getTypeCode(clazz))
        os.writeInt(size)
        for (i in 0..size - 1) {
            writeTagPayload(tags[i])
        }
    }

    /**
     * Writes a `TAG_String` tag.

     * @param tag
     * *            The tag.
     * *
     * @throws IOException
     * *             if an I/O error occurs.
     */
    private fun writeStringTagPayload(tag: StringTag) {

        val bytes = tag.value.toByteArray(NBTConstants.CHARSET)
        os.writeShort(bytes.size)
        os.write(bytes)
    }

    /**
     * Writes a `TAG_Double` tag.

     * @param tag
     * *            The tag.
     * *
     * @throws IOException
     * *             if an I/O error occurs.
     */
    private fun writeDoubleTagPayload(tag: DoubleTag) {

        os.writeDouble(tag.value)
    }

    /**
     * Writes a `TAG_Float` tag.

     * @param tag
     * *            The tag.
     * *
     * @throws IOException
     * *             if an I/O error occurs.
     */
    private fun writeFloatTagPayload(tag: FloatTag) {

        os.writeFloat(tag.value)
    }

    /**
     * Writes a `TAG_Long` tag.

     * @param tag
     * *            The tag.
     * *
     * @throws IOException
     * *             if an I/O error occurs.
     */
    private fun writeLongTagPayload(tag: LongTag) {

        os.writeLong(tag.value)
    }

    /**
     * Writes a `TAG_Int` tag.

     * @param tag
     * *            The tag.
     * *
     * @throws IOException
     * *             if an I/O error occurs.
     */
    private fun writeIntTagPayload(tag: IntTag) {

        os.writeInt(tag.value)
    }

    /**
     * Writes a `TAG_Short` tag.

     * @param tag
     * *            The tag.
     * *
     * @throws IOException
     * *             if an I/O error occurs.
     */
    private fun writeShortTagPayload(tag: ShortTag) {

        os.writeShort(tag.value.toInt())
    }

    /**
     * Writes a `TAG_Int_Array` tag.

     * @param tag
     * *            The tag.
     * *
     * @throws IOException
     * *             if an I/O error occurs.
     */
    private fun writeIntArrayTagPayload(tag: IntArrayTag) {

        val ints = tag.value
        os.writeInt(ints.size)
        for (i in ints.indices) {
            os.writeInt(ints[i])
        }
    }

    /**
     * Writes a `TAG_Empty` tag.

     * @param tag
     * *            The tag.
     * *
     * @throws IOException
     * *             if an I/O error occurs.
     */
    private fun writeEndTagPayload(tag: EndTag) {

        /* empty */
    }

    override fun close() {

        os.close()
    }
}
