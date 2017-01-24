package org.matthelliwell.minecraft.level

import org.matthelliwell.minecraft.utils.StringUtils
import org.matthelliwell.minecraft.world.DefaultLayers
import org.matthelliwell.minecraft.world.World
import java.util.*

/**
 * This class can be used to define the default block layers of the part of the world that will be
 * created by the internal Minecraft generator. This results in a flat world where the blocks of
 * each Y-coordinate are the same. It is recommended to combine the FlatGenerator with the
 * DefaultLayers to get a consistent world.
 */
class FlatGenerator(private val layers: DefaultLayers?) : IGenerator {

    constructor() : this(null)

    /**
     * {@inheritDoc}
     */
    override val generatorName: String
        get() = "flat"

    /**
     * {@inheritDoc}
     */
    override val generatorOptions: String?
        get() {
            if (layers == null) {
                return null
            }
            var lastBlockId = 0
            var count = 0
            val parts = ArrayList<String>()
            for (y in 0..World.MAX_HEIGHT) {
                val isLast = y == World.MAX_HEIGHT
                var blockId = 0
                if (!isLast) {
                    val material = layers.getLayer(y)
                    if (material != null) {
                        blockId = material.value
                    }
                }

                if (y == 0 && !isLast) {
                    lastBlockId = blockId
                } else {
                    if (blockId != lastBlockId || isLast) {
                        var part = ""
                        if (count != 1) {
                            part += count.toString() + "*"
                        }
                        part += lastBlockId
                        if (!isLast || lastBlockId != 0) {
                            parts.add(part)
                        }

                        lastBlockId = blockId
                        count = 0
                    }
                }
                count++
            }
            val layerOptions = StringUtils.join(parts.toTypedArray(), ",")
            val options = "3;" + layerOptions
            return options
        }
}
