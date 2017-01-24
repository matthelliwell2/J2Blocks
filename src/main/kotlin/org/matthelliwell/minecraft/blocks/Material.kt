package org.matthelliwell.minecraft.blocks

/**
 * This class defines all the basic block with their IDs.
 */
enum class Material constructor(val value: Int, val transparency: Int) {
    AIR(0, 1), // √
    STONE(1, 0), // √
    GRASS(2, 0), // √
    DIRT(3, 0), // √
    COBBLESTONE(4, 0), // √
    PLANKS(5, 0), // √
    SAPLING(6, 1), // ~ (Growth stage missing)
    BEDROCK(7, 0), // √
    FLOWING_WATER(8, 2),
    WATER(9, 2), // √
    FLOWING_LAVA(10, 1),
    LAVA(11, 1), // √
    SAND(12, 0), // √
    GRAVEL(13, 0), // √
    GOLD_ORE(14, 0), // √
    IRON_ORE(15, 0), // √
    COAL_ORE(16, 0), // √
    LOG(17, 0),
    LEAVES(18, 2), // Diffuses sky light
    SPONGE(19, 0),
    GLASS(20, 1), // √
    LAPIS_ORE(21, 0), // √
    LAPIS_BLOCK(22, 0), // √
    DISPENSER(23, 0),
    SANDSTONE(24, 0), // √
    NOTEBLOCK(25, 0),
    BED(26, 1),
    GOLDEN_RAIL(27, 1), // √
    DETECTOR_RAIL(28, 1), // √
    STICKY_PISTON(29, 1),
    WEB(30, 2), // √, Diffuses sky light
    TALLGRASS(31, 1),
    DEADBUSH(32, 1), // √
    PISTON(33, 1),
    PISTON_HEAD(34, 1),
    WOOL(35, 0), // √
    PISTON_EXTENSION(36, 1),
    YELLOW_FLOWER(37, 1), // √ (Double implemented)
    RED_FLOWER(38, 1), // √
    BROWN_MUSHROOM(39, 1), // √
    RED_MUSHROOM(40, 1), // √
    GOLD_BLOCK(41, 0), // √
    IRON_BLOCK(42, 0), // √
    DOUBLE_STONE_SLAB(43, 0),
    STONE_SLAB(44, 1),
    BRICK_BLOCK(45, 0), // √
    TNT(46, 0),
    BOOKSHELF(47, 0), // √
    MOSSY_COBBLESTONE(48, 0), // √
    OBSIDIAN(49, 0), // √
    TORCH(50, 1),
    FIRE(51, 1),
    MOB_SPAWNER(52, 0), // Only graphical transparency
    OAK_STAIRS(53, 0), // Partial transparency
    CHEST(54, 1),
    REDSTONE_WIRE(55, 1),
    DIAMOND_ORE(56, 0), // √
    DIAMOND_BLOCK(57, 0), // √
    CRAFTING_TABLE(58, 0), // √
    WHEAT(59, 1),
    FARMLAND(60, 0), // Partial transparency
    FURNACE(61, 0),
    LIT_FURNACE(62, 0),
    STANDING_SIGN(63, 1),
    WOODEN_DOOR(64, 1), // √
    LADDER(65, 1),
    RAIL(66, 1), // √
    STONE_STAIRS(67, 0), // Partial transparency
    WALL_SIGN(68, 0),
    LEVER(69, 1),
    STONE_PRESSURE_PLATE(70, 1),
    IRON_DOOR(71, 1), // √
    WOODEN_PRESSURE_PLATE(72, 1),
    REDSTONE_ORE(73, 0), // √
    LIT_REDSTONE_ORE(74, 1), // √
    UNLIT_REDSTONE_TORCH(75, 1), // √
    REDSTONE_TORCH(76, 1), // √
    STONE_BUTTON(77, 1),
    SNOW_LAYER(78, 1),
    ICE(79, 2), // √
    SNOW(80, 1), // √
    CACTUS(81, 1),
    CLAY(82, 0), // √
    REEDS(83, 1),
    JUKEBOX(84, 0),
    FENCE(85, 1), // √
    PUMPKIN(86, 0),
    NETHERRACK(87, 0), // √
    SOUL_SAND(88, 0), // √
    GLOWSTONE(89, 1), // √
    PORTAL(90, 1), // √
    LIT_PUMPKIN(91, 0),
    CAKE(92, 1),
    UNPOWERED_REPEATER(93, 1),
    POWERED_REPEATER(94, 1),
    STAINED_GLASS(95, 1), // √
    TRAPDOOR(96, 1),
    MONSTER_EGG(97, 0),
    STONEBRICK(98, 0), // √
    BROWN_MUSHROOM_BLOCK(99, 0),
    RED_MUSHROOM_BLOCK(100, 0),
    IRON_BARS(101, 1), // √
    GLASS_PANE(102, 1), // √
    MELON_BLOCK(103, 0), // √
    PUMPKIN_STEM(104, 1),
    MELON_STEM(105, 1),
    VINE(106, 1),
    FENCE_GATE(107, 1),
    BRICK_STAIRS(108, 0), // Partial transparency
    STONE_BRICK_STAIRS(109, 0), // Partial transparency
    MYCELIUM(110, 0), // √
    WATERLILY(111, 1), // √
    NETHER_BRICK(112, 0), // √
    NETHER_BRICK_FENCE(113, 1), // √
    NETHER_BRICK_STAIRS(114, 0), // Partial transparency
    NETHER_WART(115, 1),
    ENCHANTING_TABLE(116, 1),
    BREWING_STAND(117, 1),
    CAULDRON(118, 1),
    END_PORTAL(119, 1),
    END_PORTAL_FRAME(120, 0),
    END_STONE(121, 0), // √
    DRAGON_EGG(122, 1), // √
    REDSTONE_LAMP(123, 0), // √
    LIT_REDSTONE_LAMP(124, 1), // √
    DOUBLE_WOODEN_SLAB(125, 0),
    WOODEN_SLAB(126, 1),
    COCOA(127, 1),
    SANDSTONE_STAIRS(128, 0), // Partial transparency
    EMERALD_ORE(129, 0), // √
    ENDER_CHEST(130, 1),
    TRIPWIRE_HOOK(131, 1),
    TRIPWIRE(132, 1),
    EMERALD_BLOCK(133, 0), // √
    SPRUCE_STAIRS(134, 0), // Partial transparency
    BIRCH_STAIRS(135, 0), // Partial transparency
    JUNGLE_STAIRS(136, 0), // Partial transparency
    COMMAND_BLOCK(137, 0),
    BEACON(138, 1),
    COBBLESTONE_WALL(139, 1),
    FLOWER_POT(140, 1),
    CARROTS(141, 1),
    POTATOES(142, 1),
    WOODEN_BUTTON(143, 1),
    SKULL(144, 1),
    ANVIL(145, 1),
    TRAPPED_CHEST(146, 1),
    LIGHT_WEIGHTED_PRESSURE_PLATE(147, 1),
    HEAVY_WEIGHTED_PRESSURE_PLATE(148, 1),
    UNPOWERED_COMPARATOR(149, 1),
    POWERED_COMPARATOR(150, 1),
    DAYLIGHT_DETECTOR(151, 1),
    REDSTONE_BLOCK(152, 0), // √, Partial transparency
    QUARTZ_ORE(153, 0), // √
    HOPPER(154, 1),
    QUARTZ_BLOCK(155, 0), // √
    QUARTZ_STAIRS(156, 0), // Partial transparency
    ACTIVATOR_RAIL(157, 1), // √
    DROPPER(158, 0),
    STAINED_HARDENED_CLAY(159, 0), // √
    STAINED_GLASS_PANE(160, 1), // √
    LEAVES2(161, 2), // Diffuses sky light
    LOG2(162, 0),
    ACACIA_STAIRS(163, 0), // Partial transparency
    DARK_OAK_STAIRS(164, 0), // Partial transparency
    SLIME_BLOCK(165, 1), // √
    BARRIER(166, 1), // √
    IRON_TRAPDOOR(167, 1),
    PRISMARINE(168, 0), // √
    SEA_LANTERN(169, 1), // √, Transparency not clear
    HAY_BLOCK(170, 0),
    CARPET(171, 1), // √
    HARDENED_CLAY(172, 0), // √
    COAL_BLOCK(173, 0), // √
    PACKED_ICE(174, 0), // √
    DOUBLE_PLANT(175, 1), // Transparency not clear
    STANDING_BANNER(176, 1),
    WALL_BANNER(177, 1),
    DAYLIGHT_DETECTOR_INVERTED(178, 1),
    RED_SANDSTONE(179, 0), // √
    RED_SANDSTONE_STAIRS(180, 0), // Partial transparency
    DOUBLE_STONE_SLAB2(181, 0),
    STONE_SLAB2(182, 1),
    SPRUCE_FENCE_GATE(183, 1),
    BIRCH_FENCE_GATE(184, 1),
    JUNGLE_FENCE_GATE(185, 1),
    DARK_OAK_FENCE_GATE(186, 1),
    ACACIA_FENCE_GATE(187, 1),
    SPRUCE_FENCE(188, 1), // √
    BIRCH_FENCE(189, 1), // √
    JUNGLE_FENCE(190, 1), // √
    DARK_OAK_FENCE(191, 1), // √
    ACACIA_FENCE(192, 1), // √
    SPRUCE_DOOR(193, 1), // √
    BIRCH_DOOR(194, 1), // √
    JUNGLE_DOOR(195, 1), // √
    ACACIA_DOOR(196, 1), // √
    DARK_OAK_DOOR(197, 1);


    companion object {
        private val idToTransparency: IntArray = IntArray(values().size)

        init {
            for (m in values()) {
                idToTransparency[m.value] = m.transparency
            }
        }

        fun getTransparency(id: Byte): Int {
            return idToTransparency[id.toInt() and 0xFF]
        }
    }
}
