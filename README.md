# OptiGUI

[![GitHub Release](https://img.shields.io/github/v/release/opekope2/OptiGUI?include_prereleases&style=flat&logo=github&label=Download%20form%20GitHub)](https://github.com/opekope2/OptiGUI/releases)
[![Modrinth Download](https://img.shields.io/modrinth/v/optigui?style=flat&logo=modrinth&label=Download%20from%20Modrinth)](https://modrinth.com/mod/optigui/versions)
[![CurseForge Download](https://img.shields.io/curseforge/v/619986?style=flat&logo=curseforge&label=Download%20from%20CurseForge)](https://www.curseforge.com/minecraft/mc-mods/optigui/files)
[![GitHub Repo stars](https://img.shields.io/github/stars/opekope2/OptiGUI?style=flat&label=%E2%AD%90%20GitHub%20stars&color=ffff00)](https://github.com/opekope2/OptiGUI/stargazers)
![GitHub top language](https://img.shields.io/github/languages/top/opekope2/OptiGUI?style=flat&logo=kotlin&color=7f52ff)
[![Documentation](https://img.shields.io/badge/Read_the-documentation-8ca1af?style=flat&logo=readthedocs)](https://opekope2.dev/OptiGUI)
[![Buy me a coffee](https://img.shields.io/badge/Buy_me_a_coffee-Ko--fi-f16061?style=flat&logo=ko-fi)](https://ko-fi.com/opekope2)

OptiGUI is a client-side mod, which enables the customization of in-game GUI screen textures with resource packs. Now rewritten from scratch in Kotlin for cleaner code, better performance, more features, and extensibility.
Other mods' developers can add their custom containers and customization options. See documentation link above.

[Supports most OptiFine GUI resource packs](#supported-resource-packs). If a resource pack does not work or works differently than using OptiFine, please open an issue (select the **Report a bug** template).

## How to use it

1. Download and install [Fabric loader](https://fabricmc.net/use) or [Quilt loader](https://quiltmc.org/en/install)
2. Download this mod from your [favorite service](#optigui)
3. Download the required dependencies as well
4. Put this mod in your mods folder

## Supported languages

* 🇪🇸 by [LyriaWintona](https://github.com/LyriaWintona)
* 🇵🇱 by [lumiscosity](https://github.com/lumiscosity), [Nadios_kox](https://github.com/Fhilips613)
* 🇺🇸 by [opekope2](https://github.com/opekope2)

## Supported languages (until OptiGUI 3 alpha 3)

These were removed in OptiGUI 3 alpha 4 due to being outdated

* 🇹🇼 by [dirtTW](https://github.com/yichifauzi), [notlin4](https://github.com/notlin4)
* 🇨🇳 by [dirtTW](https://github.com/yichifauzi), [notlin4](https://github.com/notlin4)
* 🇩🇪 by [Lucanoria](https://github.com/Lucanoria)
* 🇮🇹 by [RoberbufDx8105](https://github.com/RoberbufDx8105)
* 🇲🇾 by [NuruddinPlays](https://github.com/NuruddinPlays)
* 🇺🇦 by [gorr0w7](https://github.com/gorr0w7)

Want to see your name here? [Translate OptiGUI to your native language](https://github.com/opekope2/OptiGUI/blob/dev/CONTRIBUTING.md#i-want-to-add-a-translation).

## Resource pack docs

OptiGUI supports OptiFine custom GUI resource packs, and also supports OptiGUI resource packs. OptiGUI resource packs are more flexible than OptiFine resource packs.
See documentation link above to get started.

## Supported resource packs

Excluding vanilla resource packs (which don't contain OptiFine or OptiGUI-specific files)

* [Colourful Containers](https://www.planetminecraft.com/texture-pack/colourful-containers-gui/)
* [Colourful Containers Add-On](https://www.planetminecraft.com/texture-pack/updated-colourful-containers-light-mode-gui-optifine-required/)
* [Colourful Containers Dark Mode](https://www.planetminecraft.com/texture-pack/colourful-containers-dark-mode-gui-optifine-required/)
* [Animated RGB GUI](https://www.curseforge.com/minecraft/texture-packs/optifine-animated-rgb-gui) ¹
* [NEON20 ANIMATED](https://www.planetminecraft.com/texture-pack/neon20-animated-optifine/)
* [Rybo's Enhanced GUI](https://www.planetminecraft.com/texture-pack/rybo-s-enhanced-gui/)
* [Animated Fox Guis](https://www.planetminecraft.com/texture-pack/animated-fox-guis-1-16-x/) ¹
* [\[DARK\] Animated Fox Guis](https://www.planetminecraft.com/texture-pack/fox-4931933/) ¹

¹ see FAQ for animation support

This list is not exhaustive. If you know a resource pack which works, and you'd like to see it here, or a resource pack, which does not work, but works with OptiFine, please open an issue.

## Partially supported resource packs

Excluding vanilla resource packs (which don't container OptiFine or OptiGUI-specific files)

* [New Default+](https://www.curseforge.com/minecraft/texture-packs/newdefaultplus) ¹

¹ Barrel does not work, neither does it work with OptiFine. The resource pack does not specify it according to the format.

## FAQ

### Use in a modpack?

Yes.

### OptiFine GUI animations?

Only with [Animatica](https://github.com/FoundationGames/Animatica).

### OptiFine colors (colors.properties)?

No, use [Colormatic](https://github.com/kvverti/colormatic).

### Performance impact?

OptiGUI's performance impact is negligible (tho it varies a bit depending on which resource packs are used).

### Configuration GUI/file?

Nothing to configure.

### Sodium?

Yes.

### OptiFine?

Actually, OptiGUI and OptiFine (with OptiFabric) **can** be loaded simultaneously.
I have observed that OptiGUI mixins will be placed before OptiFine ones, so you can turn off custom GUIs in OptiFine to save performance.

However, I can't assist you with this.

### Supported loaders?

Fabric & Quilt. Anything else is not supported, those may or may not work.
