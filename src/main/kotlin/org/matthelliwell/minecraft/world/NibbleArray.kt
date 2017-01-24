package org.matthelliwell.minecraft.world

import java.util.*

/**
 * This is an array for nibbles (4-bit values).
 */
internal class NibbleArray {
    /**
     * Returns the byte array that holds the nibble values. In case the size of the nibble array is
     * odd the last byte will only hold one nibble.

     * @return The byte array
     */
    val bytes: ByteArray
    private val size: Int

    /**
     * Creates a new instance.

     * @param size The number elements that the array can hold
     */
    constructor(size: Int) {
        // Round up the size in case it's odd
        this.size = size
        val num = Math.ceil(size / 2.0).toInt()
        bytes = ByteArray(num)
    }

    constructor(bytes: ByteArray) {
        this.bytes = bytes
        this.size = bytes.size * 2
    }

    /**
     * Sets an element.

     * @param index The index of the element
     * *
     * @param value The value of the element
     */
    operator fun set(index: Int, value: Byte) {
        var data = bytes[index / 2]
        if (index % 2 == 0) {
            data = data or (value and 0xF)
        } else {
            data = data or ((value and 0xF).toInt() shl 4).toByte()
        }
        bytes[index / 2] = data
    }

    /**
     * Gets an element.

     * @param index The index of the element
     * *
     * @return The value of the element
     */
    operator fun get(index: Int): Byte {
        val data = bytes[index / 2]
        if (index % 2 == 0) {
            return (data and 0xF)
        } else {
            return (data.toInt() shr 4 and 0xF).toByte()
        }
    }

    /**
     * @return The number of elements
     */
    fun size(): Int {
        return size
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as NibbleArray?

        if (size != that!!.size) return false
        return Arrays.equals(bytes, that.bytes)

    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(bytes)
        result = 31 * result + size
        return result
    }
}
