package org.jnbt

class EndTag : Tag<Any?>("", null) {
    override fun toString(): String {
        return "TAG_End"
    }
}
