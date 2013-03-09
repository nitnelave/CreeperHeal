Advanced Configuration Guide
===

These settings are more delicate to manipulate. Do not change them on a production server without testing first, if you don't know exactly what you're doing.

### Server-wide settings

These settings are in the advanced.yml at the root of your CreeperHeal directory.
* **command-alias**: If the /ch command conflicts with another plugin, you can change it here.
* **lightweight-mode**: Skip some tests. The plugin is slightly faster, but the replacement quality is somehow degraded, especially concerning vines, leaves and falling blocks (sand, gravel). default false.
* **wait-before-burn-again**: Delay (in s) for which a burnt block is immune to fire after replacement. default 240.
* **prevent-block-fall**: If true, blocks that should fall because their support was blown up will not fall. Provides for nicer replacement. defaults true.
* **overwrite-blocks**: If true, the blocks placed in an explosion hole will be dropped when the explosion is replaced.
* **drop-destroyed-blocks**: If enabled, destroyed blocks will be dropped on the ground, with the percent chance defined. default true, 100
* **teleport-when-buried**: If enabled, entities suffocating because of block replacement will be teleported to the surface, or the nearest empty spot.
* **distance-near**: Distance, in blocks, that is considered near when replacing nearby explosions. default 20
* **prevent-chain-reaction**: If enabled, TNT blocks in an explosion will not explode. Default false.
* **obsidian**: Gives the possibility to explode obsidian blocks. If explode is set to true, for every explosion, every obsidian block in the radius will have the defined chance of exploding. default false, 5, 20
* **log-warnings**: When a warning is issued for griefing, log that message to the log file. default true.
* **verbose-level**: Verbosity level of the messages. 1 for standard messages, 0 for critical messages only. default 1
* **debug-messages**: Display debug messages. Not recommended.

### World-specific settings

These settings are in the advanced.yml in your worlds' folder.

* **restrict**: Prevent replacement of some blocks. If use-whitelist is true, then only the blocks listed in the whitelist are replaced, otherwise every block except those listed in the blacklist are replaced. 

    The format to enter a block is ID:DATA, with the data being optional. The values are separated by comas. If no data is specified, then all data values are included. For example, to block dirt, all types of wool, normal logs and birch logs : 3, 35, 17:0, 17:2
* **replace-grass-with-dirt**: If true, then grass blocks that are blown up will be replaced by plain dirt blocks.
* **repair-time-of-day**: Minecraft time at which every explosion and burnt block should be replaced. (-1 to deactivate).

    Sunrise is around 23000, noon 6000, sunset 13000, and midnight 18000. Day (zombies burning) starts at 0.
* **protected-list**: List of block types to protect (in the same format as the restrict lists).
* **factions**: To use with the Factions plugin. If the settings are set to true, the explosions in wilderness or territory are ignored by CreeperHeal (blocks are destroyed normally).

[Back to the Configuration guide](/wiki/configuration/guide)