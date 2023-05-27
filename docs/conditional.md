# Conditional loading

OptiGUI starting at 2.1.0-beta.3 supports conditionally loading OptiGUI resources.
These conditions should be added to [groups](/syntax/#groups) to specify when (not) to load the group.

## `if`

!!! abstract "OptiGUI 2.1.0-beta.3+"

!!! info "Type"
    [Boolean](/syntax/#booleans)

!!! example
    ```ini
    [container]
    if = false
    ```

Set to `false` to prevent the group from loading.

## `id.mod.optigui.version.at_least`

!!! abstract "OptiGUI 2.1.0-beta.3+"

!!! info "Type"
    [Semantic version](https://semver.org)

!!! example
    ```ini
    [container]
    if.mod.optigui.version.at_least = 2.1.0-beta.3
    ```

Set the minimum version of OptiGUI required to load the group. Useful to prevent loading on old versions, which don't support some features
