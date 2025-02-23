<h1 style="text-align:center;">BoltUX - An Unofficial Bolt Expansion</h1>
<p style="text-align:center;">
    <img alt="GitHub License" src="https://img.shields.io/github/license/Alathra/Template-Gradle-Plugin?style=for-the-badge&color=blue&labelColor=141417">
    <img alt="GitHub Downloads (all assets, all releases)" src="https://img.shields.io/github/downloads/Alathra/Template-Gradle-Plugin/total?style=for-the-badge&labelColor=141417">
    <img alt="GitHub Release" src="https://img.shields.io/github/v/release/Alathra/Template-Gradle-Plugin?include_prereleases&sort=semver&style=for-the-badge&label=LATEST%20VERSION&labelColor=141417">
    <img alt="GitHub Actions Workflow Status" src="https://img.shields.io/github/actions/workflow/status/Alathra/Template-Gradle-Plugin/ci.yml?style=for-the-badge&labelColor=141417">
    <img alt="GitHub Issues or Pull Requests" src="https://img.shields.io/github/issues/Alathra/Template-Gradle-Plugin?style=for-the-badge&labelColor=141417">
    <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/Alathra/Template-Gradle-Plugin?style=for-the-badge&labelColor=141417">
</p>

---

## Description

BoltUX (Bolt User Experience) primarily seeks to provide a GUI interface for [Bolt](https://github.com/pop4959/Bolt) commands and interactions, as well additonal user feedback. At this time it is only supported on Bukkit/Paper servers for Minecraft 1.21.4. This plugin is inspired by projects like [TownMenu](https://github.com/cobrex1/TownyMenu), which provide GUIs for feature-rich, command-based plugins. By providing user interfaces, projects like TownyMenu and BoltUX make it easier for most players to use their parent plugin by abstracting away commands and granular functionality. Since the parent plugin's (Bolt's) commands are retained, more experienced players (or those who prefer commands) can use commands in conjuction with, or in the place of the GUI menus.

### Features


---

## Dependencies/Hooks
### Dependencies
* #### [Bolt](https://github.com/pop4959/Bolt)
### Optional Hooks
* #### [Towny](https://github.com/TownyAdvanced/Towny)
    If Towny is used, BoltUX will automatically use TownyAPI in order to provide Town and Nation members as suggestions in the Add Access Menu. 
* #### [ItemsAdder](https://itemsadder.devs.beer/), [Nexo](https://docs.nexomc.com/), [Oraxen](https://oraxen.com/)
    The above plugins are used to create and manage custom items. BoltUX supports the use of either of them for the optional lock item. If you wish to create a custom lock item using one of these item frameworks, you can define item id for BoltUX to link and use in the [config.yml](https://github.com/Alathra/BoltUX/blob/main/src/main/resources/config.yml).

---

## Permissions
BoltUX contains the following permission nodes:
* ``boltux.admin``
Grants the user the ability to open and use the BoltUX GUI for any protection, regardless of whether the user is the protection owner. It also grants access to the **/boltux getlock** command.

---

## Commands
BoltUX adds the following commands:
* ``/boltux getlock <amount>``
Spawns the specified amount of lock items in the player's inventory, defaults to 1. This command requires the permission node **boltux.admin**.

---

## Configuration

BoltUX can be configured by editing values in the [config.yml](https://github.com/Alathra/BoltUX/blob/main/src/main/resources/config.yml). **BoltUX does not have a reload command because it automatically checks for updates in the config file**. When you make edits to the file the changes will be applied immediately.

---


