# Conditional loading

OptiGUI ^2.1.0-beta.3+^ supports conditionally loading OptiGUI resources.
These conditions should be added to [groups](/syntax/#groups) to specify when (not) to load the group.

## `if`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.3+**{.chip-darkgreen}

A single [boolean](/syntax/#booleans) specifying if the property should be loaded. Set to `false` to prevent the group from loading.

!!! example
    ```ini
    [container]
    if = false
    ```

## `if.mod.optigui.version.at_least`

**Optional**{.chip-lightblue}
**OptiGUI 2.1.0-beta.3+**{.chip-darkgreen}

A [semantic version](https://semver.org) specifying the minimum version of OptiGUI required to load the group. Useful to prevent loading on old versions, which don't support some features.

!!! example
    ```ini
    [container]
    if.mod.optigui.version.at_least = 2.1.0-beta.3
    ```
