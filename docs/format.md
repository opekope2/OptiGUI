# Replacing GUI textures

This page describes the usage of OptiGUI INI files^OptiGUI\ 2.1.0-beta.1\ or\ later^. [The OptiFine custom GUI documentation is available here](https://optifine.readthedocs.io/custom_guis.html).

!!! warning
    OptiGUI 2.1.0-beta.1 removed all OptiFine extensions from OptiFine files: `_cartography_table`, `_chest_boat`, `_grindstone`, `_loom`, `_smithing_table`, `_stonecutter`, `_barrel`, `_minecart`, `_furnace`, `_blast`, `_blast_furnace`, `_smoker`, `_camel`, `_zombie_horse`, `_skeleton_horse`, `_wandering_trader`.

    See the migration guide [here](/migrate/).

You can define a texture replacement for each inventory GUI, and apply them based on different criteria.

!!! info "Location"
    `/assets/minecraft/optigui/gui/ANY_NAME.properties` ([file naming rules apply](/syntax/#file-naming-rules))

For each container GUI texture to replace, create a `.ini` file in `/assets/minecraft/optigui/gui/` folder of the resource pack. INI files can be organized into subfolders of any depth, as long as everything is within the top-level `/assets/minecraft/optigui/gui/` folder.

!!! note
    Every property is optional, unless noted otherwise. Properties unspecified in a properties file will not be taken into account while replacing GUI textures.

## General properties

These properties may be specified for all container types.

### `replacement`

!!! info "Required"

!!! info "Type"
    [Path](/syntax/#paths) to a texture

Replacement texture for the default GUI texture of the container.

### `name`

!!! info "Type"
    [String](/syntax/#strings) (exact value)

Custom entity or block entity name.

Apply texture only when the container's name is equal to the specified string.

### `name.wildcard`

!!! info "Type"
    [String](/syntax/#strings) (case-sensitive wildcard)

Custom entity or block entity name.

Apply texture only when the container's name matches the specified wildcard.

### `name.wildcard.ignore_case`

!!! info "Type"
    [String](/syntax/#strings) (case-insensitive wildcard)

Custom entity or block entity name.

Apply texture only when the container's name matches the specified wildcard.

### `name.regex`

!!! info "Type"
    [String](/syntax/#strings) (case-sensitive regex)

Custom entity or block entity name.

Apply texture only when the container's name matches the specified regex.

### `name.regex.ignore_case`

!!! info "Type"
    [String](/syntax/#strings) (case-insensitive regex)

Custom entity or block entity name.

Apply texture only when the container's name matches the specified regex.

### `biomes`

!!! info "Type"
    [List](/syntax/#lists) of biome identifiers

Biomes of the entity or block entity, where this replacement applies.

### `heights`

!!! info "Type"
    [Integer](/syntax/#ranges), or [range](/syntax/#numbers) of integers

Heights (Y coordiantes) of the entity or block entity, where this replacement applies.

!!! note
    Since Minecraft 1.18, negative values may be specified for height. When used in a range, they have to be put in parenthesis `( )`.

### `date`

!!! info "Type"
    [Date](/syntax/#dates)

The dates in a year, when the texture should be replaced.

### `interaction.texture`

!!! info "Type"
    [Path](/syntax/#paths) to a texture

!!! abstract "Default"
    When left empty, OptiGUI looks up the default texture of the specified containers.

The original texture path of a container.

## Anvil

!!! abstract "Texture path"
    `minecraft:textures/gui/container/anvil.png`

!!! example
    ```ini
    [anvil chipped_anvil damaged_anvil]
    ```

## Barrel

!!! abstract "Texture path"
    `minecraft:textures/gui/container/generic_54.png`

!!! example
    ```ini
    [barrel]
    ```

## Beacon

!!! abstract "Texture path"
    `minecraft:textures/gui/container/beacon.png`

!!! example
    ```ini
    [beacon]
    beacon.levels = 1 3-5
    ```

### `beacon.levels`

!!! info "Type"
    [Integer](/syntax/#ranges), or [range](/syntax/#numbers) of integers

What levels of beacon power to apply to (how many bases of blocks).

## Brewing stand

!!! abstract "Texture path"
    `minecraft:textures/gui/container/brewing_stand.png`

!!! example
    ```ini
    [brewing_stand]
    ```

## Cartography table

!!! abstract "Texture path"
    `minecraft:textures/gui/container/cartography_table.png`

!!! example
    ```ini
    [cartography_tabe]
    ```

## Chest & trapped chest

!!! abstract "Texture path"
    `minecraft:textures/gui/container/generic_54.png`

!!! example
    ```ini
    [chest trapped_chest]
    chest.large = true
    ```

### `chest.large`

!!! info "Type"
    [Boolean](/syntax/#booleans)

Use replacement on a double chest.

## Ender chest

!!! abstract "Texture path"
    `minecraft:textures/gui/container/generic_54.png`

!!! example
    ```ini
    [ender_chest]
    ```

## Chest boats

!!! abstract "Texture path"
    `minecraft:textures/gui/container/generic_54.png`

!!! example
    ```ini
    [chest_boat]
    chest_boat.variants = oak spruce
    ```

### `chest_boat.variants`

!!! info "Type"
    [List](/syntax/#lists) of strings

The wood type of the chest boat. Possible vaues:

* `acacia`
* `bamboo` ^Minecraft\ 1.19.3\ or\ later^
* `birch`
* `cherry` ^Minecraft\ 1.19.4\ or\ later^
* `dark_oak`
* `jungle`
* `mangrove`
* `oak`
* `spruce`

## Chest minecart

!!! abstract "Texture path"
    `mniecraft:textures/gui/container/generic_54.png`

!!! example
    ```ini
    [chest_minecart]
    ```

## Crafting table

!!! abstract "Texture path"
    `minecraft:textures/gui/container/crafting_table.png`

!!! example
    ```ini
    [crafting_table]
    ```

## Dispenser

!!! abstract "Texture path"
    `minecraft:textures/gui/container/dispenser.png`

!!! example
    ```ini
    [dispenser]
    ```

## Dropper

!!! abstract "Texture path"
    `minecraft:textures/gui/container/dispenser.png`

!!! example
    ```ini
    [dropper]
    ```

## Enchanting table

!!! abstract "Texture path"
    `minecraft:textures/gui/container/enchanting_table.png`

!!! example
    ```ini
    [enchanting_table]
    ```

## Furnace

!!! abstract "Texture path"
    `minecraft:textures/gui/container/furnace.png`

!!! example
    ```ini
    [furnace]
    ```

## Blast furnace

!!! abstract "Texture path"
    `minecraft:textures/gui/container/blast_furnace.png`

!!! example
    ```ini
    [blast_furnace]
    ```

## Smoker

!!! abstract "Texture path"
    `minecraft:textures/gui/container/smoker.png`

!!! example
    ```ini
    [smoker]
    ```

## Grindstone

!!! abstract "Texture path"
    `minecraft:textures/gui/container/grindstone.png`

!!! example
    ```ini
    [grindstone]
    ```

## Hopper

!!! abstract "Texture path"
    `minecraft:textures/gui/container/hopper.png`

!!! example
    ```ini
    [hopper]
    ```

## Hopper minecart

!!! abstract "Texture path"
    `minecraft:textures/gui/container/hopper.png`

!!! example
    ```ini
    [hopper_minecart]
    ```

## Horse

!!! abstract "Texture path"
    `minecraft:textures/gui/container/horse.png`

!!! example
    ```ini
    [horse]
    ```

## Donkey

!!! abstract "Texture path"
    `minecraft:textures/gui/container/horse.png`

!!! example
    ```ini
    [donkey]
    ```

## Mule

!!! abstract "Texture path"
    `minecraft:textures/gui/container/horse.png`

!!! example
    ```ini
    [mule]
    ```

## Llama & trader llama

!!! abstract "Texture path"
    `minecraft:textures/gui/container/horse.png`

!!! example
    ```ini
    [llama trader_llama]
    llama.colors = red green blue
    ```

### `llama.colors`

!!! info "Type"
    [List](/syntax/#lists) of strings

Llama carpet color. Possible values:

* `white`
* `orange`
* `magenta`
* `light_blue`
* `yellow`
* `lime`
* `pink`
* `gray`
* `light_gray`
* `cyan`
* `purple`
* `blue`
* `brown`
* `green`
* `red`
* `black`

## Zombie horse

!!! abstract "Texture path"
    `minecraft:textures/gui/container/horse.png`

!!! example
    ```ini
    [zombie_horse]
    ```

## Skeleton horse

!!! abstract "Texture path"
    `minecraft:textures/gui/container/horse.png`

!!! example
    ```ini
    [skeleton_horse]
    ```

## Camel

!!! abstract "Texture path"
    `minecraft:textures/gui/container/horse.png`

!!! example
    ```ini
    [camel]
    ```

## Loom

!!! abstract "Texture path"
    `minecraft:textures/gui/container/loom.png`

!!! example
    ```ini
    [loom]
    ```

## Shulker boxes

!!! abstract "Texture path"
    `minecraft:textures/gui/container/shulker_box.png`

!!! example
    ```ini
    [shulker_box]
    [white_shulker_box]
    [orange_shulker_box]
    [magenta_shulker_box]
    [light_blue_shulker_box]
    [yellow_shulker_box]
    [lime_shulker_box]
    [pink_shulker_box]
    [gray_shulker_box]
    [light_gray_shulker_box]
    [cyan_shulker_box]
    [purple_shulker_box]
    [blue_shulker_box]
    [brown_shulker_box]
    [green_shulker_box]
    [red_shulker_box]
    [black_shulker_box]
    ```

## Smithing table

!!! abstract "Texture path"
    `minecraft:textures/gui/container/smithing.png`

!!! example
    ```ini
    [smithing_table]
    ```

## Stonecutter

!!! abstract "Texture path"
    `minecraft:textures/gui/container/stonecutter.png`

!!! example
    ```ini
    [stonecutter]
    ```

## Villagers

!!! abstract "Texture path"
    `minecraft:textures/gui/container/villager2.png`

!!! example
    ```ini
    [villager]
    ```

### `villager.professions`

!!! info "Type"
    [List](/syntax/#lists) of profesions

List of villager professions with optional levels.

The profession syntax is similar to the [date syntax](/syntax/#dates), but it accepts and optional namespace.

!!! example "Cleric (any levels) or fisherman (any levels)"
    ```ini
    [villager]
    villager.professions = cleric minecraft:fisherman
    ```

!!! example "Fletcher (levels 1, 3, 4)"
    ```ini
    [villager]
    villager.professions = fletcher@1 minecraft:fletcher@3-4
    ```

#### Professions

* `armorer`
* `butcher`
* `cartographer`
* `cleric`
* `farmer`
* `fisherman`
* `fletcher`
* `leatherworker`
* `librarian`
* `mason`
* `nitwit`
* `shepherd`
* `toolsmith`
* `weaponsmith`

!!! tip
    OptiGUI supports professions from other mods, if prefixed with a namespace.

## Wandering trader

!!! abstract "Texture path"
    `minecraft:textures/gui/container/villager2.png`

!!! example
    ```ini
    [wandering_trader]
    ```

## Survival inventory

!!! abstract "Texture path"
    `minecraft:textures/gui/container/inventory.png`

!!! example
    ```ini
    [player]
    interaction.texture = minecraft:textures/gui/container/inventory.png
    ```

General properties are supported, however, since there is no interaction, those apply to the player instead of the interacted container, because it is not applicable.

## Creative inventory & everything else

!!! note
    GUIs not having a default texture need to be fitered with [`interaction.texture`](#interactiontexture).

!!! example "Example for creative inventory"
    This is the equivalent syntax of [OptiFine's `texture.<path>` example in the table](https://optifine.readthedocs.io/custom_guis.html#general-properties). In `/assets/minecraft/optigui/gui/creative_desert.ini`:

    ```ini
    [player #1]
    biomes = desert
    interaction.texture = minecraft:textures/gui/container/creative_inventory/tab_inventory.png
    replacement = tab_inventory_desert.png

    [player #2]
    biomes = desert
    interaction.texture = minecraft:textures/gui/container/creative_inventory/tabs.png
    replacement = tabs_desert.png

    [player #3]
    biomes = desert
    interaction.texture = minecraft:textures/gui/container/creative_inventory/tab_items.png
    replacement = tab_items_desert.png

    [player #4]
    biomes = desert
    interaction.texture = minecraft:textures/gui/container/creative_inventory/tab_item_search.png
    replacement = tab_item_search_desert.png
    ```
