# Using OptiGUI in a mod

These are the basics of OptiGUI, which gives you some idea on how to use it within your mods.

## Depending on OptiGUI

OptiGUI doesn't have a maven repository. You can install it from [Modrinth Maven](https://docs.modrinth.com/maven) or [Curse Maven](https://www.cursemaven.com/).

1. Add OptiGUI API JAR to your `build.gradle(.kts)` as described in one of the above links (classifier: `api`)
2. Add OptiGUI dependencies to the `modLocalRuntime` configuration inside the dependencies block
3. Extract the nested JARs from the OptiGUI jar's `META-INF/jars` folder to your local mods folder. You can do this manually or with `build.gradle(.kts)`. Here's an example
    1. [Create a configuration](https://github.com/opekope2/OptiGUI/blob/a837dd45285af044170ade6322766305ac107880/Extra/build.gradle.kts#L33-L34)
    2. [Add dependencies to the configuration](https://github.com/opekope2/OptiGUI/blob/a837dd45285af044170ade6322766305ac107880/Extra/build.gradle.kts#L54)
    3. [Create a task to extract nested JARs](https://github.com/opekope2/OptiGUI/blob/a837dd45285af044170ade6322766305ac107880/Extra/build.gradle.kts#L117-L134)

## Resource loading

When the game (re)loads resources, OptiGUI reads [OptiGUI properties](format.html) and [OptiFine properties](https://optifine.readthedocs.io/custom_guis.html), and creates a filter chain.

For each selector in each group in each OptiFine resource, OptiGUI invokes the registered [`ISelector`](kdoc/latest/-opti-g-u-i/opekope2.optigui.selector/-i-selector/index.html)^[(Java syntax)](javadoc/latest/-opti-g-u-i/opekope2.optigui.selector/-i-selector/index.html)^ to create a filter for the given selector. A [`ConjunctionFilter`](kdoc/latest/-opti-g-u-i/opekope2.optigui.filter/-conjunction-filter/index.html)^[(Java syntax)](javadoc/latest/-opti-g-u-i/opekope2.optigui.filter/-conjunction-filter/-conjunction-filter.html)^ will be created for a group containing these filters, and this filter will be added to the filter chain.

Filters added to the filter chain will be evaluated sequentially until one returns a non-null value. This will be cached until the end of the current game tick, and will be used as a the replacement texture of the GUI screen.

## Adding a container

If your mod adds a new container with a GUI screen, [add the GUI screen's texture to your mod's metadata](#container-default-gui-texture).

If you use or extend a vanilla block entity or entity, OptiGUI has built-in support for that. However, you may want to create [selectors](#selectors) for your block entity or entity subclass, which can be used by resource packs.

If you make your own block entity or entity, most OptiGUI selectors won't work. You'll need to create your own [selectors](#selectors), which can be used by resource packs.

## The interaction

* Before an interaction, OptiGUI can be notified about an upcoming interaction using [`Interaction.prepare`](kdoc/latest/-opti-g-u-i/opekope2.optigui.interaction/-interaction/-companion/prepare.html)^[(Java syntax)](javadoc/latest/-opti-g-u-i/opekope2.optigui.interaction/-interaction/prepare.html)^. This is called internally, when the player right clicks a block or entity. This method call returns `false`, and is ignored while an interaction is ongoing.
* An interaction begins when the game opens a GUI screen
* An interaction ends, when the open GUI screen gets closed
* Use [`IBeforeInteractionBeginCallback`](kdoc/latest/-opti-g-u-i/opekope2.optigui.interaction/-i-before-interaction-begin-callback/index.html)^[(Java syntax)](javadoc/latest/-opti-g-u-i/opekope2.optigui.interaction/-i-before-interaction-begin-callback/index.html)^ to get notified right before an interaction begins.

## Selectors

Selectors create a filter from a string description in an OptiGUI INI file. For example, `#!ini name = OptiGUI` is a selector, `name` is the selector key, and `OptiGUI` is the string description.

The created filters process an [`Interaction`](kdoc/latest/-opti-g-u-i/opekope2.optigui.interaction/-interaction/index.html)^[(Java syntax)](javadoc/latest/-opti-g-u-i/opekope2.optigui.interaction/-interaction/index.html)^ once every game tick, and may provide a replacement texture. This texture will be cached for subsequent GUI screen rendering calls within the game tick.

To create a selector:

1. Create a public class
2. Implement [`ISelector`](kdoc/latest/-opti-g-u-i/opekope2.optigui.selector/-i-selector/index.html)^[(Java syntax)](javadoc/latest/-opti-g-u-i/opekope2.optigui.selector/-i-selector/index.html)^ ([Kotlin examples](https://github.com/opekope2/OptiGUI/tree/main/OptiGUI/src/main/kotlin/opekope2/optigui/internal/selector))
3. Register it with [`SelectorRegistry.register`](kdoc/latest/-opti-g-u-i/opekope2.optigui.registry/-selector-registry/register.html)^[(Java syntax)](javadoc/latest/-opti-g-u-i/opekope2.optigui.registry/-selector-registry/register.html)^

## OptiGUI custom metadata

`fabric.mod.json` allows [custom fields](https://fabricmc.net/wiki/documentation:fabric_mod_json_spec#custom_fields) to be specified.
OptiGUI will read this metadata from each mod where present.

### Container default GUI texture

Add a JSON object named `optigui:container_default_gui_textures` to the custom fields to define the texture of a container's screen:

```json
{
  //...
  "custom": {
    "optigui:container_default_gui_textures": {
      "modid:container1": "modid:path/to/textures/container1.png",
      "modid:container2": "modid:path/to/textures/container2.png"
    }
  }
}
```

Container `modid:container1` has the texture `modid:path/to/textures/container1.png`, and `modid:container2` has the texture `modid:path/to/textures/container2.png`.
