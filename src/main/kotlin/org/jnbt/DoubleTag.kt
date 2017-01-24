package org.jnbt

/**
 * The `TAG_Double` tag.

 * @author Graham Edgecombe
 */
class DoubleTag (name: String, value: Double) : Tag<Double>(name, value) {

    override fun toString(): String {
        val name = name
        var append = ""
        if (name != "") {
            append = "(\"$name\")"
        }
        return "TAG_Double$append: $value"
    }
}
