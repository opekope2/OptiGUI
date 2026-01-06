# How to contribute?

Hello there! I'm glad you'd like to make OptiGUI a better mod.

## I fixed something in OptiGUI

Cool!

1. Clone the repository
2. Create a new branch from `dev`
3. Commit your changes. Make sure to format the code and optimize imports. Use *Kotlin style guide* in IDEA
4. Open a pull request to the `dev` branch

## I want to add a translation

Cool!

1. Clone the repository
2. Create a new branch from `dev`
3. Add your translation JSON to `OptiGUI/src/main/resources/assets/optigui/lang/` folder
4. Open a pull request to the `dev` branch
5. I will add your GitHub display name and GitHub link to the README, the Modrinth and CurseForge pages, `fabric.mod.json`, and `neoforge.mods.toml`. If you want a different name or link, include it in the pull request description

### Translation context

* `optigui.enum.InspectorNbtDumpOption.DISABLED`: Disabled means *off*, not *impaired*
* `optigui.gui.rp_converter.target_namespace`: Namespace refers to [resource location namespace](https://minecraft.wiki/w/Resource_location#Namespaces)
* `optigui.rp_loader.warn.no_interaction_target`: `blocks`, `entities`, `items`, `inventory`, and `unknown` are parts of JSON resources, so these shouldn't be translated
* `optigui.validation.error.empty_map`: Map refers to [this data structure](https://en.wikipedia.org/wiki/Associative_array), not a [map item](https://minecraft.wiki/w/Map)
* `optigui.validation.error.not_a_relative_identifier`: relative resource location refers to an OptiGUI-specific variant of [resource location](https://minecraft.wiki/w/Resource_location), so `./texture.png` can be used instead of `namespace:path/to/texture.png`
* `optigui.validation.error.of_texture_required`: `texture.PATH` is documented by [OptiFine docs](https://optifine.readthedocs.io/custom_guis.html#texture-texture-path), so it should not be translated

## I'd like to see a feature added to OptiGUI

1. Create a new issue and select *Request a feature* template
2. Fill in the form

## OptiGUI doesn't work how it's supposed to

1. Search the open issues to see if anyone already reported it. If not, proceed to step 2
2. Create a new issue and select *Report a bug* template
3. Fill in the form with reasonable details, so I can reproduce it, and submit the issue

## OptiGUI crashed my game

1. Search the open issues to see if anyone already reported it. If not, proceed to step 2
2. Create a new issue and select *Report a crash* template
3. Fill in the form with reasonable details, so I can reproduce it, and submit the issue
