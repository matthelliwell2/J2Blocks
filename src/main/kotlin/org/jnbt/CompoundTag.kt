package org.jnbt

/**
 * The `TAG_Compound` tag.

 * @author Graham Edgecombe
 */
class CompoundTag(name: String, value: Map<String, Tag<*>>) : Tag<Map<String, Tag<*>>>(name, value) {

    override fun toString(): String {
        val name = name
        var append = ""
        if (name != "") {
            append = "(\"$name\")"
        }
        val bldr = StringBuilder()

        bldr.append("TAG_Compound" + append + ": " + value.size
                + " entries\r\n{\r\n")
        value.forEach { _, v -> bldr.append("   "
                + v.toString().replace("\r\n".toRegex(), "\r\n   ")
                + "\r\n") }
        bldr.append("}")
        return bldr.toString()
    }
}
