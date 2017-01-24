package org.matthelliwell.minecraft.level

/**
 * Interface for world generators.
 */
interface IGenerator {
    /**
     * Returns the generator name as it is used in the level file.

     * @return The generator name
     */
    val generatorName: String

    /**
     * Returns the generator options as they are used in the level file.

     * @return The generator options. Can be 'null'
     */
    val generatorOptions: String?
}
