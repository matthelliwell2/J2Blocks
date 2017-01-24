package org.jnbt

/**
 * The `TAG_Short` tag.

 * @author Graham Edgecombe
 */
class ShortTag (name: String, value: Short) : Tag<Short>(name, value) {

    override fun toString(): String {
        val name = name
        var append = ""
        if (name != "") {
            append = "(\"$name\")"
        }
        return "TAG_Short$append: $value"
    }
}
