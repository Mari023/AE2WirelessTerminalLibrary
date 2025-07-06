[![build main](https://github.com/Mari023/AE2WirelessTerminalLibrary/actions/workflows/build.yml/badge.svg)](https://github.com/Mari023/AE2WirelessTerminalLibrary/actions/workflows/build.yml)
[![Curseforge downloads](http://cf.way2muchnoise.eu/full_459929_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/applied-energistics-2-wireless-terminals)
[![Modrinth downloads](https://img.shields.io/modrinth/dt/pNabrMMw?color=modrinth&label=modrinth&logo=modrinth)](https://modrinth.com/mod/applied-energistics-2-wireless-terminals)
[![Modrinth Versions](https://img.shields.io/badge/dynamic/json?color=modrinth&label=modrinth&prefix=Available%20for:%20&query=game_versions&url=https://api.modrinth.com/v2/project/pNabrMMw&style=flat&logo=modrinth)](https://modrinth.com/mod/applied-energistics-2-wireless-terminals/versions)

Applied Energistics 2 Wireless Terminals
========================================
This is a Neoforge port
of [Wireless Crafting Terminal](https://www.curseforge.com/minecraft/mc-mods/wireless-crafting-terminal),
[Wireless Pattern Terminal](https://www.curseforge.com/minecraft/mc-mods/wireless-pattern-terminal),
[Wireless Interface Terminal](https://www.curseforge.com/minecraft/mc-mods/wireless-interface-terminal),
[Wireless Fluid Terminal](https://www.curseforge.com/minecraft/mc-mods/wireless-fluid-terminal) and
[Wireless Terminal Library](https://www.curseforge.com/minecraft/mc-mods/ae2wtlib)

It features several wireless versions of Applied Energistics 2 terminals with support for a Quantum Bridge Card,
which allows terminals to work from everywhere and even across dimensions.

It also adds a wireless universal terminal which has all wireless terminals in one item

## Missing Features / Known Issues

- Displaying Trinkets in wireless crafting terminal

## License

* Applied Energistics 2 Wireless Terminals
  - (c) 2021 Mari_023
  - [![License](https://img.shields.io/badge/License-MIT-red.svg?style=flat-square)](http://opensource.org/licenses/MIT)

* Textures
  - (c) 2021, [Ridanisaurus Rid](https://github.com/Ridanisaurus/)
  - [![License](https://img.shields.io/badge/License-CC%20BY--NC--SA%203.0-yellow.svg?style=flat-square)](https://creativecommons.org/licenses/by-nc-sa/3.0/)
  - based on [Applied Energistics 2](https://github.com/AppliedEnergistics/Applied-Energistics-2) Textures
    - (c) 2020, [Ridanisaurus Rid](https://github.com/Ridanisaurus/), (c) 2013 - 2020 AlgorithmX2 et al
    - [![License](https://img.shields.io/badge/License-CC%20BY--NC--SA%203.0-yellow.svg?style=flat-square)](https://creativecommons.org/licenses/by-nc-sa/3.0/)

* Text and Translations
  - [![License](https://img.shields.io/badge/License-No%20Restriction-green.svg?style=flat-square)](https://creativecommons.org/publicdomain/zero/1.0/)

## Translations

There are two sets of localisation files:

The main mod, in `src/main/resources/assets/ae2wtlib/lang/en_us.json`

https://github.com/bayi/AE2WirelessTerminalLibrary/blob/main/src/main/resources/assets/ae2wtlib/lang/en_us.json

And the api mod, in `ae2wtlib_api/src/main/resources/assets/ae2wtlib_api/lang/en_us.json`

https://github.com/Mari023/AE2WirelessTerminalLibrary/blob/main/ae2wtlib_api/src/main/resources/assets/ae2wtlib_api/lang/en_us.json

## API

Like ae2, ae2wtlib is available on modmaven
```
repositories {
    maven {
        url = uri("https://modmaven.dev/")
        content {
            includeGroup "appeng"
            includeGroup "de.mari_023"
        }
    }
}
```

ae2wtlib has an api, which is what you should compile against when making integrations with ae2wtlib
```
dependencies {
    implementation("de.mari_023:ae2wtlib_api:VERSION")
    runtimeOnly("de.mari_023:ae2wtlib:VERSION")
}
```

If you want to add your own terminals, you should jar-in-jar ae2wtlib_api
```
dependencies {
    jarJar(implementation("de.mari_023:ae2wtlib_api:VERSION"))
    runtimeOnly("de.mari_023:ae2wtlib:VERSION")
}
```

If you want to add upgrades to ae2wtlib terminals, you can use `UpgradeHelper#addUpgradeToAllTerminals`
```java
addUpgradeToAllTerminals(upgradeCard, maxSupported);
// use 0 to add the maximum amount of upgrades the terminal can fit
addUpgradeToAllTerminals(upgradeCard, 0);
```

### Adding terminals

For a simple example of a wireless terminal, you can look at the Wireless Pattern Access Terminal.
The related classes are `ItemWAT`, `WATMenu`, `WATMenuHost` and `WATScreen`

For registration, you need to listen to the `AddTerminalEvent` (it isn't actually an EventBus event)
```java
AddTerminalEvent.register(event -> event.builder(
        ...
).addTerminal())
```

The builder has some additional methods for overriding properties that are inferred from other attributes, like `WTDefinitionBuilder#translationKey` 
