Permissions
===


When adding permissions, mind the case : creeperheal.* is different from CreeperHeal.*


CreeperHeal.* : Master permission, gives access to everything, to be given only to ops. Does not include the "warn" permissions, to avoid message spamming.
### Command permissions
* CreeperHeal.admin - - - Gives access to every commands (including configuration ones)
* CreeperHeal.heal - - - Gives access to the /ch heal command and all its derivative (healBurnt, healNear)
* CreeperHeal.healNear.self - - - Gives access the the healNear command, (heal the explosions nearby) but only around yourself
* CreeperHeal.healNear.all - - - Allows to healNear <somebody>

### Anti-griefing
#### Bypass
* Creeperheal.bypass.* - - - Gives permission to all the bypass permissions
* CreeperHeal.bypass.place-lava  - - - Gives permission to place lava in worlds where it is blocked
* CreeperHeal.bypass.place-tnt
* CreeperHeal.bypass.place-blacklist - - - Gives permission to place blacklisted blocks
* CreeperHeal.bypass.pvp - - - Allows you to hit other players in worlds wher it is forbidden (by CreeperHeal - you may have other pvp control plugins)
* CreeperHeal.bypass.spawnEggs - - - Allows you to spawn mobs with monster eggs (from creative)
* CreeperHeal.bypass.fire - - - Allows you to start fires with flint and _steel

#### Warn
**CreeperHeal.* doesn't give those permissions, to avoid message spamming !**
* CreeperHeal.warn.* - - - Receive every message that is sent to the log.
* CreeperHeal.warn.place-lava - - - If you have the warn lava setting enabled, then you will receive messages about players placing lava only if you have this permission. 
* CreeperHeal.warn.place-tnt
* CreeperHeal.warn.place-blacklist
* CreeperHeal.warn.pvp
* CreeperHeal.warn.spawnEggs
* CreeperHeal.warn.fire
