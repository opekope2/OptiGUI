# Using OptiGUI in a mod

These are the basics of OptiGUI, which gives you some idea on how to use it within your mods.

## Installing OptiGUI

OptiGUI doesn't have a maven repository. You can install it from [Modrinth Maven](https://docs.modrinth.com/docs/tutorials/maven/) or [Curse Maven](https://www.cursemaven.com/).

1. Add OptiGUI to your `build.gradle(.kts)` as described in one of the above links
2. Download OptiGUI dependencies to your local mods folder (usually `${PROJECT_DIR}/run/mods`, where `${PROJECT_DIR}` is your project directory)
3. Extract `commons-text-*.jar` and the appropriate `optiglue-*.jar` from the OptiGUI jar's `META-INF/jars` folder to your local mods folder, otherwise you'll get a juicy `RuntimeException` (because Loom removes the `jars` key from `fabric.mod.json` for some reason). Starting at OptiGUI 2.1.0-beta.3, it won't even load if no appropriate version of OptiGlue is found.

## Entry point

Add an [entry point](https://fabricmc.net/wiki/documentation:entrypoint) named `optigui` of type [`EntryPoint`](../kdoc/latest/-opti-g-u-i/opekope2.optigui/-entry-point/index.html)^[(Java\ syntax)](../javadoc/latest/-opti-g-u-i/opekope2.optigui/-entry-point/index.html)^ to obtain an instance of an [initializer context](#initializer-context).
You can initialize OptiGUI-related behavior in the [`EntryPoint.onInitialize`](../kdoc/latest/-opti-g-u-i/opekope2.optigui/-entry-point/on-initialize.html)^[(Java\ syntax)](../javadoc/latest/-opti-g-u-i/opekope2.optigui/-entry-point/on-initialize.html)^ method of your entry point.

This entry point will get invoked by OptiGUI, when it finishes initialization.

!!! tip "OptiGUI as an optional dependency"
    If your mod has an optional dependency on OptiGUI, these features can be enabled here (for example, using wrapper interfaces around OptiGUI, and setting the actual implementation instead of a dummy one).

## Initializer context

The entry point grants access to an [`InitializerContext`](../kdoc/latest/-opti-g-u-i/opekope2.optigui/-initializer-context/index.html)^[(Java\ syntax)](../javadoc/latest/-opti-g-u-i/opekope2.optigui/-initializer-context/index.html)^, in which you can register [filter factories](#filters-and-filter-factories) and [preprocessors](#preprocessors). For each java (as in JVM) class, only one preprocessor can be registered.

!!! tip "Vanilla containers"
    OptiGUI registers preprocessors for each vanilla container before any other mods, therefore other mods can't do this.

## Resource loading

When the game (re)loads resources, OptiGUI reads [OptiFine properties](../format), and creates a filter chain.

For each resource, OptiGUI invokes every [filter factory](#filters-and-filter-factories) to create a filter for the given resource. Each filter returned by the filter factories will be added to the filter chain.

## The texture replacer

The [texture replacer](https://github.com/opekope2/OptiGUI-Next/blob/main/OptiGUI/src/main/kotlin/opekope2/optigui/internal/TextureReplacer.kt#L21) is a core internal part of OptiGUI, which replaces the default GUI screen texture to the one specified in a resource pack.

Filters added to the filter chain will be evaluated sequentially until one returns a [`Match`](../kdoc/latest/-opti-g-u-i/opekope2.filter/-filter-result/-match/index.html)^[(Java\ syntax)](../javadoc/latest/-opti-g-u-i/opekope2.filter/-filter-result/-match/index.html)^.
The [`result`](../kdoc/latest/-opti-g-u-i/opekope2.filter/-filter-result/-match/result.html)^[(Java\ syntax)](../javadoc/latest/-opti-g-u-i/opekope2.filter/-filter-result/-match/get-result.html)^ of this will be cached until the interaction ends, or the [current preprocessor](#preprocessors) returns a different object.

## The interaction

* Before an interaction, the [texture replacer](#the-texture-replacer) can be notified about an upcoming interaction
* An interaction begins when the game opens a GUI screen
* An interaction ends, when the open GUI screen gets closed

[`InteractionService.interact`](../kdoc/latest/-opti-g-u-i/opekope2.optigui.service/-interaction-service/interact.html)^[(Java\ syntax)](../javadoc/latest/-opti-g-u-i/opekope2.optigui.service/-interaction-service/interact.html)^ is used to prepare the [texture replacer](#the-texture-replacer) for the next interaction by telling it information about how the GUI texture should be replaced. The parameters of this method call determine which [preprocessor](#preprocessors) processes the interaction. Once the interaction has begun, this method call is ignored.

!!! tip
    This method is called internally when a player right-clicks an entity or a block entity.

## Filters and filter factories

Filter factories create filters when resources are (re)loaded (when the game starts, or the player presses F3+T).

Each filter accepts an interaction, processes it, and returns a result whether a texture should be replaced or not, and if yes, provides a replacement texture. Each filter must be deterministic (i.e. return the same output for the same input).

Filter factories can be registered with [`InitializerContext.registerFilterFactory`](../kdoc/latest/-opti-g-u-i/opekope2.optigui/-initializer-context/register-filter-factory.html)^[(Java\ syntax)](../javadoc/latest/-opti-g-u-i/opekope2.optigui/-initializer-context/register-filter-factory.html)^ (see more documentation there).

## Preprocessors

Entity preprocessors and block entity preprocessors supply the [`Interaction.data`](../kdoc/latest/-opti-g-u-i/opekope2.optigui.interaction/-interaction/data.html)^[(Java\ syntax)](../javadoc/latest/-opti-g-u-i/opekope2.optigui.interaction/-interaction/get-data.html)^ of the filters the [filter factories](#filters-and-filter-factories) create. During an interaction, if a matching preprocessor is found, it is executed each game tick, so it must execute as quickly as possible.

Preprocessors can be registered with [`InitializerContext.registerPreprocessor`](../kdoc/latest/-opti-g-u-i/opekope2.optigui/-initializer-context/register-preprocessor.html)^[(Java\ syntax)](../javadoc/latest/-opti-g-u-i/opekope2.optigui/-initializer-context/register-preprocessor.html)^ (see more documentation there).
