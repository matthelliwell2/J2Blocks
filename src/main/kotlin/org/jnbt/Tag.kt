package org.jnbt

/**
 * Represents a single NBT tag.

 * @author Graham Edgecombe
 */
abstract class Tag<out T>(val name: String, val value: T) {
    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Tag<*>

        if (name != other.name) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int{
        var result = name.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }
}
