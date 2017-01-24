package org.matthelliwell.minecraft.level

import org.jnbt.*
import org.matthelliwell.minecraft.tags.CompoundTagFactory
import org.matthelliwell.minecraft.tags.ITagProvider

/**
 * The level defines the settings for the world. Like game mode, spawn point, etc.
 *
 * @property levelName The name of the world. This value is also used as directory name for the generated world.
 * @property generator The generator that is used for the world. Default CREATIVE.
 * @property allowCommands If cheats are allowed. The default value is 'false'.
 * @property mapFeatures If structures like villages, mineshafts, etc. should be generated. The default value is 'true'.
 * @property randomSeed The random seed that is used for world generation. The default value is a random positive long.
 * @property spawnPoint Sets the default world spawn position. In singleplayer mode the player will be spawned within
 * 20x20 blocks around this position. The default values are '0'.
 * @property The game mode. The default value is 'GameType.CREATIVE'.
 */
class Level(val levelName: String,
            val generator: IGenerator = FlatGenerator(),
            val allowCommands: Boolean = false,
            val mapFeatures: Boolean = true,
            var randomSeed: Long? = (Math.random() * java.lang.Long.MAX_VALUE).toLong(),
            val spawnPoint: SpawnPoint = Level.SpawnPoint(0, 0, 0),
            val gameType: GameType = GameType.CREATIVE) : ITagProvider {

    /**
     * {@inheritDoc}
     */
    override val tag: Tag<*>
        get() {
            val factory = CompoundTagFactory("Data")
            factory.set(ByteTag("allowCommands", if (allowCommands) 1.toByte() else 0.toByte()))
            factory.set(IntTag("GameType", gameType.value))
            factory.set(StringTag("generatorName", generator.generatorName))
            factory.set(LongTag("LastPlayed", System.currentTimeMillis()))
            factory.set(StringTag("LevelName", levelName))
            factory.set(ByteTag("MapFeatures", if (mapFeatures) 1.toByte() else 0.toByte()))
            factory.set(LongTag("RandomSeed", randomSeed!!))
            factory.set(IntTag("SpawnX", spawnPoint.x))
            factory.set(IntTag("SpawnY", spawnPoint.y))
            factory.set(IntTag("SpawnZ", spawnPoint.z))
            factory.set(IntTag("version", 19133))
            val options = generator.generatorOptions
            if (options != null) {
                factory.set(StringTag("generatorOptions", options))
            }
            val factory2 = CompoundTagFactory("")
            factory2.set(factory.tag)
            return factory2.tag
        }

    data class SpawnPoint(val x: Int, val y: Int, val z: Int)
}
