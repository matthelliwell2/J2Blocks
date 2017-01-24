package org.matthelliwell.minecraft.tags

import org.jnbt.ListTag
import org.jnbt.Tag
import java.util.*

/**
 * This class generates a ListTag.
 * @propery name The name of this tag
 * @propery type The type of tags that will be in the list
 */
class ListTagFactory(private val name: String, private val type: Class<out Tag<*>>) : ITagProvider {
    private val values: ArrayList<Tag<*>> = ArrayList()

    /**
     * Adds a tag to the list. There can be multiple tags with the same name.

     * @param tag The tag to add
     */
    fun add(tag: Tag<*>) {
        values.add(tag)
    }

    /**
     * {@inheritDoc}
     */
    override val tag: Tag<*>
        get() = ListTag(name, type, values)
}
