package org.matthelliwell.minecraft.utils

/**
 * A class for common string methods.
 */
object StringUtils {
    /**
     * Joins an array of strings to a single String where the parts are seperated by the glue
     * string.

     * @param parts The string parts to be joined
     * *
     * @param glue The seperator between the parts
     * *
     * @return The joined string
     */
    // TODO use extention function
    fun join(parts: Array<String>, glue: String): String {
        var str = ""
        var isFirst = true
        for (part in parts) {
            if (!isFirst) {
                str += glue
            } else {
                isFirst = false
            }
            str += part
        }
        return str
    }
}
