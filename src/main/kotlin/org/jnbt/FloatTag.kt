package org.jnbt

/**
 * The `TAG_Float` tag.

 * @author Graham Edgecombe
 */
class FloatTag (name: String, value: Float) : Tag<Float>(name, value) {
    override fun toString(): String {
        val name = name
        var append = ""
        if (name != "") {
            append = "(\"$name\")"
        }
        return "TAG_Float$append: $value"
    }
}
