Basic Configuration Guide
====
### Server-wide configuration

Here are covered the basic settings, in the config.yml file at the root of you CreeperHeal directory. They will be applied once the plugin (or the server) is reloaded.

* **wait-before-heal**: 
   * **explosions** : the delay, in seconds, before an explosion starts getting replaced
    * **fire** : the delay, in seconds, before a burnt block is replaced.
* **block-per-block**
    * **enabled** : If set to false, all the blocks destroyed by one explosion will be replaced at the same time, instead of one at a time. Default to true.
    * **interval** : Is the delay, in ticks (1/20th of a second) between the replacement of each block in an explosion, if *enabled* is true.
* **replace-protected-chests-immediately** : If set to true, chests that are protected by another plugin like LWC or WorldGuard will not explode. To protect all chests from exploding, add the chest id (54) to the worlds' protected list of blocks.
* **crack-destroyed-brick** : If set to true, destroyed stone bricks will be repaired cracked.
* **replace-grass-with-dirt** : If set to true, destroyed grass will be repaired as plain dirt.

***

### World-specific configuration

These settings are in the config.yml in your worlds' folder.

* **Creepers**, **TNT**, **Ghast**, **Wither**, **custom** : Whether you want the damage from Creeper/TNT/Ghast/Wither or "custom" (from other plugins) explosions to be healed.
* **Dragons** : replace blocks destroyed by EnderDragons.
* **Fire** : replace burnt blocks.
* **Enderman** : if true, prevent Endermen from picking up blocks.
* **replace-above-limit-only** : if true, only damage above the *replace-limit* will be replaced.
* **replace-limit** : the limit

[Back to the configuration guide](/wiki/configuration/guide)