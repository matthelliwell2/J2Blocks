package org.jnbt

import java.nio.charset.Charset

/**
 * A class which holds constant values.

 * @author Graham Edgecombe, Jocopa3
 */
object NBTConstants {

    /**
     * The character set used by NBT (UTF-8).
     */
    val CHARSET = Charset.forName("UTF-8")!!

    /**
     * Tag type constants.
     */
    val TYPE_END = 0
    val TYPE_BYTE = 1
    val TYPE_SHORT = 2
    val TYPE_INT = 3
    val TYPE_LONG = 4
    val TYPE_FLOAT = 5
    val TYPE_DOUBLE = 6
    val TYPE_BYTE_ARRAY = 7
    val TYPE_STRING = 8
    val TYPE_LIST = 9
    val TYPE_COMPOUND = 10
    val TYPE_INT_ARRAY = 11

}
