package org.matthelliwell.minecraft.level

/**
 * Defines a game type for the level.
 */
enum class GameType(val value: Int) {
    /**
     * Survival mode
     */
    SURVIVAL(0),

    /**
     * Creative mode
     */
    CREATIVE(1),

    /**
     * Adventure mode
     */
    ADVENTURE(2),

    /**
     * Spectator mode
     */
    SPECTATOR(3)
}
