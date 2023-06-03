# Migration guide

This page contains resources to migrate OptiFine extensions to the new OptiGUI ini files, and equivalents of OptiFine.

## Migrate OptiFine extensions

### Migrate containers

`#!properties container=_something` usually becomes `#!ini [something]`. See the table to migrate each container:

| OptiFine container   | OptiGUI replacement                                                                                     |
|----------------------|---------------------------------------------------------------------------------------------------------|
| `_cartography_table` | `#!ini [cartography_table]`                                                                             |
| `_chest_boat`        | `#!ini [chest_boat]`, `#!properties variants=<variants>` becomes `#!ini chest_boat.variants=<variants>` |
| `_grindstone`        | `#!ini [grindstone]`                                                                                    |
| `_loom`              | `#!ini [loom]`                                                                                          |
| `_smithing_table`    | `#!ini [smithing_table]`                                                                                |
| `_stonecutter`       | `#!ini [stonecutter]`                                                                                   |

### Migrate extension properties

Extension properties usually become their own groups. See the table to migrate each extension property:

| OptiFine extension property                          | OptiGUI replacement                                                                                |
|------------------------------------------------------|----------------------------------------------------------------------------------------------------|
| `#!properties _barrel=true`                          | `#!ini [barrel]`                                                                                   |
| `#!properties _minecart=true`                        | `#!ini [chest_minecart]` or `#!ini [hopper_minecart]`                                              |
| `#!properties _furnace`                              | `#!ini [furnace]`. `#!properties variants` have been removed from `#!properties container=furnace` |
| `#!properties _blast`, `#!properties _blast_furnace` | `#!ini [blast_furnace]`                                                                            |
| `#!properties _smoker`                               | `#!ini [smoker]`                                                                                   |
| `#!properties _camel=true`                           | `#!ini [camel]`                                                                                    |
| `#!properties _zombie_horse=true`                    | `#!ini [zombie_horse]`                                                                             |
| `#!properties _skeleton_horse=true`                  | `#!ini [skeleton_horse]`                                                                           |
| `#!properties _wandering_trader=true`                | `#!ini [wandering_trader]`                                                                         |

## OptiFine equivalents

### container

`#!properties container=something` becomes `#!ini [the_identifier_of_something]`.

!!! tip
    Go to the [Minecraft Wiki](https://minecraft.fandom.com). Select the container, scroll down to **Data values/ID/Java Edition**, and copy the text from the **Identifier** column. This identifier is used by the `/give` and `/summon` commands.

### texture

`#!properties texture=replacement.png` becomes `#!ini replacement=replacement.png`.

Paths may need to be rewritten. Please refer to [paths](/syntax/#paths).

!!! note
    OptiGUI requires the file extension to be specified, otherwise it won't find the resource.

```ini
texture.<path1>=replacement_1
texture.<path2>.png=replacement_2.png

# becomes

[#1]
interaction.texture = minecraft:textures/gui/<path1>.png
replacement = replacement_1.png

[#2]
interaction.texture = minecraft:textures/gui/<path2>.png
replacement = replacement_2.png
```

### name

Replace the `pattern`, `ipattern`, `regex`, and `iregex` prefixes with name postfixes as shown below:

```ini
name = Name
# becomes
name = Name

name = pattern:Name
# becomes
name.wildcard = Name

name = ipattern:Name
# becomes
name.wildcard.ignore_case = Name

name = regex:Name
# becomes
name.regex = Name

name = iregex:Name
# becomes
name.regex.ignore_case = Name
```

Keep backslashes as they are.

### biomes

Keep it as-is.

### heights

Keep it as-is.

### large

Becomes [`#!ini chest.large`](/format/#chestlarge).

### trapped

See [Chest & trapped chest](/format/#chest-trapped-chest).

### christmas

```ini
# If true
date = dec@24-26

# If false
date = 1 2 3 4 5 6 7 8 9 10 11 dec@1-23 dec@27-31
```

### ender

Becomes `#!ini [ender_chest]`

### levels

Becomes [`#!ini beacon.levels`](/format/#beaconlevels).

### professions

Becomes [`villager.professions`](/format/#villagerprofessions). See the following example for rewriting rules:

```ini
professions = profession
# becomes
villager.professions = profession

professions = profession1 profession2
# becomes
villager.professions = profession1 profession2

professions = profession:1
# becomes
villager.professions = profession@1

professions = profession:2-3
# becomes
villager.professions = profession@2-3

professions = profession:1,2-3
# becomes
villager.professions = profession@1 profesions@2-3
```

`#!properties profession=none` is **not supported**.

### variants

Becomes a group in the INI file.

```ini
variants = variant
# beacomes
[variant]

variants = variant1 variant2
# becomes
[variant1 variant2]
```

### colors

* For llamas, it becomes [`#!ini llama.colors`](/format/#llamacolors)
* For shulker boxes, add the color in front of the shulker box like this: `<color>_shulker_box`, and it becomes a group
