package org.jnbt

/**
 * The `TAG_Byte_Array` tag.
 */
class ByteArrayTag (name: String, value: ByteArray) : Tag<ByteArray>(name, value) {

    override fun toString(): String {

        val hex = StringBuilder()
        for (b in value) {
            val hexDigits = Integer.toHexString(b.toInt()).toUpperCase()
            if (hexDigits.length == 1) {
                hex.append("0")
            }
            hex.append(hexDigits).append(" ")
        }
        val name = name
        var append = ""
        if (name != "") {
            append = "(\"$name\")"
        }
        return "TAG_Byte_Array" + append + ": " + hex.toString()
    }
}
