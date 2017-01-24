package org.jnbt

/**
 * The `TAG_Byte` tag.
 */
class ByteTag (name: String, value: Byte) : Tag<Byte>(name, value) {

    override fun toString(): String {
        val name = name
        var append = ""
        if (name != "") {
            append = "(\"$name\")"
        }
        return "TAG_Byte$append: $value"
    }
}
