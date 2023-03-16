# Basic information & syntax

OptiGUI supports a syntax very similar to the [OptiFine syntax](https://optifine.readthedocs.io/syntax.html). This page has some corrections. Unused syntax was removed.

## File naming rules

For any file in a resource pack to be visible to the game, it must follow a set of rules:

* Must be entirely lowercase
* Must not contain spaces
* Must only contain characters `a-z 0-9 _`

> It must match the regular expression `^[a-z0-9_]+$`

Textures must be in [PNG](https://en.wikipedia.org/wiki/Portable_Network_Graphics) format with the `.png` extension.

All text files must be encoded in UTF-8. Do not use an ASCII encoding.

## File structure

Properties files are simple [Java properties](https://en.wikipedia.org/wiki/.properties) text files. Each line is a property, specified as `name=value`.

```properties
# Comments begin with a hashtag
property1=value
property2=some_other_value

# Blank lines are allowed
property3=yet_another_value
```

All property names are case-sensitive: `renderpass` is not the same as `renderPass`. The order of properties within the file does not matter. Many properties have default values, and can be omitted.

## Paths

The folder structure within a resource pack is deeply nested, so OptiGUI has some shortcuts to make paths easier to write. Any of these options can be used to specify the same path.

Always use forward slashes (`/`) to separate folders.

!!! warning "Caution"
    Regardless of operating system (*Windows, Mac, \*nix*), do not use backslashes (`\`), or the game will not properly recognize the path.

The table below summarises path shortcuts:

Symbol       | Resolves to
-------------|-----------------------------------------------------------
`./`         | Relative to the directory the properties file is in
`../`        | Relative to the parent directory the properties file is in
`~`          | `/assets/minecraft/optifine/`
`namespace:` | `/assets/namespace/`

### Bare filename

Bare filenames with no slashes will refer to the current directory, just like `./`.

!!! warning "Note"
    This behavior is different from the one described in the OptiFine documentation. Please use `./` or `~` explicitly.

```properties
texture=texture.png
```

### Dot and dot-dot

You can use `./` to denote the current directory, regardless of location. `..` can be used to travel up a folder, into the parent directory.

```properties
texture=./texture.png
texture=../texture.png
texture=../../subfolder/texture.png
```

### Tildes

The tilde character (`~`) can be used to refer to `/assets/minecraft/optifine/`.

```properties
texture=~/texture.png
texture=~/subfolder/texture.png
```

### Namespaces

An optional namespace prefix can be added. This example refers to `/assets/minecraft/textures/entity/creeper/creeper.png`:

```properties
texture=minecraft:textures/entity/creeper/creeper.png
```

For textures used by other mods, the namespace will be something other than `minecraft:`

```properties
texture=MODID:subfolder/texture.png
```

This refers to `/assets/MODID/subfolder/texture.png`, not to `/assets/minecraft/MODID/subfolder/texure.png`.

Namespaces can also apply to blocks, items, and biome IDs.

!!! tip
    In OptiGUI, namespaces can also apply to villager professions.

## Biomes

For features that call for a list of biomes, use [this page](https://minecraft.gamepedia.com/Biome#Biome_IDs). Biomes added by mods can also be used.

```properties
biomes=ocean deep_ocean river minecraft:beach
```

## Strings

This section is about matching strings and values using different matching methods. Strings can be matched in several ways.

!!! info "Important"
    Any backslashes must be doubled. Matching backslashes within a regular expression or wildcard must be quadrupled.

    ✅ Correct: `name=regex:\\d+`, `name=regex:\\\\`, `name=/\\/\\`

    ❌ Wrong: `name=regex:\d+`, `name=regex:\\` (for matching \\), `name=/\/\\` (missing a backslash)

### Exact value

For strings, you may either type the string directly: `Letter to Herobrine` matches the exact string `Letter to Herobrine` and nothing else.

!!! warning "Note"
    OptiGUI doens't support the [raw syntax](https://optifine.readthedocs.io/syntax.html#raw).

### Wildcards

Wildcards are shorter versions of regular expressions, in that they only support two unique constructs:

* The symbol `*` matches any text, as long as it exists.
* The symbol `?` matches any text, regardless of its presence.

The wildcard `*` is equivalent to the regular expression `.+` ([RegExr](https://regexr.com/7a982)). The wildcard `?` is equivalent to the regular expression `.*` ([RegExr](https://regexr.com/744fh)).

Wildcard patterns must start with either `pattern:` or `ipattern:`.

=== "pattern"
    `pattern` is case-sensitive.

    Example: `pattern:?Letter to *`

    ✅ Matches: `Letter to Herobrine`, `Letter to a creeper`, `My Letter to John`

    ❌ Does not match: `letter to Herobrine` (case sensitivity), `Letter from Herobrine` (doesn’t match "to")

=== "ipattern"
    `ipattern` is case-insensitive, meaning that “ABC” and “abc” are viewed the same.

    Example: `ipattern:Letter to *`

    ✅ Matches: `Letter to Herobrine`, `Letter to a creeper`, `letter to Herobrine`, `letter to STEVE`

    ❌ Does not match: `A letter to CJ` ("A" does not match the beginning), `Letter from Herobrine` (doesn’t match "to")

### Regular expressions

[Regular expressions](https://en.wikipedia.org/wiki/Regular_expression) are strings that create patterns that other strings can be matched against. Patterns can be as simple as `.`, to IP address validation. OptiGUI supports two types of regular expressions, depending on the case-sensitivity.

!!! warning "Note"
    OptiGUI does not support [regular expression flags](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Regular_Expressions#advanced_searching_with_flags).

The syntax understood by OptiGUI is the [Java syntax](https://docs.oracle.com/javase/tutorial/essential/regex/). OptiGUI regular expressions are not multiline and are not global.

Regular expressions must start with either `regex:` or `iregex:`.

=== "regex"
    A regular expression.

    Example: `regex:Letter (to|from) .*`

    ✅ Matches: `Letter to Herobrine`, `Letter from Herobrine`

    ❌ Does not match: `letter to Herobrine` (letter case), `A Letter to Herobrine` ("A" is not in expression)

=== "iregex"
    `iregex` is similar to `regex`, except that it is case-insensitive.

    Pattern: `iregex`

    Example: `iregex:Letter (to|from) .*`

    ✅ Matches: `Letter to Herobrine`, `Letter from Herobrine`, `letter to Herobrine`, `LETTER TO HEROBRINE`

    ❌ Does not match: `A Letter to Herobrine` ("A" is not in expression), `LETTER TOFROM HEROBRINE` ("TOFROM" does not match neither "to", nor "from"; can’t match both)

## Numbers

Numbers can be matched simply by typing the number. Additionally, you can match more than one number as well as a range of numbers with lists and ranges.

### Ranges

!!! info "Important"
    There is no range for less than or equal to, specify the lower bound explicitly: `0-100` instead of `-100`.

Inclusive ranges between numbers are defined with a `-` between those digits. If there is no number present on the right side of the `-`, the range will match to positive infinity.

Ranges can be combined and intermixed with lists.

For example,

```properties
# 1, 2, 3
numbers=1-3

# Multiple ranges
# 1 through 3, or 6, or 8, or 10 through 15
# 1, 2, 3, 6, 8, 10, 11, 12, 13, 14, 15
numbers=1-3 6 8 10-15

# Greater than or equal to
# 100, or 200, or 5340, or 25902, etc.
numbers=100-

# Negative number, not a range
# Only matches negative 100, not -4, -7, or -101
numbers=-100
```

Since 1.18, negative values can also be specified. When used in a range, they must be surrounded by parentheses.

To use negative numbers in a range, parentheses must be around those negative numbers.

```properties
list=(-3)-(-1)
```

These can be combined to create vast ranges of possible numeric values.

## Lists

Lists are defined with a space between each item.

For example,

```properties
numbers=1 2 3 4 5 6
numbers=10 70 23 -6 210
numbers=(-100)-200 500 900-
biomes=ocean deep_ocean river minecraft:beach
```
