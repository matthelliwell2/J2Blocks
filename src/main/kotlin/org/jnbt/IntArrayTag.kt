package org.jnbt

/**
 * The `TAG_Byte_Array` tag.
 * @author Jocopa3
 */
class IntArrayTag (name: String, value: IntArray) : Tag<IntArray>(name, value) {

    override fun toString(): String {
        val integers = StringBuilder()
        for (b in value) {
            integers.append(b).append(" ")
        }
        val name = name
        var append = ""
        if (name != "") {
            append = "(\"$name\")"
        }
        return "TAG_Int_Array" + append + ": " + integers.toString()
    }
}
