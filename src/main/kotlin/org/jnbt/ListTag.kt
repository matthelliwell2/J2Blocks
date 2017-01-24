package org.jnbt


/**
 * The `TAG_List` tag.

 * @author Graham Edgecombe
 */
class ListTag(name: String, val type: Class<out Tag<*>>, value: List<Tag<*>>) : Tag<List<Tag<*>>>(name, value) {
    override fun toString(): String {

        val name = name
        var append = ""
        if (name != "") {
            append = "(\"$name\")"
        }
        val bldr = StringBuilder()
        bldr.append("TAG_List" + append + ": " + value.size
                + " entries of type " + NBTUtils.getTypeName(type)
                + "\r\n{\r\n")
        for (t in value) {
            bldr.append("   " + t.toString().replace("\r\n".toRegex(), "\r\n   ")
                    + "\r\n")
        }
        bldr.append("}")
        return bldr.toString()
    }
}
