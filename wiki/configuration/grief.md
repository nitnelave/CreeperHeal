Grief Configuration Guide
====

Grief prevention is a side feature of CreeperHeal. It can be completely deactivated by setting every grief feature to false in every world, as it is by default. There are only world-wide settings, except for the *log-warnings* option in the main advanced.yml file.

* **prevent-fire-spread**: Prevent the fire from spreading, or from being started by lava.
* **warn**: Send a message to players commiting an infraction, and warn OPs about it.
* **block**: Prevent the player from commiting the infraction.
    * **TNT**: Placing TNT blocks.
    * **PvP**: Hitting (or shooting or throwing a potion) another player.
    * **spawn-eggs**: Using eggs to spawn monsters.
    * **blacklist**: Placing any of the blocks defined in the blacklist.
    * **lava**: Emptying a bucket of lava.
    * **flint-and-steel**: Using flint-and-steel.
* **blacklist**: List of forbidden blocks. Only effective if *warn->blacklist* or *block->blacklist* is set to true.

    The format to enter a block is ID:DATA, with the data being optional. The values are separated by comas. If no data is specified, then all data values are included. For example, to block dirt, all types of wool, normal logs and birch logs : 3, 35, 17:0, 17:2

[Back to the Configuration guide](/wiki/configuration/guide)