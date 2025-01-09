# Extensions to OptiFine properties

OptiGUI 2.1.0-beta.1 removed support for all extensions from OptiFine properties. Here's a guide how to convert them to [OptiGUI JSON resources](JSON.md).

## `#!properties container=_cartography_table`

```json
{
  "container": "cartography_table"
}
```

## `#!properties container=_chest_boat` before Minecraft 1.21.2

Add a matcher for the `Type` NBT of the chest boat entity:

```json
{
  "container": "chest_boat",
  "match": {
    "@entity": {
      "@Type": "<variant>"
    }
  }
}
```

If you have multiple `variants`:

```json
{
  "container": "chest_boat",
  "match": {
    "@entity": { 
      "@Type": {
        "any_of": ["<variant1>", "<variant2>"]
      }
    }
  }
}
```

## `#!properties container=_chest_boat` starting Minecraft 1.21.2

Add `variants` before `chest_boat` (without the angle brackets):

```json
{
  "container": "<variant>_chest_boat"
}
```

If you have multiple `variants`, put them in a JSON array:

```json
{
  "container": ["<variant1>_chest_boat", "<variant2>_chest_boat"]
}
```

## `#!properties container=_grindstone`

```json
{
  "container": "grindstone"
}
```

## `#!properties container=_loom`

```json
{
  "container": "loom"
}
```

## `#!properties container=_smithing_table`

```json
{
  "container": "smithing_table"
}
```

## `#!properties container=_stonecutter`

```json
{
  "container": "stonecutter"
}
```

## `#!properties _barrel=true`

```json
{
  "container": "barrel"
}
```

## `#!properties _minecart=true` if `#!properties container=chest`

```json
{
  "container": "chest_minecart"
}
```

## `#!properties _minecart=true` if `#!properties container=hopper`

```json
{
  "container": "hopper_minecart"
}
```

## `#!properties variants=_furnace`

```json
{
  "container": "furnace"
}
```

## `#!properties variants=_blast`, `#!properties variants=_blast_furnace`

```json
{
  "container": "blast_furnace"
}
```

## `#!properties variants=_smoker`

```json
{
  "container": "smoker"
}
```

## `#!properties _camel=true`

```json
{
  "container": "camel"
}
```

## `#!properties _zombie_horse=true`

```json
{
  "container": "zombie_horse"
}
```

## `#!properties _skeleton_horse=true`

```json
{
  "container": "skeleton_horse"
}
```

## `#!properties _wandering_trader=true`

```json
{
  "container": "wandering_trader"
}
```
