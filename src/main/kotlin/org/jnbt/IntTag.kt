package org.jnbt

/**
 * The `TAG_Int` tag.
 * @author Graham Edgecombe
 */
class IntTag (name: String, value: Int) : Tag<Int>(name, value) {
    override fun toString(): String {

        val name = name
        var append = ""
        if (name != "") {
            append = "(\"$name\")"
        }
        return "TAG_Int$append: $value"
    }
}
