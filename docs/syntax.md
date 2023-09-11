# Basic information & syntax

OptiGUI supports two syntaxes:

* OptiGUI syntax ^2.1.0-beta.1+^
* [OptiFine syntax](https://optifine.readthedocs.io/syntax.html).

!!! tip
    To prevent loading an OptiFine `.properties` file on OptiGUI, add `#!properties optigui.ignore=true` into it.

    OptiGUI files support [conditional loading](conditional.html).

OptiGUI syntax is only supported for OptiGUI files, and the OptiFine syntax is only supported for OptiFine files. Do not mix them.

This page describes the OptiGUI syntax. For the OptiFine syntax, visit the link above.

## File naming rules

Each file in a resource pack must only contain characters `a-z 0-9 _`. All lowercase, no whitespace. Otherwise, the game will not recognize it.

> Each file name must match the regular expression `^[a-z0-9_]+$`

Textures must be [PNG](https://en.wikipedia.org/wiki/Portable_Network_Graphics) images with `.png` extension.

All text files must be encoded in UTF-8. Do not use an ASCII encoding.

## File structure

OptiGUI uses [INI](https://en.wikipedia.org/wiki/INI_file) files, kind of like OptiFine, but uses more features of it.

```ini
# Comments start with a hashtag
; or a semicolon
```

All selectors are case-sensitive: `name` is not the same as `Name`. The order of selectors within the file or within a group does not matter.

### Groups

Groups start with `#!ini [square bracketed]` identifiers. Place the identifier of the container to replace the GUI.

!!! tip
    Go to the [Minecraft Wiki](https://minecraft.fandom.com). Select a container (for example, a chest, horse, crafting table, etc.), scroll down to **Data values/ID/Java Edition**, and copy the text from the **Identifier** column. This identifier is used by the `/give` and `/summon` commands.

If multiple selectors are specified in a group, they **all must match** in order to apply the replacement texture. If incompatible selectors are specified (for example, `llama.colors` to `[chest]`, it will **never** match).

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

## Paths

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

## Strings

OptiGUI supports exact values, and case-sensitive and case-insensitive variants of wildcards and regexes. However, these are not prefixed with `regex:`, `iregex:`, `pattern:`, or `ipattern:`. The accepted type (wildcard, regex, ...) depends on the selector, and always noted explicitly.

!!! note
    Any backslashes must be doubled. Matching backslashes within a regular expression or wildcard must be quadrupled.

    ✅ Correct: `name=regex:\\d+`, `name=regex:\\\\`, `nbt.display.name=/\\/\\`

    ❌ Wrong: `name=regex:\d+`, `name=regex:\\` (for matching \), `name=/\/\\` (missing a backslash)

### Exact value

`Letter to Herobrine` matches `Letter to Herobrine`, and nothing else.

### Wildcard

You may use the following characters to match other characters:

| Character | Regex equivalent | Meaning                      |
|-----------|------------------|------------------------------|
| `*`       | `.+`             | Matches 1 or more characters |
| `?`       | `.*`             | Matches 0 or more characters |

### Regex

[Regular expressions](https://en.wikipedia.org/wiki/Regular_expression) "patterns" other strings can be matched against.

OptiGUI understands the [Java syntax](https://docs.oracle.com/javase/tutorial/essential/regex/). [Expression flags](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Regular_Expressions#advanced_searching_with_flags) are **not** supported.

You can use the [RegExr](https://regexr.com/) tool to create and test your regexes. When pasting into az OptiGUI INI file, make sure to duplicate all backslashes (`\`), as OptiGUI will unescape any escape sequences Java supports (like hexadecimals, line breaks, and unicode codepoints).

## Numbers

Numbers can be specified as a signed or unsigned integer.

!!! example
    ```ini
    number = 1
    ```

### Ranges

Inclusive ranges between numbers are defined with a `-` between the minimum and the maximum number. The right side is optional: if it is omitted, the upper bound will be positive infinity.

!!! tip
    OptiGUI usually allows specifying ranges as [lists](#lists)

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

## Booleans

Booleans are case-insensitive.

Possible values: `true`, `false`. Everything else is ignored.

## Lists

Lists can hold multiple elements separated with a whitespace. The element type is specified by the selector, it can be any type, like numbers or booleans. Strings are also supported, however, whitespaces within a string will start a new list element (and therefore, cannot be specified in strings inside lists).

If multiple elements are specified in a list, **any of them** can match in order to replace a texture.

## Dates

Dates can be specified by the name, the first 3 characters of its name, or number of the month, and an optional day [number](#numbers) or day [range](#ranges) separated with a `@` character:

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

### Supported month abbreviations

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

## Professions

Villager professions can be specified by an optional namespace and the profession name name, and an optional [level](#numbers) or [levl range](#ranges) separated with a `@` character:

!!! example
    ```ini
    villager.professions = cleric minecraft:fisherman           # Cleric (any level) or fisherman (any level)
    villager.professions = fletcher@1 minecraft:fletcher@3-4    # Fletcher (levels 1, 3, 4)
    ```
