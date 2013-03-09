Configuration Guide
====
A new configuration version has been introduced in version 6.0. The file organization is different and clearer:

In the CreeperHeal folder, there are now 4 files:
* **config.yml** : Basic settings for CreepeHeal (replacement delays, etc...)
* **advanced.yml** : Advanced settings (interaction with other plugins, replacement options...)
* **messages.properties** : Message templates for grief warning and other player messages.
* **log.txt** : (created when necessary) Logs the warning messages.

There is also a folder for each of the worlds, with 3 files in each, for the world-specific settings :
* **config.yml** : Basic world setttings (which explosions are replaced...)
* **advanced.yml** : Advanced world settings (protected/blacklisted blocks, etc...)
* **grief.yml** : Grief control options.

***
#### [Basic Configuration Guide](/wiki/configuration/basic)
#### [Advanced Configuration Guide](/wiki/configuration/advanced)
#### [Grief Configuration Guide](/wiki/configuration/grief)
