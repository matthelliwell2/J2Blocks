package org.matthelliwell.minecraft.tags

import org.jnbt.CompoundTag
import org.jnbt.Tag
import java.util.*

/**
 * This class generates a Compound tag.
 * @param name The name of this tag
 */
class CompoundTagFactory (private val name: String) : ITagProvider {
    private val values: MutableMap<String, Tag<*>> = HashMap()

    /**
     * Set a tag. Overwrites other tags that have the same tag name.

     * @param tag The tag to set
     */
    fun set(tag: Tag<*>) {
        values.put(tag.name, tag)
    }

    /**
     * {@inheritDoc}
     */
    override val tag: Tag<*>
        get() = CompoundTag(name, values)
}
