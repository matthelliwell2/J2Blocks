package org.jnbt

/**
 * The `TAG_String` tag.

 * @author Graham Edgecombe
 */
class StringTag (name: String, value: String) : Tag<String>(name, value) {

    override fun toString(): String {

        val name = name
        var append = ""
        if (name != "") {
            append = "(\"$name\")"
        }
        return "TAG_String$append: $value"
    }
}
