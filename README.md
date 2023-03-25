# OptiGUI

[![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/opekope2/OptiGUI-Next?include_prereleases&label=Download+from+GitHub&logo=github)](https://github.com/opekope2/OptiGUI-Next/releases)
[![Modrinth Version](https://img.shields.io/modrinth/v/optigui?label=Download+from+Modrinth&logo=modrinth)](https://modrinth.com/mod/optigui/versions)
[![CurseForge Download](https://img.shields.io/badge/Download_from_CurseForge-uhh..._latest_I_guess%3F-E04E14?logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/optigui/files)
[![GitHub Repo stars](https://img.shields.io/github/stars/opekope2/OptiGUI-Next?label=⭐+GitHub+stars&color=ffff00)](https://github.com/opekope2/OptiGUI-Next/stargazers)
![GitHub top language](https://img.shields.io/github/languages/top/opekope2/OptiGUI-Next?color=7F52FF&logo=kotlin)
[![Documentation](https://img.shields.io/badge/Read%20the-documentation-8CA1AF?logo=readthedocs)](https://opekope2.github.io/OptiGUI-Next)

A client side drop-in replacement for OptiFine custom GUIs. Now rewritten from scratch in Kotlin for cleaner code, better performance, more features, and extensibility.
Other mods' developers can add their custom containers and customization options. See documentation link above.

[Supports most OptiFine GUI resource packs](#supported-resource-packs). If a resource pack does not work or works differently than using OptiFine, please open an issue.

## How to use it

1. Download and install [Fabric loader](https://fabricmc.net/use) or [Quilt loader](https://quiltmc.org/en/install)
2. Download this mod from your [favorite service](#optigui)
3. Download the required dependencies as well
4. Put this mod in your mods folder

## Resource pack docs

This mod supports a superset of OptiFine features, see documentation link above. Some of them are not supported by OptiFine, and are marked as such.

## Supported resource packs

* [Colourful Containers](https://www.planetminecraft.com/texture-pack/colourful-containers-gui/)
* [Colourful Containers Add-On](https://www.planetminecraft.com/texture-pack/updated-colourful-containers-light-mode-gui-optifine-required/)
* [Colourful Containers Dark Mode](https://www.planetminecraft.com/texture-pack/colourful-containers-dark-mode-gui-optifine-required/)
* [Animated RGB GUI](https://www.curseforge.com/minecraft/texture-packs/optifine-animated-rgb-gui) ¹
* [NEON20 ANIMATED](https://www.planetminecraft.com/texture-pack/neon20-animated-optifine/)
* [Rybo's Enhanced GUI](https://www.planetminecraft.com/texture-pack/rybo-s-enhanced-gui/)

¹ see FAQ for animation support

This list is not exhaustive. If you know a resource pack which works, and you'd like to see it here, or a resource pack, which does not work, but works with OptiFine, please open an issue.

## Partially supported resource packs

* [Advanced GUI](https://www.planetminecraft.com/texture-pack/custom-gui/) ¹
* [New Default+](https://www.curseforge.com/minecraft/texture-packs/newdefaultplus) ²

¹ Color shulker boxes, Ender chests and Villager Professions do not work. The resource pack does not include OptiFine-specific assets, and these features are not supported by OptiGUI. (Some mods (Colourful Containers) include assets for these features and work with OptiGUI)

² Barrel does not work, neither does it work with OptiFine. The resource pack does not specify it according to the format.

## FAQ

### Use in a modpack?

Yes.

### OptiFine GUI animations?

Only with [Animatica](https://github.com/FoundationGames/Animatica).

### OptiFine colors (colors.properties)?

No, use [Colormatic](https://github.com/kvverti/colormatic).

### Configuration GUI/file?

Nothing to configure.

### Sodium?

Yes.

### Supported loaders?

Fabric & Quilt. Anything else is not supported, those may or may not work.
