package org.jnbt

/**
 * A class which contains NBT-related utility methods.

 * @author Graham Edgecombe
 */
object NBTUtils {

    /**
     * Gets the type name of a tag.

     * @param clazz
     * *            The tag class.
     * *
     * @return The type name.
     */
    fun getTypeName(clazz: Class<out Tag<*>>): String {

        if (clazz == ByteArrayTag::class.java) {
            return "TAG_Byte_Array"
        } else if (clazz == ByteTag::class.java) {
            return "TAG_Byte"
        } else if (clazz == CompoundTag::class.java) {
            return "TAG_Compound"
        } else if (clazz == DoubleTag::class.java) {
            return "TAG_Double"
        } else if (clazz == EndTag::class.java) {
            return "TAG_End"
        } else if (clazz == FloatTag::class.java) {
            return "TAG_Float"
        } else if (clazz == IntArrayTag::class.java) {
            return "TAG_Int_Array"
        } else if (clazz == IntTag::class.java) {
            return "TAG_Int"
        } else if (clazz == ListTag::class.java) {
            return "TAG_List"
        } else if (clazz == LongTag::class.java) {
            return "TAG_Long"
        } else if (clazz == ShortTag::class.java) {
            return "TAG_Short"
        } else if (clazz == StringTag::class.java) {
            return "TAG_String"
        } else {
            throw IllegalArgumentException("[JNBT] Invalid tag classs ("
                    + clazz.name + ").")
        }
    }

    /**
     * Gets the type code of a tag class.

     * @param clazz
     * *            The tag class.
     * *
     * @return The type code.
     * *
     * @throws IllegalArgumentException
     * *             if the tag class is invalid.
     */
    fun getTypeCode(clazz: Class<out Tag<*>>): Int {

        if (clazz == ByteArrayTag::class.java) {
            return NBTConstants.TYPE_BYTE_ARRAY
        } else if (clazz == ByteTag::class.java) {
            return NBTConstants.TYPE_BYTE
        } else if (clazz == CompoundTag::class.java) {
            return NBTConstants.TYPE_COMPOUND
        } else if (clazz == DoubleTag::class.java) {
            return NBTConstants.TYPE_DOUBLE
        } else if (clazz == EndTag::class.java) {
            return NBTConstants.TYPE_END
        } else if (clazz == FloatTag::class.java) {
            return NBTConstants.TYPE_FLOAT
        } else if (clazz == IntArrayTag::class.java) {
            return NBTConstants.TYPE_INT_ARRAY
        } else if (clazz == IntTag::class.java) {
            return NBTConstants.TYPE_INT
        } else if (clazz == ListTag::class.java) {
            return NBTConstants.TYPE_LIST
        } else if (clazz == LongTag::class.java) {
            return NBTConstants.TYPE_LONG
        } else if (clazz == ShortTag::class.java) {
            return NBTConstants.TYPE_SHORT
        } else if (clazz == StringTag::class.java) {
            return NBTConstants.TYPE_STRING
        } else {
            throw IllegalArgumentException("[JNBT] Invalid tag classs ("
                    + clazz.name + ").")
        }
    }

    /**
     * Gets the class of a type of tag.

     * @param type
     * *            The type.
     * *
     * @return The class.
     * *
     * @throws IllegalArgumentException
     * *             if the tag type is invalid.
     */
    fun getTypeClass(type: Int): Class<out Tag<*>> {

        when (type) {
            NBTConstants.TYPE_END -> return EndTag::class.java
            NBTConstants.TYPE_BYTE -> return ByteTag::class.java
            NBTConstants.TYPE_SHORT -> return ShortTag::class.java
            NBTConstants.TYPE_INT -> return IntTag::class.java
            NBTConstants.TYPE_LONG -> return LongTag::class.java
            NBTConstants.TYPE_FLOAT -> return FloatTag::class.java
            NBTConstants.TYPE_DOUBLE -> return DoubleTag::class.java
            NBTConstants.TYPE_BYTE_ARRAY -> return ByteArrayTag::class.java
            NBTConstants.TYPE_STRING -> return StringTag::class.java
            NBTConstants.TYPE_LIST -> return ListTag::class.java
            NBTConstants.TYPE_COMPOUND -> return CompoundTag::class.java
            NBTConstants.TYPE_INT_ARRAY -> return IntArrayTag::class.java
            else -> throw IllegalArgumentException(
                    "[JNBT] Invalid tag type : $type.")
        }
    }
}
