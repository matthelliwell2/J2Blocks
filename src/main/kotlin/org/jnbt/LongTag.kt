package org.jnbt

/**
 * The `TAG_Long` tag.

 * @author Graham Edgecombe
 */
class LongTag (name: String, value: Long) : Tag<Long>(name, value) {
    override fun toString(): String {

        val name = name
        var append = ""
        if (name != "") {
            append = "(\"$name\")"
        }
        return "TAG_Long$append: $value"
    }
}
