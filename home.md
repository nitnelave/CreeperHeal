CreeperHeal
=======

[Project page][project]

Protects your server against Creepers, TNT, and griefers! 

CreeperHeal is more than a griefing protection plugin : it repairs the damage done by explosions (Creepers, TNT, etc...) and fire, in a natural way! Stay a while after a Creeper made a hole, and you will see the terrain pop back slowly, block per block, exactly as it was before the explosion! That way, your players will still be afraid of Creepers, but your server will continue looking nice!
* **Natural healing of the terrain after an explosion**
* **Fully customisable**
* **Easy installation and configuration**
* **Anti-Griefing features**

Here's a sample [fan-made video][video].

NOTE : This is not an official video, but one made by a fan. An official one will be posted in a while.

**Before reporting a bug, please check the [FAQ](/wiki/FAQ). To report a bug, or make a suggestion, use the [Tickets][tickets] page on BukkitDev.**


If you like what I do, please [![Donate button](https://www.paypal.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=5DSJCTVN7JBW4)

Configuration
-------
* [Configuration Guide](configuration-guide).
* [Default Config files](https://github.com/nitnelave/CreeperHeal/blob/master/src/config.yml). 

Installation
-------
Very simple! Just drop the jar in your plugins folder, and you are good to go! Default configuration is perfectly fine for any server, but feel free to customize it to your liking.

Features
-----
*    Entirely customisable, from the time it takes to heal the explosions to the messages displayed.
*    Restores the land exactly as it was before.
*    Support for Creepers, TNT, Dragons, Endermen, Wither, and more!
*    Compatible with several plugins, such as WorldGuard, Factions, LWC or Lockette
*    Complete Anti-griefing set of features : block lava, tnt, any block, pvp, monster eggs, fire, fire spread, etc...

[Full list of features](features).

Permissions
------
Supports native Bukkit perms and bPermissions.

[List of Permissions](permissions).

Commands
----
All commands can be accessed via the /CreeperHeal command, with a default alias of /ch (this can be changed in the advanced config).
See the [Command List](commands).
   * /CreeperHeal (or/ch) help : help menu.
   * /ch heal (world): Heals all explosions and burnt blocks. Defaults to the caller's world.
   * /ch healBurnt (world): Heal all burnt blocks.
   * /ch [creeper|tnt|fire|ghast|magical] \(on|off) (world) : toggles the creeper (or tnt, fire...) explosion replacement (can be used with on/off)
   * /ch interval [seconds] : Sets the interval before an explosion is replaced to x seconds
   * /ch burnInterval [seconds] : Sets the interval before a block burnt is replaced

[project]: http://dev.bukkit.org/server-mods/creeperheal-nitnelave
[tickets]: http://dev.bukkit.org/server-mods/creeperheal-nitnelave/tickets
[video]: http://www.youtube.com/watch?v=H3GReOROOZA