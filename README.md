# DropLauncher

## Version 0.3.24a has been released!

[Download](https://github.com/adakitesystems/DropLauncher/releases)

## Description
Designed to be a simple tool to aid players in setting up and playing 1v1 StarCraft: Brood War games against any [BWAPI bot](https://github.com/bwapi/bwapi) using Local Area Network (UDP) as the connection type. It is recommended to make a copy your StarCraft directory and use that copy for DropLauncher.

## Licenses
* DropLauncher: [GNU Affero General Public License version 3](https://www.gnu.org/licenses/agpl-3.0.en.html)
* bwheadless.exe: [CC0 1.0 Universal](https://github.com/tscmoo/bwheadless/blob/master/LICENSE)

## Supported Software
* StarCraft: Brood War version **1.16.1**
* BWAPI versions **3.7.4** to **4.2.0**
* BWAPI bot types: **module (\*.dll)**, **client (\*.exe, \*.jar)**

## Current Features
* No installation of DropLauncher required (just extract to any directory)
* No installation of BWAPI required
* 1v1 (Human vs Bot) Melee over UDP LAN
* Drag and drop bot file loading
* Auto-eject bot after game has ended
* Auto-connect bot to game lobby after eject

## TODO / Future Features
* Bot vs bot automation
* Bot profile saving/loading
* Download/update bot from SSCAIT
* Display bot's StarCraft window

## Third-party Libraries/Applications
* [bwheadless.exe](https://github.com/tscmoo/bwheadless): starts a separate StarCraft instance and injects the BWAPI bot
* [commons-io-2.5.jar](https://commons.apache.org/proper/commons-io/): Apache's Commons IO library
* [zip4j_1.3.2.jar](http://www.lingala.net/zip4j/): zip4j library

## FAQ

Q: How do I install DropLauncher?<br/>
A: No installation required. Just download the latest *.zip file on the [releases page](https://github.com/adakitesystems/DropLauncher/releases) and extract it to any directory.

Q: Do I need to install anything?<br/>
A: No. DropLauncher is designed to include all required runtime files excluding StarCraft: Brood War.

Q: How do I use DropLauncher to play against a bot?<br/>
A: After downloading and extracting DropLauncher:
* Create a Melee game over Local Area Network (UDP) in StarCraft.
* Run **DropLauncher.exe**.
* Load a bot by **dragging all bot files (including the BWAPI.dll)** into DropLauncher or by using the **File -> Select bot files...** menu option.
* Press the **Start** button and the bot should connect to your game.
* More detailed instructions are provided in the **Instructions.html** file included with DropLauncher.

Q: How do I launch StarCraft when playing against a bot?<br/>
A: However you want! As long as you can create a Melee game over LAN UDP. You can launch StarCraft using any launcher (e.g. mca64launcher, Chaoslauncher) or none at all.

Q: Why does the bot freeze the game when the game starts?<br/>
A: Some bots use the [BWTA library](https://bitbucket.org/auriarte/bwta2) which is a terrain analyzer. This library allows the bot to read map information before the game starts. Depending on the size of the map and the speed of the computer, this may take up to a few minutes to generate a cache file for the currently selected map. If a cache file for the map is already present, the bot will load the map data almost instantaneously and should not freeze at the start of the game. These cache files are saved to **StarCraft/bwapi-data/BWTA/** and/or **StarCraft/bwapi-data/BWTA2/**.

## Screenshots

![alt tag](http://i.imgur.com/PnluPMg.png)

![alt tag](http://i.imgur.com/UzXIoOP.png)

## Known Issues

* If StarCraft is installed to **C:\Program Files\StarCraft\** or any write-protected directory, you will need to start DropLauncher with administrative privileges.
* If you start the bot *before* hosting a game, you may get the following error message: `"Unable to initialize network provider."`
