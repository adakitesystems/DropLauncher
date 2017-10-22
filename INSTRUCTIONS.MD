# DropLauncher

## Instructions

### Installation / Extraction
1. Download the latest **DropLauncher.zip** file from the [Releases](https://github.com/adakitesystems/DropLauncher/releases) page.
2. Extract the .zip file to any directory.
3. Done.

*Note: You are NOT required to install BWAPI or Chaoslauncher.*

### Configuration

DropLauncher needs to know the location of **StarCraft.exe** in order to run the bot. You can set the location by dragging the StarCraft.exe into DropLauncher or by selecting it via the **Edit > Settings** menu option.

*Note: DropLauncher extracts required files to the StarCraft directory in order to run the bot. DropLauncher will attempt to clean up the files after closing the program, but some files may linger. Therefore, you might find it more preferable to make a copy of your StarCraft directory and point DropLauncher to the copy.*

### Downloading / Obtaining a BWAPI Bot

You should be able to use any bot from any site that was created with BWAPI versions 3.7.4 to 4.2.0. This section will describe how to obtain a bot from the Student StarCraft AI Tournament website.

1. Go to the bot download section on the SSCAIT website: [https://sscaitournament.com/index.php?action=scores](https://sscaitournament.com/index.php?action=scores)
2. Choose a bot by clicking on its name. **Remember the race of the bot**. You will need to select its race in DropLauncher before starting the bot.
3. Scroll down the page until you see download links for **binary** and **BWAPI.dll**. Download both files.

### Playing 1v1 Human vs Bot

1. Start **StarCraft**.
  * You are **NOT** required to use Chaoslauncher to play against the bot. If you would like to use Chaoslauncher, make sure to **DISABLE** the **BWAPI Plugin**. If the BWAPI Plugin is enabled, there is a high chance that your BWAPI instance will clash with the bot's BWAPI instance. If you still would like to enable the BWAPI Plugin, it is recommended to put the bot on another computer/laptop or in a virtual machine.
  * You should be able to use mca64launcher, Chaoslauncher (if BWAPI is disabled), or any other launcher when playing against the bot with DropLauncher.
2. Host a **Melee** using **Local Area Network (UDP)**.
3. Run **DropLauncher.exe** if you haven't already.
4. Drag-n-drop the bot files into DropLauncher or select them using the **File > Select Bot Files...** menu option.
5. Select the bot's race in DropLauncher.
6. Click **Join Game** in DropLauncher.
7. Start the game using StarCraft.
8. Good luck, have fun!
