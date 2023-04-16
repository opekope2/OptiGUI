@file: JvmName("ContainerTexturePath")

package opekope2.util

import net.minecraft.util.Identifier

/**
 * Texture paths for Minecraft containers.
 */
object TexturePath {
    @JvmField
    val ANVIL = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/anvil.png")

    @JvmField
    val BEACON = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/beacon.png")

    @JvmField
    val BLAST_FURNACE = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/blast_furnace.png")

    @JvmField
    val BREWING_STAND = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/brewing_stand.png")

    @JvmField
    val CARTOGRAPHY_TABLE = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/cartography_table.png")

    @JvmField
    val GENERIC_54 = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/generic_54.png")

    @JvmField
    val CRAFTING_TABLE = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/crafting_table.png")

    @JvmField
    val DISPENSER = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/dispenser.png")

    @JvmField
    val ENCHANTING_TABLE = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/enchanting_table.png")

    @JvmField
    val FURNACE = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/furnace.png")

    @JvmField
    val GRINDSTONE = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/grindstone.png")

    @JvmField
    val HOPPER = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/hopper.png")

    @JvmField
    val LOOM = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/loom.png")

    @JvmField
    val SHULKER_BOX = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/shulker_box.png")

    @JvmField
    val SMITHING_TABLE = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/smithing.png")

    @JvmField
    val LEGACY_SMITHING_TABLE = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/legacy_smithing.png")

    @JvmField
    val SMOKER = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/smoker.png")

    @JvmField
    val STONECUTTER = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/stonecutter.png")

    @JvmField
    val HORSE = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/horse.png")

    @JvmField
    val VILLAGER2 = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/villager2.png")

    @JvmField
    val INVENTORY = Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/inventory.png")

    private val containerTextures = mapOf(
        "anvil" to ANVIL,
        "chipped_anvil" to ANVIL,
        "damaged_anvil" to ANVIL,

        "barrel" to GENERIC_54,

        "beacon" to BEACON,

        "brewing_stand" to BREWING_STAND,

        "cartography_table" to CARTOGRAPHY_TABLE,

        "chest" to GENERIC_54,
        "ender_chest" to GENERIC_54,
        "trapped_chest" to GENERIC_54,

        "acacia_chest_boat" to GENERIC_54,
        "bamboo_chest_boat" to GENERIC_54,
        "birch_chest_boat" to GENERIC_54,
        "cherry_chest_boat" to GENERIC_54,
        "dark_oak_chest_boat" to GENERIC_54,
        "jungle_chest_boat" to GENERIC_54,
        "mangrove_chest_boat" to GENERIC_54,
        "oak_chest_boat" to GENERIC_54,
        "spruce_chest_boat" to GENERIC_54,

        "chest_minecart" to GENERIC_54,

        "crafting_table" to CRAFTING_TABLE,

        "dispenser" to DISPENSER,
        "dropper" to DISPENSER,

        "enchanting_table" to ENCHANTING_TABLE,

        "furnace" to FURNACE,
        "blast_furnace" to BLAST_FURNACE,
        "smoker" to SMOKER,

        "grindstone" to GRINDSTONE,

        "hopper" to HOPPER,
        "hopper_minecart" to HOPPER,

        "horse" to HORSE,
        "camel" to HORSE,
        "donkey" to HORSE,
        "llama" to HORSE,
        "mule" to HORSE,
        "skeleton_horse" to HORSE,
        "zombie_horse" to HORSE,

        "loom" to LOOM,

        "shulker_box" to SHULKER_BOX,
        "black_shulker_box" to SHULKER_BOX,
        "blue_shulker_box" to SHULKER_BOX,
        "brown_shulker_box" to SHULKER_BOX,
        "cyan_shulker_box" to SHULKER_BOX,
        "gray_shulker_box" to SHULKER_BOX,
        "green_shulker_box" to SHULKER_BOX,
        "light_blue_shulker_box" to SHULKER_BOX,
        "light_gray_shulker_box" to SHULKER_BOX,
        "lime_shulker_box" to SHULKER_BOX,
        "magenta_shulker_box" to SHULKER_BOX,
        "orange_shulker_box" to SHULKER_BOX,
        "pink_shulker_box" to SHULKER_BOX,
        "purple_shulker_box" to SHULKER_BOX,
        "red_shulker_box" to SHULKER_BOX,
        "white_shulker_box" to SHULKER_BOX,
        "yellow_shulker_box" to SHULKER_BOX,

        "smithing_table" to SMITHING_TABLE,

        "stonecutter" to STONECUTTER,

        "villager" to VILLAGER2,
        "wandering_trader" to VILLAGER2,
    )

    /**
     * Returns the texture path of the given container, or `null`, if it's unknown.
     *
     * @param container The id of the entity or block entity to get its GUI texture
     */
    @JvmStatic
    fun ofContainer(container: String) = containerTextures[container]
}
