# Using OptiGUI in a mod

These are the basics of OptiGUI, which gives you some idea on how to use it within your mods.

## Installing OptiGUI

OptiGUI doesn't have a maven repository. You can install it from [Modrinth Maven](https://docs.modrinth.com/maven) or [Curse Maven](https://www.cursemaven.com/).

1. Add OptiGUI API JAR to your `build.gradle(.kts)` as described in one of the above links (classifier: `api`)
2. Add OptiGUI dependencies to the `modLocalRuntime` configuration inside the dependencies block
3. Extract the nested JARs from the OptiGUI jar's `META-INF/jars` folder to your local mods folder. You can do this with `build.gradle(.kts)`. Here's an example
    1. [Create a configuration](https://github.com/opekope2/OptiGUI/blob/a837dd45285af044170ade6322766305ac107880/Extra/build.gradle.kts#L33-L34)
    2. [Add dependencies to the configuration](https://github.com/opekope2/OptiGUI/blob/a837dd45285af044170ade6322766305ac107880/Extra/build.gradle.kts#L54)
    3. [Create a task to extract nested JARs](https://github.com/opekope2/OptiGUI/blob/a837dd45285af044170ade6322766305ac107880/Extra/build.gradle.kts#L117-L134)

## Bundling OptiGUI API as nested JAR and adding a dependency in `fabric.mod.json`

When OptiGUI is present, OptiGUI API JAR will not be loaded, because OptiGUI provides `optigui-api`.

If OptiGUI is a required dependency of your mod

* Add `optigui` as depends in `fabric.mod.json`
* Do not add OptiGUI or OptiGUI API as nested JAR

If OptiGUI is an optional dependency and you use the API

* Add `optigui-api` as depends in `fabric.mod.json`
* Add `optigui` as recommends/suggests in `fabric.mod.json`
* Add OptiGUI API as a nested JAR. You can check if OptiGUI is present with [`IOptiGuiApi.isAvailable`](kdoc/latest/Api/opekope2.optigui.api/-i-opti-gui-api/is-available.html)^[(Java syntax)](javadoc/latest/Api/opekope2.optigui.api/-i-opti-gui-api/is-available.html)^

If OptiGUI is an optional dependency, and you don't use the API

* Add `optigui` as recommends/suggests in `fabric.mod.json`
* Do not add OptiGUI or OptiGUI API as nested JAR

!!! tip
    It is recommended to add a dependency on the minor version of OptiGUI (API) to avoid crashes caused by breaking changes in OptiGUI. This way, Fabric will refuse to load likely incompatible mods.
    ```json
    "optigui": "~2.2.0"
    ```

## Entry point

OptiGUI 2.2.0-alpha.1 no longer invokes the `optigui` entrypoint. The functionality has been removed, because the methods of [`InitializerContext`](kdoc/2.1.5/-opti-g-u-i/opekope2.optigui/-initializer-context/index.html)^[(Java syntax)](javadoc/2.1.5/-opti-g-u-i/opekope2.optigui/-initializer-context/index.html)^ has been replaced by metadata in `fabric.mod.json`, making it redundant.

## Resource loading

When the game (re)loads resources, OptiGUI reads [OptiGUI properties](format.html) and [OptiFine properties](https://optifine.readthedocs.io/custom_guis.html), and creates a filter chain.

For each selector in each group in each OptiFine resource, OptiGUI invokes the registered [Selector](kdoc/latest/Api/opekope2.optigui.api.selector/-i-selector/index.html)^[(Java syntax)](javadoc/latest/Api/opekope2.optigui.api.selector/-i-selector/index.html)^ to create a filter for the given selector. A [ConjunctionFilter](kdoc/latest/Filters/opekope2.optigui.filter/-conjunction-filter/index.html)^[(Java syntax)](javadoc/latest/Filters/opekope2.optigui.filter/-conjunction-filter/index.html)^ will be created for a group containing these filters, and this filter will be added to the filter chain.

## The texture replacer

The [texture replacer](https://github.com/opekope2/OptiGUI/blob/main/OptiGUI/src/main/kotlin/opekope2/optigui/internal/TextureReplacer.kt#L21) is a core internal part of OptiGUI, which replaces the default GUI screen texture to the one specified in a resource pack.

If your mod adds a new container with a GUI screen, please consider [adding its texture to the metadata](#optigui-custom-metadata). For example:

```json
{
  //...
  "custom": {
    "optigui": {
      "containerTextures": {
        "modid:container1": "modid:path/to/textures/container1.png",
        "modid:container2": "modid:path/to/textures/container2.png"
      }
    }
  }
}
```

Filters added to the filter chain will be evaluated sequentially until one returns a [`Match`](kdoc/latest/Api/opekope2.optigui.filter/-i-filter/-result/-match/index.html)^[(Java syntax)](javadoc/latest/Api/opekope2.optigui.filter/-i-filter/-result/-match/index.html)^.
The [`result`](kdoc/latest/Api/opekope2.optigui.filter/-i-filter/-result/-match/result.html)^[(Java syntax)](javadoc/latest/Api/opekope2.optigui.filter/-i-filter/-result/-match/get-result.html)^ of this will be cached until the interaction ends, or the [current processor](#processors) returns a different object.

## The interaction

* Before an interaction, the [texture replacer](#the-texture-replacer) can be notified about an upcoming interaction using [IInteractor.interact](kdoc/latest/Api/opekope2.optigui.api.interaction/-i-interactor/interact.html)^[(Java syntax)](javadoc/latest/Api/opekope2.optigui.api.interaction/-i-interactor/interact.html)^. This method call is ignored while an interaction is ongoing.
* An interaction begins when the game opens a GUI screen
* An interaction ends, when the open GUI screen gets closed

## Filter factories

Since OptiGUI 2.2.0-alpha.1, the role of filter factories changed. They now create a filter from a selector in an OptiGUI INI file.

To create a filter factory:

1. Create a pubic class
2. Make it implement [ISelector](kdoc/latest/Api/opekope2.optigui.api.selector/-i-selector/index.html)^[(Java syntax)](kdoc/latest/Api/opekope2.optigui.api.selector/-i-selector/index.html)^ ([Kotlin examples](https://github.com/opekope2/OptiGUI/tree/main/OptiGUI/src/main/kotlin/opekope2/optigui/internal/selector))
3. Annotate it with [Selector](kdoc/latest/Api/opekope2.optigui.annotation/-selector/index.html)^[(Java syntax)](javadoc/latest/Api/opekope2.optigui.annotation/-selector/index.html)^. The value should be the name of the selector to parse.
4. Add your class as an [entry point](https://fabricmc.net/wiki/documentation:entrypoint) named `optigui-selector` ([example](https://github.com/opekope2/OptiGUI/blob/7d806e6201a8b9d7920992e9eb70897e3ba91a84/OptiGUI/src/main/resources/fabric.mod.json#L108-L205))

Selectors are highly recommended to be used with [processors](#processors). Processors provide [Interaction.data](kdoc/latest/Api/opekope2.optigui.api.interaction/-interaction/index.html)^[(Java syntax)](javadoc/latest/Api/opekope2.optigui.api.interaction/-interaction/index.html)^, which can be processed by filters created by selectors.

## Processors

Entity processors and block entity processors provide [Interaction.data](kdoc/latest/Api/opekope2.optigui.api.interaction/-interaction/index.html)^[(Java syntax)](javadoc/latest/Api/opekope2.optigui.api.interaction/-interaction/index.html)^, which can be processed by the filters created by [filter factories](#filter-factories). During an interaction, if a matching processor is found, it is executed each game tick, so it must execute as quickly as possible.

### Entity processors

To create an entity processor:

1. Create a pubic class
2. Make it implement [IEntityProcessor](kdoc/latest/Api/opekope2.optigui.api.interaction/-i-entity-processor/index.html)^[(Java syntax)](javadoc/latest/Api/opekope2.optigui.api.interaction/-i-entity-processor/index.html)^ ([Kotlin examples](https://github.com/opekope2/OptiGUI/blob/main/OptiGUI/src/main/kotlin/opekope2/optigui/internal/VanillaProcessors.kt))
3. Annotate it with [EntityProcessor](kdoc/latest/Api/opekope2.optigui.annotation/-entity-processor/index.html)^[(Java syntax)](javadoc/latest/Api/opekope2.optigui.annotation/-entity-processor/index.html)^. The value should be the class to register a processor for. A processor will not be automatically registered for its subclasses.
4. Add your class as an [entry point](https://fabricmc.net/wiki/documentation:entrypoint) named `optigui-entityprocessor` ([example](https://github.com/opekope2/OptiGUI/blob/7d806e6201a8b9d7920992e9eb70897e3ba91a84/OptiGUI/src/main/resources/fabric.mod.json#L56-L85))

### Block entity processors

To create a block entity processor:

1. Create a pubic class
2. Make it implement [IBlockEntityProcessor](kdoc/latest/Api/opekope2.optigui.api.interaction/-i-block-entity-processor/index.html)^[(Java syntax)](javadoc/latest/Api/opekope2.optigui.api.interaction/-i-block-entity-processor/index.html)^ ([Kotlin examples](https://github.com/opekope2/OptiGUI/blob/main/OptiGUI/src/main/kotlin/opekope2/optigui/internal/VanillaProcessors.kt))
3. Annotate it with [BlockEntityProcessor](kdoc/latest/Api/opekope2.optigui.annotation/-block-entity-processor/index.html)^[(Java syntax)](javadoc/latest/Api/opekope2.optigui.annotation/-block-entity-processor/index.html)^. The value should be the class to register a processor for. A processor will not be automatically registered for its subclasses.
4. Add your class as an [entry point](https://fabricmc.net/wiki/documentation:entrypoint) named `optigui-blockentityprocessor` ([example](https://github.com/opekope2/OptiGUI/blob/7d806e6201a8b9d7920992e9eb70897e3ba91a84/OptiGUI/src/main/resources/fabric.mod.json#L86-L107))

## OptiGUI custom metadata

`fabric.mod.json` allows [custom fields](https://fabricmc.net/wiki/documentation:fabric_mod_json_spec#custom_fields) to be specified.
OptiGUI will read this metadata from each mod where present.

Here's a pseudocode representation of what the OptiGUI metadata looks like.

```java
class CustomMetadata {
    containerTextures: Map<Identifier, Identifier | List<Identifier | TexturePathMetadata>>?
}

class TexturePathMetadata {
    conditions: ConditionsMetadata?
    texture: Identifier
}

class ConditionsMetadata {
    mods: Map<String, VersionPredicate>?
}
```

External types:

* [Identifier](https://minecraft.fandom.com/wiki/Resource_location)
* [VersionPredicate](conditional.html#ifmods)
* Map: A JSON Object (ex. `{ "key": "value" }`)
* `A | B`: Either `A` or `B`
* `?`: Optional (not required)
