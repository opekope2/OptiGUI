# OptiGUI INI resources

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}

You can define a texture replacement for each inventory GUI, and apply them based on different criteria.

## File naming rules

Each file in a resource pack must only contain characters `a-z 0-9 _`. All lowercase, no whitespace. Otherwise, the game will not recognize it.

> Each file name must match the regular expression `^[a-z0-9_]+$`

Textures must be [PNG](https://en.wikipedia.org/wiki/Portable_Network_Graphics) images with `.png` extension.

!!! note
    When specifying texture paths, **do not** forget file extensions, otherwise OptiGUI **will not** find the resources!

All text files must be encoded in UTF-8. Do not use an ASCII encoding.

For each container GUI texture to replace, create a `.ini` file in `/assets/optigui/gui/` folder (or any of its subfolders in any depth) of the resource pack.

## File structure

OptiGUI uses [INI](https://en.wikipedia.org/wiki/INI_file) files, kind of like OptiFine, but uses more features of it.

All selectors are case-sensitive: `name` is not the same as `Name`. The order of selectors within the file or within a group does not matter.

### Groups

Groups start with `#!ini [square bracketed]` identifiers. Place the identifier of the container to replace the GUI.

!!! tip
    Go to the [Minecraft Wiki](https://minecraft.wiki). Select a container (for example, a chest, horse, crafting table, etc.), scroll down to **Data values/ID/Java Edition**, and copy the text from the **Identifier** column. This identifier is used by the `/give` and `/summon` commands.

If multiple selectors are specified in a group, they **all must match** in order to apply the replacement texture. If incompatible selectors are specified (for example, `llama.colors` to `[chest]`, it will **never** match).

!!! example
    ```ini
    [chest]
    # Selectors here apply to minecraft:chest
    # If namespace is omitted, the default is minecraft
    selector=value

    [minecraft:barrel]
    # Starts a new group
    # Selectors here apply to minecraft:barrel
    selector=value_for_barrel

    [horse minecraft:llama]
    # Selectors here apply to both horses and llamas
    # Namespaces and the lack of them can be mixed
    # The default namespace is minecraft
    white_spaces = are_trimmed
    # Is the same as
    white_spaces=are_trimmed

    [chest #2]
    # [chest] is not allowed again
    # Anything specified after a hashtag is ignored
    # Useful when want to replace the GUI of the same container, but with different selectors

    [#3 chest]
    # Hashtags can be anywhere between the square brackets
    # Remember, the group accepts a list of identifiers, a hashtag's scope lasts until the next whitespace
    # Here, only #3 is ignored, but not chest
    ```

## Types

### Path

!!! warning "Caution"
    Always use forward slashes (`/`) to separate folders.

    Regardless of operating system (*Windows, Mac, \*nix*), do **not** use backslashes (`\`), or the game will not properly recognize the path.

OptiGUI paths can be specified in two ways: relative and absolute.

```ini
# Relative path (relative to the folder the INI file is in)
path=texture.png
path=subfolder/texture.png

# You can use current and parent directory
path=./texture.png
path=../other/texture.png

# Absolute (namespace prefix)
path=minecraft:textures/gui/container/crafting_table.png
```

!!! warning "Caution"
    Contrary to OptiFine, OptiGUI **requires** the file extension (`.png` here) to be specified. If it is not specified, OptiGUI will **not** find the texture!

!!! note
    Tildes (`~`) are **not** supported by OptiGUI. When loading OptiFine `.properties`, OptiGUI will expand them.

### String

OptiGUI supports exact values, and case-sensitive and case-insensitive variants of wildcards and regexes. However, these are not prefixed with `regex:`, `iregex:`, `pattern:`, or `ipattern:`. The accepted type (wildcard, regex, ...) depends on the selector, and always noted explicitly.

!!! note
    Any backslashes must be doubled. Matching backslashes within a regular expression or wildcard must be quadrupled.

    ✅ Correct: `name=regex:\\d+`, `name=regex:\\\\`, `nbt.display.name=/\\/\\`

    ❌ Wrong: `name=regex:\d+`, `name=regex:\\` (for matching \), `name=/\/\\` (missing a backslash)

#### Exact value

`Letter to Herobrine` matches `Letter to Herobrine`, and nothing else.

#### Wildcard

You may use the following characters to match other characters:

| Character | Regex equivalent | Meaning                      |
|-----------|------------------|------------------------------|
| `?`       | `.`              | Matches exactly 1 character  |
| `*`       | `.*`             | Matches 0 or more characters |

#### Regex

[Regular expressions](https://en.wikipedia.org/wiki/Regular_expression) "patterns" other strings can be matched against.

OptiGUI understands the [Java syntax](https://docs.oracle.com/javase/tutorial/essential/regex/). [Expression flags](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Regular_Expressions#advanced_searching_with_flags) are **not** supported.

You can use the [RegExr](https://regexr.com/) tool to create and test your regexes. When pasting into az OptiGUI INI file, make sure to duplicate all backslashes (`\`), as OptiGUI will unescape any escape sequences Java supports (like hexadecimals, line breaks, and unicode codepoints).

### Number

Numbers can be specified as a signed or unsigned integer.

!!! example
    ```ini
    number = 1
    ```

#### Range

Inclusive ranges between numbers are defined with a `-` between the minimum and the maximum number. The right side is optional: if it is omitted, the upper bound will be positive infinity.

!!! tip
    OptiGUI usually allows specifying ranges as [lists](#list)

!!! example
    ```ini
    # 1, 2, 3
    numbers = 1-3

    # Multiple ranges
    # 1 through 3, or 6, or 8, or 10 through 15
    # 1, 2, 3, 6, 8, 10, 11, 12, 13, 14, 15
    numbers = 1-3 6 8 10-15

    # Greater than or equal to
    # 100, or 200, or 5340, or 25902, etc.
    numbers = 100-

    # Negative number, not a range
    # Only matches negative 100, not -4, -7, or -101
    numbers = -100

    # Negative numbers must be surrounded with parenthesis
    numbers = (-1)-(-3)
    ```

!!! note
    There is **no** range to specify `≤` relation, you need to specify the lower bound: `0-100`. `-100` is a number, and will only match `-100`.

!!! note
    The `range:` prefix is **not supported** by OptiGUI and will be ignored when loading OptiFine `.properties`.

### Boolean

Booleans are case-insensitive.

Possible values: `true`, `false`. Everything else is ignored.

### List

Lists can hold multiple elements separated with a whitespace. The element type is specified by the selector, it can be any type, like numbers or booleans. Strings are also supported, however, whitespaces within a string will start a new list element (and therefore, cannot be specified in strings inside lists).

If multiple elements are specified in a list, **any of them** can match in order to replace a texture.

## Pseudo-selectors

These are not real selectors used for selecting entities and block entities, just have the same syntax. OptiGUI processes these specially when loading the resource.

### `replacement`

**Required**{.chip-darkblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

[Path](#path) to the replacement texture for the default GUI texture of the container. This is not really a selector, as it is not used to match against an interaction, but specifies the replacement if all other selectors match.

### `load.priority`

**Optional**{.chip-lightblue}
**OptiGUI 2.2.0-alpha.1+**{.chip-darkgreen}
**Removed in OptiGUI 3.0.0-alpha.1**{.chip-red}
**Minecraft 1.18+**{.chip-lightgreen}

A single [integer](#number) (not range) specifying the load priority of the group. Default load priority is `0`. Higher load priority means earlier processing while evaluating the loaded filters.

!!! warning
    Not supported on OptiGUI 3 due to performance implications

## Interaction selectors

### `interaction.texture`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

[Path](#path) to the texture to replace (the default texture of the container). When left empty, OptiGUI looks up the default texture of the specified containers.

### `interaction.hand`

**Optional**{.chip-lightblue}
**OptiGUI 2.2.0-alpha.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

The hand the player started the interaction with. Possible values:

* `main_hand`
* `off_hand`

### `interaction.screen_title`

**Upcoming**{.chip-purple}

## Common selectors

### `name`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [String](#string) specifying the exact value of the name of the entity or block entity.

### `name.wildcard`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [String](#string) specifying the case-sensitive [wildcard](#wildcard) to match against the name of the entity or block entity.

### `name.wildcard.ignore_case`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [String](#string) specifying the case-insensitive [wildcard](#wildcard) to match against the name of the entity or block entity.

### `name.regex`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [String](#string) specifying the case-sensitive [regex](#regex) to match against the name of the entity or block entity.

### `name.regex.ignore_case`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [String](#string) specifying the case-insensitive [regex](#regex) to match against the name of the entity or block entity.

### `biomes`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](#list) of biome identifiers specifying the biomes of the entity or block entity where this replacement applies.

### `heights`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](#list) of [integers](#number) and [ranges](#range) specifying the heights (Y coordiantes) of the entity or block entity, where this replacement applies.

## Independent selectors

### `date`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](#list) of dates specifying when the texture should be replaced.

A date can be specified by the name, the first 3 characters of its name, or number of the month, and an optional day [number](#number) or day [range](#range) separated with a `@` character:

!!! example
    ```ini
    # January
    date = january
    date = jan
    date = 1
    # These are all equivalent to:
    date = jan@1-31

    # October 1-5, 11-15, 21-25
    date = oct@1-5 10@11-15 october@21-25
    # The following is not valid:
    invalid_date = october@1-5,11-15,21-25

    # Christmas
    christmas = dec@24-26

    # Not Christmas
    not_christmas = 1 2 3 4 5 6 7 8 9 spooktober 11 dec@1-23 dec@27-31
    ```

#### Supported month abbreviations

| Month     | Abbreviations                        |
|-----------|--------------------------------------|
| January   | `1`, `jan`, `january`                |
| February  | `2`, `feb`, `february`               |
| March     | `3`, `mar`, `march`                  |
| April     | `4`, `apr`, `april`                  |
| May       | `5`, `may`                           |
| June      | `6`, `jun`, `june`                   |
| July      | `7`, `jul`, `july`                   |
| August    | `8`, `aug`, `augustus`               |
| September | `9`, `sep`, `september`              |
| October   | `10`, `oct`, `october`, `spooktober` |
| November  | `11`, `nov`, `november`              |
| December  | `12`, `dec`, `december`              |

## Player selectors

### `player.name`

**Optional**{.chip-lightblue}
**OptiGUI 2.3.0-beta.3+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [String](#string) specifying the exact value of the name of the entity or block entity.

### `player.name.wildcard`

**Optional**{.chip-lightblue}
**OptiGUI 2.3.0-beta.3+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [String](#string) specifying the case-sensitive [wildcard](#wildcard) to match against the name of the entity or block entity.

### `player.name.wildcard.ignore_case`

**Optional**{.chip-lightblue}
**OptiGUI 2.3.0-beta.3+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [String](#string) specifying the case-insensitive [wildcard](#wildcard) to match against the name of the entity or block entity.

### `player.name.regex`

**Optional**{.chip-lightblue}
**OptiGUI 2.3.0-beta.3+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [String](#string) specifying the case-sensitive [regex](#regex) to match against the name of the entity or block entity.

### `player.name.regex.ignore_case`

**Optional**{.chip-lightblue}
**OptiGUI 2.3.0-beta.3+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [String](#string) specifying the case-insensitive [regex](#regex) to match against the name of the entity or block entity.

### `player.biomes`

**Optional**{.chip-lightblue}
**OptiGUI 2.3.0-beta.3+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](#list) of biome identifiers specifying the biomes of the entity or block entity where this replacement applies.

### `player.heights`

**Optional**{.chip-lightblue}
**OptiGUI 2.3.0-beta.3+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](#list) of [integers](#number) and [ranges](#range) specifying the heights (Y coordiantes) of the entity or block entity, where this replacemen

## Comparator selectors

### `comparator.output`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.3+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](#list) of [integers](#number) and [ranges](#range) specifying the redstone comparator output of the entity or block entity, where this replacement applies.

## Anvil

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/anvil.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [anvil chipped_anvil damaged_anvil]
    ```

## Barrel

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/generic_54.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

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

A [list](#list) of [integers](#number) and [ranges](#range) specifying the levels of beacon power to apply to (how many bases of blocks).

## Book

**OptiGUI 2.1.3+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/book.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [written_book]
    book.page.current = 1 3-4
    book.page.count = 5 10-15
    ```

### `book.page.current`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.3+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](#list) of [integers](#number) and [ranges](#range) specifying the the current page of the book, where this replacement applies.

### `book.page.count`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.3+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](#list) of [integers](#number) and [ranges](#range) specifying the the page count of the book, where this replacement applies.

## Book and Quill

**OptiGUI 2.1.3+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/book.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [book selectors](#book)

!!! example
    ```ini
    [writable_book]
    ```

## Brewing stand

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/brewing_stand.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [brewing_stand]
    ```

## Cartography table

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/cartography_table.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

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

A single [boolean](#boolean) specifying if the texture of a double chest should be replaced.

## Trapped chest

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/generic_54.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors), [chest selectors](#chest)

!!! example
    ```ini
    [trapped_chest]
    chest.large = true
    ```

## Ender chest

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/generic_54.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

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
**Minecraft 1.19-1.21.1**{.chip-lightgreen}
**Removed in Minecraft 1.21.2**{.chip-red}

!!! warning
    Entity `minecraft:chest_boat` was split in [snapshot 24w39a](https://minecraft.wiki/w/Java_Edition_24w39a#Non-mob_entities). Use the new entity IDs instead of this selector.
    Use [conditional loading](#ifmods) to support multiple versions of the game.

A [list](#list) of [strings](#string) specifying the wood type of the chest boat. Possible values:

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

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [chest_minecart]
    ```

## Crafting table

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/crafting_table.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [crafting_table]
    ```

## Dispenser

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/dispenser.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [dispenser]
    ```

## Dropper

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/dispenser.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [dropper]
    ```

## Enchanting table

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/enchanting_table.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [enchanting_table]
    ```

## Furnace

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/furnace.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [furnace]
    ```

## Blast furnace

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/blast_furnace.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [blast_furnace]
    ```

## Smoker

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/smoker.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [smoker]
    ```

## Grindstone

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/grindstone.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [grindstone]
    ```

## Hopper

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/hopper.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [hopper]
    ```

## Hopper minecart

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/hopper.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

!!! example
    ```ini
    [hopper_minecart]
    ```

## Horse

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [donkey selectors](#donkey)

!!! example
    ```ini
    [horse]
    ```

### `horse.has_saddle`

**Optional**{.chip-lightblue}
**OptiGUI 2.2.0-alpha.1**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A single [boolean](#boolean) specifying if the entity needs to be or not be saddled.

### `horse.variants`

**Optional**{.chip-lightblue}
**OptiGUI 2.2.0-alpha.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](#list) of [strings](#string) specifying the horse's variant. Possible values:

* `black`
* `brown`
* `chestnut`
* `creamy`
* `dark_brown`
* `gray`
* `white`

### `horse.markings`

**Optional**{.chip-lightblue}
**OptiGUI 2.2.0-alpha.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](#list) of [strings](#string) specifying the marking on a horse. Possible values:

* `black_dots`
* `none`
* `white`
* `white_dots`
* `white_field`

## Donkey

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [`horse.has_saddle`](#horsehas_saddle)

!!! example
    ```ini
    [donkey]
    ```

### `donkey.has_chest`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.3+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A single [boolean](#boolean) specifying if the entity needs to have or not have a chest.

## Mule

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [donkey selectors](#donkey)

!!! example
    ```ini
    [mule]
    ```

## Llama

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [donkey selectors](#donkey)

!!! example
    ```ini
    [llama trader_llama]
    llama.colors = red green blue
    ```

### `llama.colors`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](#list) of [strings](#string) specifying the llama's carpet color. Possible values:

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

### `llama.variants`

**Optional**{.chip-lightblue}
**OptiGUI 2.2.0-alpha.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](#list) of [strings](#string) specifying the llama's variant. Possible values:

* `brown`
* `creamy`
* `gray`
* `white`

## Trader llama

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [donkey selectors](#donkey), [llama selectors](#llama)

!!! example
    ```ini
    [llama trader_llama]
    llama.colors = red green blue
    ```

## Skeleton horse

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [skeleton_horse]
    ```

## Zombie horse

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [zombie_horse]
    ```

## Camel

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.20+**{.chip-lightgreen}
**Minecraft 1.19.3+ with 1.20 experiments**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [`horse.has_saddle`](#horsehas_saddle)

!!! example
    ```ini
    [camel]
    ```

## Lectern

**OptiGUI 2.1.0+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/horse.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors), [book selectors](#book)

!!! example
    ```ini
    [lectern]
    ```

## Loom

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/loom.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [loom]
    ```

## Shulker boxes

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/shulker_box.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors), [comparator selectors](#comparator-selectors)

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

## Sign

**Not supported**{.chip-red}

Sign edit GUI renders a block model of the sign block, it doesn't have a texture.

## Hanging sign

**OptiGUI 2.1.3+**{.chip-darkgreen}
**Minecraft 1.20+**{.chip-lightgreen}
**Minecraft 1.19.3+ with 1.20 experiments**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/hanging_signs/*.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [acacia_hanging_sign acacia_wall_hanging_sign]
    [bamboo_hanging_sign bamboo_wall_hanging_sign]
    [birch_hanging_sign birch_wall_hanging_sign]
    [cherry_hanging_sign cherry_wall_hanging_sign]
    [crimson_hanging_sign crimson_wall_hanging_sign]
    [dark_oak_hanging_sign dark_oak_wall_hanging_sign]
    [jungle_hanging_sign jungle_wall_hanging_sign]
    [mangrove_hanging_sign mangrove_wall_hanging_sign]
    [oak_hanging_sign oak_wall_hanging_sign]
    [spruce_hanging_sign spruce_wall_hanging_sign]
    [warped_hanging_sign warped_wall_hanging_sign]
    ```

## Smithing table

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/smithing.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [smithing_table]
    ```

## Stonecutter

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/stonecutter.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

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

A [list](#list) of villager professions specifying villager professions with optional levels.

Villager professions can be specified by an optional namespace and the profession name name, and an optional [level](#number) or [level range](#range) separated with a `@` character:

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

#### Vanilla professions

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

### `villager.type`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.3+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

A [list](#list) of [strings](#string) specifying villager types (which biome was it born in). This is how its clothing looks like. Possible values:

* `desert`
* `jungle`
* `plains`
* `savanna`
* `snow`
* `swamp`
* `taiga`

## Wandering trader

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/villager2.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

!!! example
    ```ini
    [wandering_trader]
    ```

## Survival inventory

**OptiGUI 2.1.0-beta.1+**{.chip-darkgreen}
**Minecraft 1.18+**{.chip-lightgreen}

**Texture path:** `minecraft:textures/gui/container/inventory.png`

Supports the following additional selectors: [common selectors](#common-selectors), [interaction selectors](#interaction-selectors), [independent selectors](#independent-selectors)

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

## Conditional loading

OptiGUI ^2.1.0-beta.3+^ supports conditionally loading OptiGUI resources.

Conditional loading selectors always begin with `if.` (except for `if`), and should be added to [groups](#groups) to specify when (not) to load the group (just like normal selectors). These will be evaluated when loading resources instead of when replacing textures.

### `if`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.3+**{.chip-darkgreen}

A single [boolean](#boolean) specifying if the property should be loaded. Set to `false` to prevent the group from loading.

!!! example
    ```ini
    [container]
    if = false
    ```

### `if.mod.optigui.version.at_least`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.3+**{.chip-darkgreen}
**Removed in OptiGUI 2.3.0-alpha.1**{.chip-red}

Use [`if.mods = optigui>=version`](#ifmods) instead of `#!properties if.mod.optigui.version.at_least = version`.

### `if.mods`

**Optional**{.chip-lightblue}
**OptiGUI 2.2.0-alpha.1+**{.chip-darkgreen}

A [list](#list) of the following things:

The mod ID, and optionally an operator and a [semantic version](https://semver.org) specifying the version of a mod required to load the group. If an operator and a version is omitted, OptiGUI will check for the presence of the mod. Useful to prevent loading on old versions, which don't support some features.

If multiple mods are specified, all must match to load the resource. If a mod is not present, it will not match regardless of the version specified.

!!! note
    There is no operator currently to match only when the mod is not present

!!! example
    ```ini
    if.mods = optigui                               # Checks for the presence of optigui
    if.mods = optigui>=2.3.0-alpha.1                # Checks, if OptiGUI is 2.3.0-alpha.1 or newer
    if.mods = minecraft~1.20.1                      # Checks if Minecraft is >=1.20.1 and <1.21
    if.mods = optigui>=2.3.0-alpha.1 optigui<2.4.0  # Checks if OptiGUI meets multiple criteria
    if.mods = minecraft fabric-api java>=17         # Mix-n-match (all of them has to match)
    ```

#### Operators

| Opertor | Checks, if a mod...                                                                  |
|---------|--------------------------------------------------------------------------------------|
| `>`     | is newer, than the given version                                                     |
| `>=`    | is at least as new, as the given version                                             |
| `<`     | is older, than the given version                                                     |
| `<=`    | is at most as old, as the given version                                              |
| `=`     | has the same version, as the given version                                           |
| `~`     | is at least as new, and has the same minor (**X.Y**.z) version, as the given version |
| `^`     | is at least as new, and has the same major (**X**.y.z) version, as the given version |
