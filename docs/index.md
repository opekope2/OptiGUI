# Home

[![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/opekope2/OptiGUI?include_prereleases&label=Download+from+GitHub&logo=github)](https://github.com/opekope2/OptiGUI/releases)
[![Modrinth Version](https://img.shields.io/modrinth/v/optigui?label=Download+from+Modrinth&logo=modrinth)](https://modrinth.com/mod/optigui/versions)
[![CurseForge Download](https://img.shields.io/badge/Download_from_CurseForge-uhh..._latest_I_guess%3F-E04E14?logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/optigui/files)
[![GitHub Repo stars](https://img.shields.io/github/stars/opekope2/OptiGUI?label=⭐+GitHub+stars&color=ffff00)](https://github.com/opekope2/OptiGUI/stargazers)
![GitHub top language](https://img.shields.io/github/languages/top/opekope2/OptiGUI?color=7F52FF&logo=kotlin)
[![Buy me a coffee](https://img.shields.io/badge/Buy%20me%20a%20coffe-Ko--fi-f16061?logo=ko-fi)](https://ko-fi.com/opekope2)

Welcome to the OptiGUI documentation. OptiGUI enables the customization of any inventory GUIs on Fabric&Quilt, and has built-in support for OptiFine resource packs.

## OptiFine resource packs

OptiGUI supports [OptiFine resource packs](https://optifine.readthedocs.io/custom_guis.html). When loading OptiFine resources, OptiGUI internally converts them to the equivalent OptiGUI resource, then proceeds to load them.

If you want to reach a broader user base with your resource pack, and OptiFine provides all the necessary features, then create an OptiFine resource pack. However, if some features are not possible with OptiFine, scroll down for alternatives.

## OptiGUI resource packs

OptiFine's custom GUI support is lagging behind by quite a few years, as
[seen](https://github.com/sp614x/optifine/issues/3027)
[in](https://github.com/sp614x/optifine/issues/5292)
[some](https://github.com/sp614x/optifine/issues/5329)
[issues](https://github.com/sp614x/optifine/issues/6481).
OptiGUI resource packs, in contrast, support more features.

I also found it confusing using `#!properties container=chest`, as there is no way to specify whether to replace the texture of a single chest, and adding `_barrel` to it was a mistake, because OptiFine ignores `_barrel` as mentioned in [#30](https://github.com/opekope2/OptiGUI/issues/30).

I introduced OptiGUI-specific configuration files in OptiGUI 2.1.0-beta.1, which enables the use of more precise selectors to choose when to replace the texture. These can be found [here](format.html). More selectors are planned to be added in later versions, you can check out [GitHub issues](https://github.com/opekope2/OptiGUI) to see some of them.

## Hybrid resource packs

OptiGUI and OptiFine resources reside in different folders, and have different file extensions, so it is possible to create "hybrid resource packs".

If you want to use OptiGUI features, and also want to support OptiFine, then I recommend creating OptiFine-specific assets whenever possible, and if it's not possible, use OptiGUI features. These latter resources will only be available for OptiGUI users, while OptiFine users can enjoy a subset of your resource pack.

### Referencing OptiFine textures from OptiGUI INI files

OptiFine assets are located in the `/assets/minecraft/optifine/gui/container/` folder (or its children).

!!! example
    If you have a texture at `/assets/minecraft/optifine/gui/container/shulker_boxes/purple.png`, you can reference it from any OptiGUI INI file using:

    ```ini
    replacement = minecraft:optifine/gui/container/shulker_boxes/purple.png
    ```

### Referencing OptiGUI assets from OptiFine properties

OptiGUI assets are located in the `/assets/optigui/gui/` folder (or its children).

!!! example
    If you have a texture at `/assets/optigui/gui/shulker_boxes/purple.png`, you can reference it from any OptiFine properties file using:

    ```properties
    texture=optigui:gui/shulker_boxes/purple.png
    ```

## Replacing inventory menu textures (typically found on servers)

WIP. Some tips can be found in [#50](https://github.com/opekope2/OptiGUI/issues/50).

## Animations

Animations may be possible in the future with selectors, like `frame.numerator` and `frame.denominator` (non-existent at the moment), which is different from OptiFine custom animations.

OptiGUI supports [Animatica](https://github.com/FoundationGames/Animatica) ([Modrinth](https://modrinth.com/mod/animatica), [CurseForge](https://www.curseforge.com/minecraft/mc-mods/animatica)) out of box.
