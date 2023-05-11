# Basic information & syntax

OptiGUI supports two syntaxes:

* OptiGUI syntax ^OptiGUI\ 2.1.0-beta.1\ or\ later^
* [OptiFine syntax](https://optifine.readthedocs.io/syntax.html).

OptiGUI syntax is only supported for OptiGUI files, and the OptiFine syntax is only supported for OptiFine files. Do not mix them.

This page describes the OptiGUI syntax. For the OptiFine syntax, visit the link above.

## File naming rules

!!! info
    Same as [OptiFine file naming rules](https://optifine.readthedocs.io/syntax.html#file-naming-rules).

## File structure

!!! info
    Different from [OptiFine file structure](https://optifine.readthedocs.io/syntax.html#file-structure), even if they look similar.

OptiGUI uses [INI](https://en.wikipedia.org/wiki/INI_file) files, kind of like OptiFine, but uses more features of it.

```ini
# Comments start with a hashtag
; or a semicolon
```

All property names are case-sensitive: `name` is not the same as `Name`. The order of properties within the file or within a group does not matter.

### Groups

Groups start [square bracketed] identifiers. Place the identifier of the container to replace the GUI.

!!! tip
    Go to the [Minecraft Wiki](https://minecraft.fandom.com). Select a container (for example, a chest, horse, crafting table, etc.), scroll down to `Data values/ID/Java Edition`, and copy the text from the `Identifier` column. This identifier is used by the `/give` and `/summon` commands.

```ini
[chest]
# Properties here apply to minecraft:chest
# If namespace is omitted, the default is minecraft
property=value

[minecraft:barrel]
# Properties here apply to minecraft:barrel
property=value_for_barrel

[horse minecraft:llama]
# Properties here apply to both horses and llamas
# Namespaces and the lack of them can be mixed
white_spaces = are_trimmed
# Is the same as
white_spaces=are_trimmed

[chest #2]
# [chest] is not allowed again
# Anything specified after a hashtag is ignored
# Useful when want to replace the GUI of the same container, but with different properties

[#3 chest]
# Hashtags can be anywhere between the square brackets
# Remember, the group accepts a list of identifiers, a hashtag's scope lasts until the next whitespace
# Here, only #3 is ignored, but not chest
```

## Paths

!!! info
    Different from [OptiFine paths](https://optifine.readthedocs.io/syntax.html#paths), even if they look similar.

!!! warning "Caution"
    Always use forward slashes (`/`) to separate folders.

    Regardless of operating system (*Windows, Mac, \*nix*), do not use backslashes (`\`), or the game will not properly recognize the path.

OptiGUI paths can be specified in two ways: relative and absolute.

```ini
# Relative path (relative to the folder the INI file is in)
path=texture.png
path=subfolder/texture.png

# Absolute (namespace prefix)
path=minecraft:textures/gui/container/crafting_table.png
```

## Strings

!!! info
    Different from [OptiFine strings](https://optifine.readthedocs.io/syntax.html#strings)

OptiGUI supports exact values, and case-sensitive and case-insensitive variants of wildcards and regexes. However, these are not prefixed with `regex:`, `iregex:`, `pattern:`, or `ipattern:`. The accepted type (wildcard, regex, ...) is always noted explicitly.

## Numbers

!!! info
    Same as [OptiFine numbers](https://optifine.readthedocs.io/syntax.html#numbers).

### Ranges

!!! info
    Same as [OptiFine ranges](https://optifine.readthedocs.io/syntax.html#ranges), but the `range:` prefix is **not** supported.

!!! tip
    In OptiGUI, ranges are always parsed as a [list](#lists) of ranges.

## Booleans

Booleans are case-insensitive.

Possible values: `true`, `false`. Everything else means undefined.

## Lists

!!! info
    Same as [OptiFine lists](https://optifine.readthedocs.io/syntax.html#lists), but lists can hold any types, including [strings](#strings) (white space automatically starts a new list item) or [booleans](#booleans).
