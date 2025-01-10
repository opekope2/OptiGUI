# OptiGUI JSON resources

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

!!! tip
    OptiGUI ships with an inspector.
    Press `F12` (default key binding) while a GUI screen is open, and OptiGUI will generate and copy a JSON resource to your clipboard.

## File structure

OptiGUI 3 adds support for a new, [JSON](https://json.org)-based resource format, with the following deviations allowed from the JSON standard (see [GSON code](https://github.com/google/gson/blob/gson-parent-2.10.1/gson/src/main/java/com/google/gson/stream/JsonReader.java#L300-L331) for the full list):

```json
# Comments
// are allowed
/*
Multi-line comments are allowed,
but can't be nested
*/
{
  UnquotedNames: "are allowed",
  'Single-quoted names': "are allowed",
  "Unquoted strings": are_allowed,
  "Single-quoted strings": 'are allowed',
  "Array elements can be separated by": ["commas", "or"; "semicolons"],
  "Unnecessary array separators": ["are treated as", null, /* null */, , ,],
  "Names and values can be separated": {
    "Using": "colons",
    "Using"= "equality signs",
    "Or"=> "arrows"
  },
  "Name/value pairs can be separated": {
    "Using": "commas";
    "Or": "semicolons"
  }
}
```

### `containers`

**Required**{.chip-darkblue}
**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

The identifiers of the blocks, entities, or items to change the GUI texture of.
This identifier is used by `/setblock`, `/summon`, and `/give` commands.

It can be specified as a single identifier:

```json
{
  "containers": "minecraft:villager"
}
```

Or a JSON array of identifiers. In this case, OptiGUI will change the textures of any of the specified blocks, entities, or items:

```json
{
  "containers": ["minecraft:villager", "minecraft:wandering_trader"]
}
```

!!! tip
    1. Go to the [Minecraft Wiki](https://minecraft.wiki).
    2. Search for a block, entity, or item, and go to its page
    3. Scroll down to **Data values/ID/Java Edition**
    4. Copy the text from the **Identifier** column

!!! tip
    If the namespace is `minecraft`, then it can be omitted.  
    For example, `waxed_lightly_weathered_cut_copper_stairs` is the same as `minecraft:waxed_lightly_weathered_cut_copper_stairs`

### `textures`

**Required**{.chip-darkblue}
**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

A JSON object specifying the original textures, and what textures to change those to.

```json
{
  "textures": {
    "mod:textures/gui/path/to/texture.png": "example:path/to/changed/texture.png"
  }
}
```

!!! tip
    If the namespace is `minecraft`, then it can be omitted.  
    For example, `waxed_lightly_weathered_cut_copper_stairs` is the same as `minecraft:waxed_lightly_weathered_cut_copper_stairs`

### `if`

**Optional**{.chip-lightblue}
**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

An [NBT filter](#filter) evaluated when loading the JSON resuorce. If it doesn't match, the JSON resource is not loaded.

### `match`

**Optional**{.chip-lightblue}
**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

An [NBT filter](#filter) evaluated when changing GUI screen textures.

!!! warning "Caution"
    Avoid creating two JSON resources, where `match` filters can match the same NBT.  
    OptiGUI chooses the least recently used JSON resource's NBT filter, and not the more specific one (for performance reasons), which can lead to the less specific NBT filter always being prioritized over the more specific filter.

## Filter

### JSON Object

A filter is a collection of [NBT Matchers](#nbt-matchers).  
When multiple matchers are placed into a JSON object, all of them has to match.

### JSON Array

A JSON array filter contains other [filters](#filter).  
When multiple filters are placed into a JSON array, any of them has to match.

### String

When only the `=` matcher is used in a JSON object, the string value can be written in place of the JSON object.
See [`=` matcher's syntax shortcut](#syntax-shortcut).

### Number

When only the `=` matcher is used in a JSON object, the numeric value can be written in place of the JSON object.
See [`=` matcher's syntax shortcut](#syntax-shortcut).

## NBT Matchers

### NBT Compound child tag

If the matcher name is `@tag`, matches an NBT element, if any of the following is true:

* it is an NBT Compound, has a child tag named `tag`, and the given [filter](#filter) matches its child tag named `tag`.
  If the filter is an empty JSON object, then only the presence of a child tag named `tag` is checked

```json
{
  "@tag": {}
}
```

### NBT List/Array `n`th element

If the matcher name is `#n`, matches an NBT element, if any of the following is true:

* it is an NBT List/Array, `n>=0`, has at least `n-1` elements, and the given [filter](#filter) matches its `n`th element (starting from 0).
  If the filter is an empty JSON object, then only the element count is checked
* it is an NBT List/Array, `n<0`, has at least [`|n|`](https://en.wikipedia.org/wiki/Absolute_value) elements, and the given [filter](#filter) matches its `size-|n|`th element (starting from 0).
  If the filter is an empty JSON object, then only the element count is checked

```json
{
  "#0":  {}, // First element
  "#1":  {}, // Second element
  "#-1": {}, // Last element
  "#-2": {}  // Second to last element
}
```

### `#none`

Matches an NBT element, if any of the following is true:

* it is an NBT List/Array, and none (exactly 0) of its elements match the given [filter](#filter)

```json
{
  "@player": {
    "@Motion": {
      "#none": {
        ">": 1
      }
    }
  }
}
```

### `#any`

Matches an NBT element, if any of the following is true:

* it is an NBT List/Array, and any (1 or more) of its elements match the given [filter](#filter)

```json
{
  "@player": {
    "@Motion": {
      "#any": {
        ">": 1
      }
    }
  }
}
```

### `#some`

Matches an NBT element, if any of the following is true:

* it is an NBT List/Array, and 0 or more, but not all of its elements match the given [filter](#filter)

```json
{
  "@player": {
    "@Motion": {
      "#some": 0
    }
  }
}
```

### `#all`

Matches an NBT element, if any of the following is true:

* it is an NBT List/Array, and all of its elements match the given [filter](#filter)

```json
{
  "@player": {
    "@Motion": {
      "#all": 0
    }
  }
}
```

### `>`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is a number, and is larger than the given number
* it is a string, and is [lexicographically after](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#compareTo-java.lang.String-) the given string (case-sensitive)

```json
{
  "@player": {
    "@health": {
      ">": 10
    }
  },
  "@biome": {
    ">": "minecraft:cherry_grove"
  }
}
```

### `>*`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is a string, and is [lexicographically after](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#compareToIgnoreCase-java.lang.String-) the given string (case-insensitive)

```json
{
  "@biome": {
    ">*": "minecraft:cherry_grove"
  }
}
```

### `>=`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is a number, and is larger than or equal to the given number
* it is a string, and is [lexicographically after or equal to](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#compareTo-java.lang.String-) the given string (case-sensitive)

```json
{
  "@player": {
    "@health": {
      ">=": 10
    }
  },
  "@biome": {
    ">=": "minecraft:cherry_grove"
  }
}
```

### `>=*`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is a string, and is [lexicographically after or equal to](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#compareToIgnoreCase-java.lang.String-) the given string (case-insensitive)

```json
{
  "@biome": {
    ">=*": "minecraft:cherry_grove"
  }
}
```

### `=`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is a number, and is equal to the given number
* it is a string, and is [equal to](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#compareTo-java.lang.String-) the given string (case-sensitive)

```json
{
  "@player": {
    "@health": {
      "=": 10
    }
  },
  "@biome": {
    "=": "minecraft:cherry_grove"
  }
}
```

#### Syntax shortcut

When only the `=` matcher is used in a JSON object, the value can be written in place of the JSON object:

```json
{
  "@player": {
    "@health": {
      "=": 10
    }
  },
  "@biome": {
    "=": "minecraft:cherry_grove"
  }
}
```

Can be written as:

```json
{
  "@player": {
    "@health": 10
  },
  "@biome": "minecraft:cherry_grove"
}
```

### `=*`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is a string, and is [equal to](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#compareToIgnoreCase-java.lang.String-) the given string (case-insensitive)

```json
{
  "@hand": {
    "=*": "Main_Hand"
  }
}
```

### `!=`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is a number, and is not equal to the given number
* it is a string, and is [not equal to](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#compareTo-java.lang.String-) the given string (case-sensitive)

```json
{
  "@player": {
    "@health": {
      "!=": 10
    }
  },
  "@biome": {
    "!=": "minecraft:cherry_grove"
  }
}
```

### `!=*`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is a string, and is [not equal to](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#compareToIgnoreCase-java.lang.String-) the given string (case-insensitive)

```json
{
  "@hand": {
    "!=*": "Off_Hand"
  }
}
```

### `<=`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is a number, and is smaller than or equal to the given number
* it is a string, and is [lexicographically before or equal to](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#compareTo-java.lang.String-) the given string (case-sensitive)

```json
{
  "@player": {
    "@health": {
      "<=": 10
    }
  },
  "@biome": {
    "<=": "minecraft:cherry_grove"
  }
}
```

### `<=*`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is a string, and is [lexicographically before or equal to](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#compareToIgnoreCase-java.lang.String-) the given string (case-insensitive)

```json
{
  "@biome": {
    "<=*": "minecraft:cherry_grove"
  }
}
```

### `<`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is a number, and is smaller than the given number
* it is a string, and is [lexicographically before](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#compareTo-java.lang.String-) the given string (case-sensitive)

```json
{
  "@player": {
    "@health": {
      "<": 10
    }
  },
  "@biome": {
    "<": "minecraft:cherry_grove"
  }
}
```

### `<*`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is a string, and is [lexicographically before](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#compareToIgnoreCase-java.lang.String-) the given string (case-insensitive)

```json
{
  "@biome": {
    "<*": "minecraft:cherry_grove"
  }
}
```

### `regex`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is a string, and matches the given regex (case-sensitive)

Test your regex at [regex101](https://regex101.com) or [RegExr](https://regexr.com) (not sponsored).  
If you need help escaping it as JSON, [try this recipe on CyberChef](https://cyberchef.org/#recipe=Escape_string('Special%20chars','Single',true,true,false)) (also not sponsored).  
If you'd like to buy me a coffee, you can do so at [Ko-fi](https://ko-fi.com/opekope2) (sponsored).

```json
{
  "@biome": {
    "regex": "^minecraft:.*jungle$"
  }
}
```

### `regex*`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is a string, and matches the given regex (case-insensitive)

Test your regex at [regex101](https://regex101.com) or [RegExr](https://regexr.com) (not sponsored).  
If you need help escaping it as JSON, [try this recipe on CyberChef](https://cyberchef.org/#recipe=Escape_string('Special%20chars','Single',true,true,false)) (also not sponsored).  
If you'd like to buy me a coffee, you can do so at [Ko-fi](https://ko-fi.com/opekope2) (sponsored).

```json
{
  "@biome": {
    "regex": "^Minecraft:.*Jungle$"
  }
}
```

### `wildcard`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is a string, and matches the given wildcard (case-sensitive)

* `?` matches exacly 1 character ([regex](#regex) equivalent: `.`)
* `*` matches 0 or more characters ([regex](#regex) equivalent: `.*`)

```json
{
  "@biome": {
    "regex": "minecraft:*savanna*"
  }
}
```

### `wildcard*`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is a string, and matches the given wildcard (case-insensitive)

* `?` matches exacly 1 character ([regex](#regex_1) equivalent: `.`)
* `*` matches 0 or more characters ([regex](#regex_1) equivalent: `.*`)

```json
{
  "@biome": {
    "regex": "Minecraft:*Savanna*"
  }
}
```

### `type`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is of a given type

```json
{
  "@block_state": {
    "type": "compound"
  }
}
```

| Type (numeric) | Type (string) |
|----------------|---------------|
| 0              | `end`         |
| 1              | `byte`        |
| 2              | `short`       |
| 3              | `int`         |
| 4              | `long`        |
| 5              | `float`       |
| 6              | `double`      |
| 7              | `byte_array`  |
| 8              | `string`      |
| 9              | `list`        |
| 10             | `compound`    |
| 11             | `int_array`   |
| 12             | `long_array`  |

### `none_of`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* none (exactly 0) of the given JSON array of [filters](#filter) matches the NBT element

```json
{
  "@player": {
    "none_of": [
      {
        "@Health": 20
      },
      {
        "@FoodLevel": 20
      }
    ]
  }
}
```

### `any_of`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* any (1 or more) of the given JSON array of [filters](#filter) matches the NBT element

```json
{
  "@player": {
    "any_of": [
      {
        "@Health": 20
      },
      {
        "@FoodLevel": 20
      }
    ]
  }
}
```

### `some_of`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* 0 or more, but not all of the given JSON array of [filters](#filter) matches the NBT element

```json
{
  "@player": {
    "some_of": [
      {
        "@Health": 20
      },
      {
        "@FoodLevel": 20
      }
    ]
  }
}
```

### `all_of`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* all of the given JSON array of [filters](#filter) matches the NBT element

```json
{
  "@player": {
    "all_of": [
      {
        "@Health": 20
      },
      {
        "@FoodLevel": 20
      }
    ]
  }
}
```

### `keys`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is an NBT Compound, and the given [filter](#filter) matches a list consisting of the compound's attributes (keys)

```json
{
  "@mods": {
    "keys": {
      "#any": {
        "=": "avm_staff"
      }
    }
  }
}
```

### `values`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is an NBT Compound, and the given [filter](#filter) matches a list consisting of the compound's values

```json
{
  "@mods": {
    "values": {
      "#all": {
        "@version": {
          ">=*": "1"
        }
      }
    }
  }
}
```

### `size`

**OptiGUI 3.0.0-alpha.1+**{.chip-darkgreen}

Matches an NBT element, if any of the following is true:

* it is an NBT Compound, and the number of the attribute-value pairs in it matches the given [filter](#filter)
* it is an NBT List/Array, and the number of the elements in it matches the given [filter](#filter)
* it is a string, and its [length](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#length--) matches the given [filter](#filter)

```json
{
  "@player": {
    "@active_effects": {
      "size": {
        ">=": 33
      }
    }
  }
}
```
