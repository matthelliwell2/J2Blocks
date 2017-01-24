package org.matthelliwell.minecraft.level

/**
 * The class for world generators that don't have generator options.
 */
enum class SimpleGenerator(override val generatorName: String) : IGenerator {
    /**
     * Default generator
     */
    DEFAULT("default"),

    /**
     * Amplified generator
     */
    AMPLIFIED("amplified"),

    /**
     * Large biomes generator
     */
    LARGE_BIOMES("largeBiomes");

    /**
     * {@inheritDoc}
     */
    override val generatorOptions: String?
        get() = null
}
