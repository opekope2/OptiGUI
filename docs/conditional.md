# Conditional loading

OptiGUI ^2.1.0-beta.3+^ supports conditionally loading OptiGUI resources.

Conditional loading selectors always begin with `if.` (except for `if`), and should be added to [groups](syntax.html#groups) to specify when (not) to load the group (just like normal selectors). These will be evaluated once as filters when loading resources instead of when replacing textures, and will not "leak" into the filters created by other selectors.

## `if`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.3+**{.chip-darkgreen}

A single [boolean](syntax.html#booleans) specifying if the property should be loaded. Set to `false` to prevent the group from loading.

!!! example
    ```ini
    [container]
    if = false
    ```

## `if.mod.optigui.version.at_least`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.3+**{.chip-darkgreen}
**Removed in OptiGUI 2.3.0-alpha.1**{.chip-red}

Use [`if.mods = optigui>=version`](#ifmods) instead of `#!properties if.mod.optigui.version.at_least = version`.

## `if.mods`

**Optional**{.chip-lightblue}
**OptiGUI 2.2.0-alpha.1+**{.chip-darkgreen}

A [list](syntax.html#lists) of the following things:

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

### Operators

Opertor | Checks, if a mod...
--------|-----------------------------------------------------------------------------------------------------
`>`     | is newer, than the given version
`>=`    | is at least as new, as the given version
`<`     | is older, than the given version
`<=`    | is at most as old, as the given version
`=`     | has the same version, as the given version
`~`     | is at least as new, and has the same minor (**X.Y**.z) version, as the given version
`^`     | is at least as new, and has the same major (**X**.y.z) version, as the given version
