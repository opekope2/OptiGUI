# Replacing GUI textures

You can define a texture replacement for each inventory GUI, and apply them based on different criteria. The resource pack format is similar to the [OptiFine format](https://optifine.readthedocs.io/custom_guis.html), with extended features.

!!! info "Location"
    `/assets/minecraft/optifine/gui/container/ANY_NAME.properties` ([file naming rules apply](/syntax/#file-naming-rules))

For each container GUI texture to replace, create a `.properties` file in `/assets/minecraft/optifine/gui/container` folder of the resource pack. Properties files can be organized into subfolders of any depth, as long as everything is within the top-level `/assets/minecraft/optifine/gui/container` folder.

!!! note
    Every property is optinal, unless noted otherwise. Properties unspecified in a properties file will not be taken into account while replacing GUI textures (unless noted otherwise).

## General properties

These properties may be specified for all container types.

### `container`

!!! note "Required"
    `container` decides which GUI texture to replace, and which additional properties may apply.

!!! info "Type"
    String

The type of the inventory to repace the texture of. One of:

* `anvil`
* `beacon`
* `brewing_stand`
* `chest`
* `crafting`
* `dispenser`
* `enchantment`
* `furnace`
* `hopper`
* `horse`
* `inventory`
* `shulker_box`
* `villager`
* `creative`

!!! warning "Note"
    The following containers are exclusive to OptiGUI, and are not supported by OptiFine.

* `_cartography_table`
* `_chest_boat`
* `_grindstone`
* `_loom`
* `_smithing_table`
* `_stonecutter`

### `texture`

!!! note "Required"
    `texture` defines the replacement texture.

!!! info "Type"
    [Path](/syntax/#paths) to a texture

Replacement texture for the default GUI texture of the container.

### `name`

!!! info "Type"
    [String](/syntax/#strings)

Custom entity or block entity name.

Apply texture only when the container's name matches this rule. See [strings](/syntax/#strings) for possibilities.

### `biomes`

!!! info "Type"
    [List](/syntax/#lists) of [biomes](/syntax/#biomes)

Biomes of the entity or block entity, where this replacement applies.

### `heights`

!!! info "Type"
    [Integer](/syntax/#ranges), or [range](/syntax/#numbers) of integers

Heights (Y coordiantes) of the entity or block entity, where this replacement applies.

!!! note
    Since Minecraft 1.18, negative values may be specified for height. When used in a range, they have to be put in parenthesis `( )`.

## Anvil

!!! note "Container"
    Requires `#!properties container=anvil`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/anvil.png`

## Beacon

!!! note "Container"
    Requires `#!properties container=beacon`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/beacon.png`

### `levels`

!!! info "Type"
    [Integer](/syntax/#ranges), or [range](/syntax/#numbers) of integers

What levels of beacon power to apply to (how many bases of blocks).

## Brewing stand

!!! note "Container"
    Requires `#!properties container=brewing_stand`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/brewing_stand.png`

## Cartography table

!!! note "Container"
    Requires `#!properties container=_cartography_table`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/cartography_table.png`

!!! warning "Note"
    This feature is exclusive to OptiGUI, and is not supported by OptiFine.

## Chests & barrel

!!! note "Container"
    Requires `#!properties container=chest`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/generic_54.png`

### `large`

!!! info "Type"
    [Boolean](/syntax/#booleans)

Use replacement on a large chest.

### `trapped`

!!! info "Type"
    [Boolean](/syntax/#booleans)

Use replacement on a trapped chest.

### `christmas`

!!! info "Type"
    [Boolean](/syntax/#booleans)

Use replacement on any chest during Christmas.

### `ender`

!!! info "Type"
    [Boolean](/syntax/#booleans)

Use replacement on an Ender Chest.

### `_barrel`

!!! info "Type"
    [Boolean](/syntax/#booleans)

!!! warning "Note"
    This feature is exclusive to OptiGUI, and is not supported by OptiFine.

Use replacement on a barrel.

### `_minecart`

!!! info "Type"
    [Boolean](/syntax/#booleans)

!!! warning "Note"
    This feature is exclusive to OptiGUI, and is not supported by OptiFine.

!!! tip
    Available since OptiGUI `2.0.0-alpha.5`.

Use replacement on a chest minecart.

## Chest boats

!!! note "Container"
    Requires `#!properties container=_chest_boat`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/generic_54.png`

!!! warning "Note"
    This feature is exclusive to OptiGUI, and is not supported by OptiFine.

!!! tip
    Available since OptiGUI `2.0.0-alpha.6`.

### `variants`

!!! info "Type"
    [List](/syntax/#lists) of strings

The wood type of the chest boat. Possible vaues:

* `acacia`
* `bamboo`
* `birch`
* `cherry`
* `dark_oak`
* `jungle`
* `mangrove`
* `oak`
* `spruce`

## Crafting table

!!! note "Container"
    Requires `#!properties container=crafting`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/crafting_table.png`

## Dispenser & dropper

!!! note "Container"
    Requires `#!properties container=dispenser`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/dispenser.png`

### `variants`

!!! info "Type"
    [List](/syntax/#lists) of strings

Which dispenser variant to apply to. Possible values:

* `dispenser`
* `dropper`

## Enchanting table

!!! note "Container"
    Requires `#!properties container=enchantment`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/enchanting_table.png`

## Furnaces

!!! note "Container"
    Requires `#!properties container=furnace`

!!! abstract "Texture path"
    Furnace: `minecraft:textures/gui/container/furnace.png`

    Blast furnace: `minecraft:textures/gui/container/blast_furnace.png`

    Smoker: `minecraft:textures/gui/container/smoker.png`

### `variants`

!!! info "Type"
    [List](/syntax/#lists) of strings

!!! warning "Note"
    This feature is exclusive to OptiGUI, and is not supported by OptiFine.

!!! info "Default"
    When unspecified, only furnace's texture will be replaced (equivalent to `#!properties variants=_furnace`).

Which furnace variant to apply to. Possible values:

* `_furnace`
* `_blast` (alias for `_blast_furnace`)
* `_blast_furnace`
* `_smoker`

## Grindstone

!!! note "Container"
    Requires `#!properties container=_grindstone`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/grindstone.png`

!!! warning "Note"
    This feature is exclusive to OptiGUI, and is not supported by OptiFine.

## Hopper

!!! note "Container"
    Requires `#!properties container=hopper`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/hopper.png`

### `_minecart`

!!! info "Type"
    [Boolean](/syntax/#booleans)

!!! warning "Note"
    This feature is exclusive to OptiGUI, and is not supported by OptiFine.

!!! tip
    Available since OptiGUI `2.0.0-alpha.5`.

Use replacement on a hopper minecart.

## Horses

!!! note "Container"
    Requires `#!properties container=horse`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/horse.png`

### `variants`

!!! info "Type"
    [List](/syntax/#lists) of strings

What horse variant to apply to. Possible values:

* `horse`
* `donkey`
* `mule`
* `llama`

!!! warning "Note"
    The following variants are exclusive to OptiGUI, and are not supported by OptiFine.

* `_camel`
* `_zombie_horse`
* `_skeleton_horse`

### `colors`

!!! info "Type"
    [List](/syntax/#lists) of strings

!!! note
    `colors` is ignored for horse variants other than llama.

!!! tip
    Available since OptiGUI `2.0.0-beta.1`.

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

## Loom

!!! note "Container"
    Requires `#!properties container=_loom`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/loom.png`

!!! warning "Note"
    This feature is exclusive to OptiGUI, and is not supported by OptiFine.

## Shulker boxes

!!! note
    Implies `#!properties container=shulker_box`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/shulker_box.png`

### `colors`

!!! info "Type"
    [List](/syntax/#lists) of strings

Shulker box color. Possible values:

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

## Smithing table

!!! note "Container"
    Requires `#!properties container=_smithing_table`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/smithing.png`

!!! warning "Note"
    This feature is exclusive to OptiGUI, and is not supported by OptiFine.

## Stonecutter

!!! note "Container"
    Requires `#!properties container=_stonecutter`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/stonecutter.png`

!!! warning "Note"
    This feature is exclusive to OptiGUI, and is not supported by OptiFine.

## Survival inventory

!!! note "Container"
    Requires `#!properties container=inventory`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/inventory.png`

### `name`

!!! info "Type"
    [String](/syntax/#strings)

!!! tip
    Available since OptiGUI `2.0.0-beta.1`.

The name of the player.

### `biomes`

!!! info "Type"
    [List](/syntax/#lists) of [biomes](/syntax/#biomes)

The biome the player is in.

### `heights`

!!! info "Type"
    [Integer](/syntax/#ranges), or [range](/syntax/#numbers) of integers

The Y coordinate of the player.

## Villagers

!!! note "Container"
    Requires `#!properties container=villager`

!!! abstract "Texture path"
    `minecraft:textures/gui/container/villager2.png`

### `professions`

!!! info "Type"
    [List](/syntax/#lists) of profesions

List of villager professions with optional levels.

#### Profession format: `profession`

!!! example "Professions fisher, shepard, nitwit"
    ```properties
    professions=fisherman shepherd nitwit
    ```

#### Profession format: `profession:levels`

!!! note
    Only applies, when `levels` starts with a digit.

!!! warning "Caution"
    `levels` can't contain spaces, because it is the list separator character.

!!! example "Professions farmer (all levels) or librarian (levels 1, 3, 4)"
    ```properties
    professions=farmer librarian:1,3-4
    ```

#### Profession format: `namespace:profession`

!!! warning "Note"
    This format is exclusive to OptiGUI, and is not supported by OptiFine.

!!! note
    Only applies, when `profession` doesn't start with a digit.

!!! example "Profession cleric"
    ```properties
    professions=minecraft:cleric
    ```

#### Profession format: `namespace:profession:levels`

!!! warning "Note"
    This format is exclusive to OptiGUI, and is not supported by OptiFine.

!!! warning "Caution"
    `levels` can't contain spaces, because it is the list separator character.

!!! example "Profession fletcher (leves 1, 3, 4)"
    ```properties
    professions=minecraft:fletcher:1,3-4
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

!!! warning "Note"
    OptiGUI doens't support the `#!properties profession=none`.

!!! tip
    OptiGUI supports professions from other mods, if prefixed with a namespace.

!!! tip
    OptiGUI supports wandering trader with `#!properties profession=_wandering_trader`. It is always level 1.

## Creative inventory & everything else

!!! note "Container"
    Requires `#!properties container=creative`

!!! abstract "Texture path"
    See example

### `texture`

!!! note
    The creative inventory GUI does not have a default texture, so it has to specify the path of the texture to replace. Use [`texture.<path>`](/format/#texturepath).

### `texture.<path>`

!!! note "Required"
    At least one `texture.<path>` is required.

Replacement for any GUI texture.

`<path>` is relative to `/assets/minecraft/textures/gui/`.

The creative inventory GUI does not have a default texture, so it has to use path textures.

!!! example "Example for creative inventory"
    In `/assets/minecraft/optifine/gui/container/creative/creative_desert.properties`:

    ```properties
    container=creative
    biomes=desert
    texture.container/creative_inventory/tab_inventory=tab_inventory_desert
    texture.container/creative_inventory/tabs=tabs_desert
    texture.container/creative_inventory/tab_items=tab_items_desert
    texture.container/creative_inventory/tab_item_search=tab_item_search_desert
    ```
