# Replacing GUI textures

This page describes the usage of OptiGUI ^2.1.0-beta.1+^ INI files. [The OptiFine custom GUI documentation is available here](https://optifine.readthedocs.io/custom_guis.html).

This page assumes you to be familiar with the [OptiGUI syntax](syntax.html).

!!! warning
    OptiGUI 2.1.0-beta.1 removed all OptiFine extensions from OptiFine files: `_cartography_table`, `_chest_boat`, `_grindstone`, `_loom`, `_smithing_table`, `_stonecutter`, `_barrel`, `_minecart`, `_furnace`, `_blast`, `_blast_furnace`, `_smoker`, `_camel`, `_zombie_horse`, `_skeleton_horse`, `_wandering_trader`.

    See the [migration guide](migrate.html) to learn how to convert your resource pack.

You can define a texture replacement for each inventory GUI, and apply them based on different criteria.

For each container GUI texture to replace, create a `.ini` file in `/assets/optigui/gui/` folder (or any of its subfolders in any depth) of the resource pack.

!!! note
    When specifying texture paths, **do not** forget file extensions, otherwise OptiGUI **will not** find the resources!

## Common selectors

### `replacement`

**Required**{.chip-darkblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

[Path](syntax.html#paths) to the replacement texture for the default GUI texture of the container. This is not really a selector, as it is not used to match against an interaction, but specifies the replacement if all other selectors match.

### `name`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [String](syntax.html#strings) specifying the exact value of the name of the entity or block entity.

### `name.wildcard`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [String](syntax.html#strings) specifying the case-sensitive [wildcard](syntax.html#wildcard) to match against the name of the entity or block entity.

### `name.wildcard.ignore_case`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [String](syntax.html#strings) specifying the case-insensitive [wildcard](syntax.html#wildcard) to match against the name of the entity or block entity.

### `name.regex`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [String](syntax.html#strings) specifying the case-sensitive [regex](syntax.html#regex) to match against the name of the entity or block entity.

### `name.regex.ignore_case`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [String](syntax.html#strings) specifying the case-insensitive [regex](syntax.html#regex) to match against the name of the entity or block entity.

### `biomes`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](syntax.html#lists) of biome identifiers specifying the biomes of the entity or block entity where this replacement applies.

### `heights`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](syntax.html#lists) of [integers](syntax.html#numbers) and [ranges](syntax.html#ranges) specifying the heights (Y coordiantes) of the entity or block entity, where this replacement applies.

## Interaction selectors

### `interaction.texture`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

[Path](syntax.html#paths) to the texture to replace (the default texture of the container). When left empty, OptiGUI looks up the default texture of the specified containers.

## Independent selectors

### `date`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](syntax.html#lists) of [dates](syntax.html#dates) specifying when the texture should be replaced.

## Player selectors

**Upcoming**{.chip-purple}

[#7](https://github.com/opekope2/OptiGUI/issues/7)

### `player.name`

**Upcoming**{.chip-purple}

### `player.name.wildcard`

**Upcoming**{.chip-purple}

### `player.name.wildcard.ignore_case`

**Upcoming**{.chip-purple}

### `player.name.regex`

**Upcoming**{.chip-purple}

### `player.name.regex.ignore_case`

**Upcoming**{.chip-purple}

### `player.biomes`

**Upcoming**{.chip-purple}

### `player.heights`

**Upcoming**{.chip-purple}

## Comparator selectors

### `comparator.output`

**Upcoming**{.chip-purple}

## Anvil

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/anvil.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [anvil chipped_anvil damaged_anvil]
    ```

## Barrel

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/generic_54.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [barrel]
    ```

## Beacon

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/beacon.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [beacon]
    beacon.levels = 1 3-5
    ```

### `beacon.levels`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](syntax.html#lists) of [integers](syntax.html#numbers) and [ranges](syntax.html#ranges) specifying the levels of beacon power to apply to (how many bases of blocks).

## Book

**Upcoming**{.chip-purple}

### `book.page.current`

**Upcoming**{.chip-purple}

### `book.page.count`

**Upcoming**{.chip-purple}

## Book and Quill

**Upcoming**{.chip-purple}

## Brewing stand

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/brewing_stand.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [brewing_stand]
    ```

## Cartography table

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/cartography_table.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [cartography_tabe]
    ```

## Chest

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/generic_54.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [chest]
    chest.large = true
    ```

### `chest.large`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A single [boolean](syntax.html#booleans) specifying if the texture of a double chest should be replaced.

## Trapped chest

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/generic_54.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors), [`chest.large`](#chestlarge)

!!! example
    ```ini
    [trapped_chest]
    chest.large = true
    ```

## Ender chest

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/generic_54.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [ender_chest]
    ```

## Chest boats

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.19+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/generic_54.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [chest_boat]
    chest_boat.variants = oak spruce
    ```

### `chest_boat.variants`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.19+**{.chip-lightgreen}

A [list](syntax.html#lists) of strings specifying the wood type of the chest boat. Possible values:

* `acacia`
* `bamboo` **Minecraft 1.20+**{.chip-lightgreen} **Minecraft 1.19.3+ with 1.20 experiments**{.chip-lightgreen}
* `birch`
* `cherry` **Minecraft 1.20+**{.chip-lightgreen} **Minecraft 1.19.4+ with 1.20 experiments**{.chip-lightgreen}
* `dark_oak`
* `jungle`
* `mangrove`
* `oak`
* `spruce`

## Chest minecart

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `mniecraft:textures/gui/container/generic_54.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [chest_minecart]
    ```

## Crafting table

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/crafting_table.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [crafting_table]
    ```

## Dispenser

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/dispenser.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [dispenser]
    ```

## Dropper

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/dispenser.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [dropper]
    ```

## Enchanting table

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/enchanting_table.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [enchanting_table]
    ```

## Furnace

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/furnace.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [furnace]
    ```

## Blast furnace

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/blast_furnace.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [blast_furnace]
    ```

## Smoker

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/smoker.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [smoker]
    ```

## Grindstone

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/grindstone.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [grindstone]
    ```

## Hopper

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/hopper.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [hopper]
    ```

## Hopper minecart

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/hopper.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [hopper_minecart]
    ```

## Horse

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [horse]
    ```

## Donkey

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [donkey]
    ```

### `donkey.has_chest`

**Upcoming**{.chip-purple}

[#26](https://github.com/opekope2/OptiGUI/issues/26)

## Mule

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [`donkey.has_chest`](#donkeyhas_chest)

!!! example
    ```ini
    [mule]
    ```

## Llama

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [`donkey.has_chest`](#donkeyhas_chest)

!!! example
    ```ini
    [llama trader_llama]
    llama.colors = red green blue
    ```

### `llama.colors`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](syntax.html#lists) of strings specifying the llama's carpet color. Possible values:

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

## Trader llama

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [`llama.colors`](#llamacolors), [`donkey.has_chest`](#donkeyhas_chest)

!!! example
    ```ini
    [llama trader_llama]
    llama.colors = red green blue
    ```

## Skeleton horse

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [skeleton_horse]
    ```

## Zombie horse

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [zombie_horse]
    ```

## Camel

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.20+**{.chip-lightgreen}
**Minecraft 1.19.3+ with 1.20 experiments**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [camel]
    ```

## Lectern

**OptiGUI 2.1.0+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors), [`book.page.current`](#bookpagecurrent), [`book.page.count`](#bookpagecount)

!!! example
    ```ini
    [lectern]
    ```

## Loom

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/loom.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [loom]
    ```

## Shulker boxes

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/shulker_box.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

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

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/smithing.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [smithing_table]
    ```

## Stonecutter

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/stonecutter.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [stonecutter]
    ```

## Villagers

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/villager2.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [villager]
    ```

### `villager.professions`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](syntax.html#lists) specifying villager professions with optional levels.

The profession syntax is similar to the [date syntax](syntax.html#dates), but it accepts and optional namespace.

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

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/villager2.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [wandering_trader]
    ```

## Survival inventory

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/inventory.png`

Supports the following selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! note
    Common selectors apply to the player instead of the interacted container, because there is no interaction with another entity or block entity.

!!! example
    ```ini
    [player]
    interaction.texture = minecraft:textures/gui/container/inventory.png
    ```

## Creative inventory & everything else

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

!!! note
    GUIs not having a default texture must to be fitered with [`interaction.texture`](#interactiontexture).

!!! example "Example for creative inventory"
    This is the equivalent syntax of [OptiFine's `texture.PATH` example in the table](https://optifine.readthedocs.io/custom_guis.html#texture-texture-path). In `/assets/optigui/gui/creative_desert.ini`:

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
